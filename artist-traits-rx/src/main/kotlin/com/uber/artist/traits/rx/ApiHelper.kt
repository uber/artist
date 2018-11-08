/*
 * Copyright (C) 2017. Uber Technologies
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

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.uber.artist.api.TypeNames
import com.uber.artist.traits.rx.config.ArtistRxConfigService
import javax.lang.model.element.Modifier

data class RxBindingInfo(
        val className: ClassName,
        val methodName: String,
        val methodDoc: String
)

data class SettableApi(
        val rxBindingInfo: RxBindingInfo,
        val listenerType: TypeName,
        val listenerMethod: String,
        val observableType: TypeName,
        val listenerImpl: MethodSpec.Builder,
        val isStateful: Boolean = false,
        val relayInitializer: CodeBlock? = null,
        val setterCaveats: String? = null,
        val isUViewOverride: Boolean = false,
        val setListenerMethodAnnotations: List<ClassName> = emptyList())

data class AdditiveApi(
        val rxBindingInfo: RxBindingInfo,
        val observableType: TypeName,
        val isUViewOverride: Boolean = false
)

fun TypeName.irrelevantIfObject(): TypeName {
    val artistRxConfig = ArtistRxConfigService.newInstance().getArtistRxConfig()
    return if (this == TypeName.OBJECT.box()) artistRxConfig.rxBindingSignalEventTypeName() else this
}

fun addRxBindingApiForAdditive(type: TypeSpec.Builder, api: AdditiveApi) {
    val artistRxConfig = ArtistRxConfigService.newInstance().getArtistRxConfig()
    type.addMethod(MethodSpec.methodBuilder(api.rxBindingInfo.methodName)
            .addJavadoc("${api.rxBindingInfo.methodDoc}\n")
            .apply {
                if (api.isUViewOverride) {
                    addAnnotation(Override::class.java)
                }
            }
            .addModifiers(Modifier.PUBLIC)
            .returns(ParameterizedTypeName.get(RxTypeNames.Rx.Observable, api.observableType.irrelevantIfObject()))
            .addCode(CodeBlock.builder()
                    .add("return \$T.${api.rxBindingInfo.methodName}(this)", api.rxBindingInfo.className)
                    .apply {
                        if (api.observableType == TypeName.OBJECT.box()) {
                            artistRxConfig.processRxBindingSignalEvent(this)
                        }
                        if (api.rxBindingInfo.methodName != "attachEvents") {
                            // Safe to call, otherwise it'd be a recursive stack overflow
                            artistRxConfig.processRxBindingStream(this, api.observableType.irrelevantIfObject())
                        }
                        add(";")
                    }
                    .build())
            .build())
}

fun addRxBindingApiForSettable(type: TypeSpec.Builder, api: SettableApi, isDebug: Boolean = true) {
    val artistRxConfig = ArtistRxConfigService.newInstance().getArtistRxConfig()
    val rxBindingClassName = api.rxBindingInfo.className
    val rxBindingMethod = api.rxBindingInfo.methodName
    val rxBindingMethodDoc = api.rxBindingInfo.methodDoc
    val isInitting = "${api.rxBindingInfo.methodName}IsInitting"
    val disposable = "${api.rxBindingInfo.methodName}Disposable"

    // clicksInitting
    type.addField(TypeName.BOOLEAN, isInitting, Modifier.PRIVATE)

    // internal relay
    type.addField(
            FieldSpec.builder(ParameterizedTypeName.get(if (api.isStateful) RxTypeNames.Rx.BehaviorRelay else RxTypeNames.Rx.PublishRelay,
                    api.observableType.irrelevantIfObject()),
            rxBindingMethod,
            Modifier.PRIVATE)
            .addAnnotation(TypeNames.Annotations.Nullable).build())

    type.addField(FieldSpec.builder(RxTypeNames.Rx.Disposable, disposable, Modifier.PRIVATE).addAnnotation(TypeNames.Annotations.Nullable).build())

    val consumer = TypeSpec.anonymousClassBuilder("")
            .addSuperinterface(ParameterizedTypeName.get(RxTypeNames.Rx.Consumer, api.observableType.irrelevantIfObject()))
            .addMethod(api.listenerImpl.addAnnotation(Override::class.java).build())
            .build()

    // Overridden and deprecated setOnClickListener method
    type.addMethod(MethodSpec.methodBuilder(api.listenerMethod)
            .addJavadoc(StringBuilder().apply {
                if (api.setterCaveats != null) {
                    append(api.setterCaveats)
                    append("\n\n")
                }
            }.append("@deprecated Use {@link #$rxBindingMethod()}\n").toString())
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(Override::class.java)
            .addAnnotation(java.lang.Deprecated::class.java)
            .addParameter(
                ParameterSpec.builder(api.listenerType, "l", Modifier.FINAL).apply {
                    api.setListenerMethodAnnotations.forEach {
                        addAnnotation(it)
                    }
                }.build()
            )
            .beginControlFlow("if ($isInitting)")
            .addStatement("$isInitting = false")
            .addStatement("super.${api.listenerMethod}(l)")
            .nextControlFlow("else")
            .beginControlFlow("if ($disposable != null)")
            .addStatement("$disposable.dispose()")
            .addStatement("$disposable = null")
            .endControlFlow()
            .beginControlFlow("if (l != null)")
            .addCode(CodeBlock.builder()
                    .add("$disposable = $rxBindingMethod()")
                    .add(".subscribe(\$L);", consumer)
                    .build())
            .endControlFlow()
            .endControlFlow()
            .build())

    type.addMethod(MethodSpec.methodBuilder(rxBindingMethod)
            .addJavadoc(rxBindingMethodDoc)
            .apply {
                if (api.isUViewOverride) {
                    addAnnotation(Override::class.java)
                }
            }
            .addModifiers(Modifier.PUBLIC)
            .returns(ParameterizedTypeName.get(RxTypeNames.Rx.Observable, api.observableType.irrelevantIfObject()))
            .beginControlFlow("if ($rxBindingMethod == null)")
            .addStatement("$isInitting = true")
            .apply {
                if (api.relayInitializer != null) {
                    addCode("$rxBindingMethod = ", RxTypeNames.Rx.BehaviorRelay)
                    addCode(api.relayInitializer)
                    addCode(";\n")
                } else {
                    addStatement("$rxBindingMethod = \$T.create()",
                            if (api.isStateful) RxTypeNames.Rx.BehaviorRelay else RxTypeNames.Rx.PublishRelay)
                }
            }
            .addCode(CodeBlock.builder()
                    .add("\$T.$rxBindingMethod(this)", rxBindingClassName)
                    .apply {
                        if (api.observableType == TypeName.OBJECT.box()) {
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
                    .add("return $rxBindingMethod.hide()")
                    .apply { artistRxConfig.processRxBindingStream(this, api.observableType.irrelevantIfObject()) }
                    .add(";")
                    .build())
            .build())
}
