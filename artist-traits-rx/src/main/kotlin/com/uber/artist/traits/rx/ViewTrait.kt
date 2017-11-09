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
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.uber.artist.api.Trait
import com.uber.artist.api.TypeNames
import com.uber.artist.traits.rx.config.ArtistRxConfigService
import javax.lang.model.element.Modifier

open class ViewTrait : Trait {
    private val artistRxConfig by lazy { ArtistRxConfigService.newInstance().getArtistRxConfig() }

    override fun generateFor(
            type: TypeSpec.Builder,
            initMethod: MethodSpec.Builder,
            rClass: ClassName,
            sourceType: String) {

        // Visibility convenience methods
        type.addMethod(addVisibilityConvenienceMethods("visible"))
        type.addMethod(addVisibilityConvenienceMethods("gone"))
        type.addMethod(addVisibilityConvenienceMethods("invisible"))

        clicks(type, sourceType)
        longClicks(type, sourceType)
        layoutChanges(type)
    }

    open fun addVisibilityConvenienceMethods(type: String): MethodSpec {
        return MethodSpec.methodBuilder("is${type.capitalize()}")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addStatement("return getVisibility() == \$T.${type.toUpperCase()}", TypeNames.Android.View)
                .build()
    }

    open fun clicks(type: TypeSpec.Builder, sourceType: String) {
        addRxBindingApiForSettable(type, SettableApi(
                RxBindingInfo(RxTypeNames.Rx.RxView,
                        "clicks",
                        """@return an Observable of click events. The emitted value is unspecified and should only be used as notification.
    """),
                ClassName.bestGuess("OnClickListener"),
                "setOnClickListener",
                TypeName.OBJECT.box(),
                MethodSpec.methodBuilder("accept")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(artistRxConfig.rxBindingSignalEventTypeName(), "ignored")
                        .addStatement("l.onClick($sourceType.this)")
        ))
    }

    open fun longClicks(type: TypeSpec.Builder, sourceType: String) {
        addRxBindingApiForSettable(type, SettableApi(
                RxBindingInfo(RxTypeNames.Rx.RxView,
                        "longClicks",
                        """@return an Observable of longclick events. The emitted value is unspecified and should only be used as notification.
    """),
                ClassName.bestGuess("OnLongClickListener"),
                "setOnLongClickListener",
                TypeName.OBJECT.box(),
                MethodSpec.methodBuilder("accept")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(artistRxConfig.rxBindingSignalEventTypeName(), "ignored")
                        .addStatement("l.onLongClick($sourceType.this)")
        ))
    }

    open fun layoutChanges(type: TypeSpec.Builder) {
        // Attach state changes observable
        addRxBindingApiForAdditive(type, AdditiveApi(
                RxBindingInfo(
                        RxTypeNames.Rx.RxView,
                        "layoutChanges",
                        "@return an observable which emits on layout changes. The emitted value is " +
                                "unspecified and should only be used as notification."),
                TypeName.OBJECT.box()))
    }
}
