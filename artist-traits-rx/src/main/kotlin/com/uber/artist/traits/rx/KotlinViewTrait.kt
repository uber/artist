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

import com.google.auto.service.AutoService
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.uber.artist.api.KotlinTrait
import com.uber.artist.api.KotlinTypeNames
import com.uber.artist.traits.rx.config.KotlinArtistRxConfigService
import io.reactivex.Observable
import java.lang.reflect.Type

@AutoService(KotlinTrait::class)
open class KotlinViewTrait : KotlinTrait {
  private val artistRxConfig by lazy { KotlinArtistRxConfigService.newInstance().getArtistRxConfig() }

  override fun generateFor(
      type: TypeSpec.Builder,
      initMethod: FunSpec.Builder,
      rClass: ClassName,
      sourceType: String) {

    clicks(type, sourceType)
    longClicks(type, sourceType)
    layoutChanges(type)

  }

  open fun clicks(type: TypeSpec.Builder, sourceType: String) {
    addRxBindingApiForSettable(type, KotlinSettableApi(
        KotlinRxBindingInfo(KotlinRxTypeNames.Rx.RxView,
            "clicks",
            """@return an Observable of click events. The emitted value is unspecified and should only be used as notification.
    """),
        ClassName.bestGuess("android.view.View.OnClickListener"),
        "setOnClickListener",
        KotlinTypeNames.Java.Object,
        FunSpec.builder("accept")
            .addParameter("ignored", artistRxConfig.rxBindingSignalEventTypeName())
            .addStatement("l.onClick(this@$sourceType)")
    ))
  }

  open fun longClicks(type: TypeSpec.Builder, sourceType: String) {
    addRxBindingApiForSettable(type, KotlinSettableApi(
        KotlinRxBindingInfo(KotlinRxTypeNames.Rx.RxView,
            "longClicks",
            """@return an Observable of longclick events. The emitted value is unspecified and should only be used as notification.
    """),
        ClassName.bestGuess("android.view.View.OnLongClickListener"),
        "setOnLongClickListener",
        KotlinTypeNames.Java.Object,
        FunSpec.builder("accept")
            .addParameter("ignored", artistRxConfig.rxBindingSignalEventTypeName())
            .addStatement("l.onLongClick(this@$sourceType)")
    ))
  }

  open fun layoutChanges(type: TypeSpec.Builder) {
    // Attach state changes observable
    addRxBindingApiForExtension(type, KotlinAdditiveApi(
        KotlinRxBindingInfo(
            KotlinRxTypeNames.Rx.RxView,
            "layoutChanges",
            "@return an observable which emits on layout changes. The emitted value is " +
                "unspecified and should only be used as notification."),
        KotlinTypeNames.Java.Object))
  }
}
