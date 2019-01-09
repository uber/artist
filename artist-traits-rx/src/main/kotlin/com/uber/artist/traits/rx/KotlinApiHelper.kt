/*
 * Copyright (C) 2018. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uber.artist.traits.rx

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.uber.artist.traits.rx.config.KotlinArtistRxConfigService

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.uber.artist.api.KotlinTypeNames

data class KotlinRxBindingInfo(
        val className: ClassName,
        val methodName: String,
        val methodDoc: String
)

data class KotlinSettableApi(
        val rxBindingInfo: KotlinRxBindingInfo,
        val listenerType: TypeName,
        val listenerMethod: String,
        val observableType: TypeName,
        val listenerImpl: FunSpec.Builder,
        val isStateful: Boolean = false,
        val relayInitializer: CodeBlock? = null,
        val setterCaveats: String? = null,
        val isUViewOverride: Boolean = false,
        val setListenerMethodAnnotations: List<ClassName> = emptyList())

data class KotlinAdditiveApi(
        val rxBindingInfo: KotlinRxBindingInfo,
        val observableType: TypeName,
        val isUViewOverride: Boolean = false
)

private fun TypeName.irrelevantIfObject(): TypeName {
    val artistRxConfig = KotlinArtistRxConfigService.newInstance().getArtistRxConfig()
    return if (this == KotlinTypeNames.Java.Object) artistRxConfig.rxBindingSignalEventTypeName() else this
}

fun addRxBindingApiForAdditive(type: TypeSpec.Builder, api: KotlinAdditiveApi) {
    val artistRxConfig = KotlinArtistRxConfigService.newInstance().getArtistRxConfig()
    type.addFunction(FunSpec.builder(api.rxBindingInfo.methodName)
            .addKdoc("${api.rxBindingInfo.methodDoc}\n")
            .apply {
                if (api.isUViewOverride) {
                    addModifiers(KModifier.OVERRIDE)
                }
            }
            .addModifiers(KModifier.OPEN)
            .returns(KotlinRxTypeNames.Rx.Observable.parameterizedBy(api.observableType.irrelevantIfObject()))
            .addCode(CodeBlock.builder()
                    .add("return %T.${api.rxBindingInfo.methodName}(this)", api.rxBindingInfo.className)
                    .apply {
                        if (api.observableType == KotlinTypeNames.Java.Object) {
                            artistRxConfig.processRxBindingSignalEvent(this)
                        }
                        if (api.rxBindingInfo.methodName != "attachEvents") {
                            // Safe to call, otherwise it'd be a recursive stack overflow
                            artistRxConfig.processRxBindingStream(this, api.observableType.irrelevantIfObject())
                        }
                    }
                    .add("\n")
                    .build())
            .build())
}

fun addRxBindingApiForSettable(type: TypeSpec.Builder, api: KotlinSettableApi, isDebug: Boolean = true) {
    val artistRxConfig = KotlinArtistRxConfigService.newInstance().getArtistRxConfig()
    val rxBindingClassName = api.rxBindingInfo.className
    val rxBindingMethod = api.rxBindingInfo.methodName
    val rxBindingMethodDoc = api.rxBindingInfo.methodDoc
    val isInitting = "${api.rxBindingInfo.methodName}IsInitting"
    val disposable = "${api.rxBindingInfo.methodName}Disposable"

    // clicksInitting
    type.addProperty(PropertySpec.builder(isInitting, BOOLEAN, KModifier.PRIVATE)
        .mutable()
        .initializer("false")
        .build())

    // internal relay
    val internalRelayTypeName = if (api.isStateful) KotlinRxTypeNames.Rx.BehaviorRelay else KotlinRxTypeNames.Rx.PublishRelay
    type.addProperty(
            PropertySpec.builder(rxBindingMethod, internalRelayTypeName.parameterizedBy(api.observableType.irrelevantIfObject()).copy(nullable = true),
            KModifier.PRIVATE)
            .mutable()
            .initializer("null")
            .build())

    type.addProperty(PropertySpec.builder(disposable, KotlinRxTypeNames.Rx.Disposable.copy(nullable = true), KModifier.PRIVATE)
        .mutable()
        .initializer("null")
        .build())

    val consumer = TypeSpec.anonymousClassBuilder()
            .addSuperinterface(KotlinRxTypeNames.Rx.Consumer.parameterizedBy(api.observableType.irrelevantIfObject()))
            .addFunction(api.listenerImpl.addModifiers(KModifier.OVERRIDE).build())
            .build()

    // Overridden and deprecated setOnClickListener method
    type.addFunction(FunSpec.builder(api.listenerMethod)
            .addKdoc(StringBuilder().apply {
                if (api.setterCaveats != null) {
                    append(api.setterCaveats)
                    append("\n\n")
                }
            }.append("@deprecated Use [$rxBindingMethod]\n").toString())
            .addModifiers(KModifier.FINAL, KModifier.OVERRIDE)
            .addAnnotation(AnnotationSpec.builder(Deprecated::class.java)
                .addMember("message = %S", "Use $rxBindingMethod()")
                .addMember("replaceWith = %T(%S)", ReplaceWith::class.asClassName(), "$rxBindingMethod()")
                .addMember("level = %T.ERROR", DeprecationLevel::class.asClassName())
                .build())
            .addParameter(
                ParameterSpec.builder("l", api.listenerType.copy(nullable = true)).apply {
                    api.setListenerMethodAnnotations.forEach {
                        addAnnotation(it)
                    }
                }.build()
            )
            .beginControlFlow("if ($isInitting)")
            .addStatement("$isInitting = false")
            .addStatement("super.${api.listenerMethod}(l)")
            .nextControlFlow("else")
            .addStatement("$disposable?.dispose()")
            .addStatement("$disposable = null")
            .beginControlFlow("if (l != null)")
            .addCode(CodeBlock.builder()
                    .add("$disposable = $rxBindingMethod()")
                    .add(".subscribe($consumer)\n")
                    .build())
            .endControlFlow()
            .endControlFlow()
            .build())

    type.addFunction(FunSpec.builder(rxBindingMethod)
            .addKdoc(rxBindingMethodDoc)
            .apply {
                if (api.isUViewOverride) {
                    addModifiers(KModifier.OVERRIDE)
                }
            }
            .addModifiers(KModifier.OPEN)
            .returns(KotlinRxTypeNames.Rx.Observable.parameterizedBy(api.observableType.irrelevantIfObject()))
            .beginControlFlow("if ($rxBindingMethod == null)")
            .addStatement("$isInitting = true")
            .apply {
                if (api.relayInitializer != null) {
                    addCode("$rxBindingMethod = ", KotlinRxTypeNames.Rx.BehaviorRelay)
                    addCode(api.relayInitializer)
                } else {
                    addStatement("$rxBindingMethod = %T.create()",
                            if (api.isStateful) KotlinRxTypeNames.Rx.BehaviorRelay else KotlinRxTypeNames.Rx.PublishRelay)
                }
            }
            .addCode(CodeBlock.builder()
                    .add("%T.$rxBindingMethod(this)", rxBindingClassName)
                    .apply {
                        if (api.observableType == KotlinTypeNames.Java.Object) {
                            artistRxConfig.processRxBindingSignalEvent(this)
                        }
                        if (rxBindingMethod.contains("click", true)) {
                            artistRxConfig.processTap(this)
                        }
                    }
                    .addStatement("\n\t.subscribe($rxBindingMethod)")
                    .build())
            .endControlFlow()
            .addCode(CodeBlock.builder()
                    .add("return $rxBindingMethod!!.hide()\n")
                    .apply { artistRxConfig.processRxBindingStream(this, api.observableType.irrelevantIfObject()) }
                    .build())
            .build())
}
