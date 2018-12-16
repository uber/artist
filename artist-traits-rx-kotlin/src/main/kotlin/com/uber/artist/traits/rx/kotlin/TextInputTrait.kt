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

package com.uber.artist.traits.rx.kotlin

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.uber.artist.api.KotlinTrait
import com.uber.artist.traits.rx.KotlinAdditiveApi
import com.uber.artist.traits.rx.KotlinRxBindingInfo
import com.uber.artist.traits.rx.KotlinRxTypeNames
import com.uber.artist.traits.rx.addRxBindingApiForAdditive

@AutoService(KotlinTrait::class)
class TextInputTrait : KotlinTrait {
  override fun generateFor(
      type: TypeSpec.Builder,
      initMethod: FunSpec.Builder,
      rClass: ClassName,
      sourceType: String) {

    // TextChanges
    addRxBindingApiForAdditive(type, KotlinAdditiveApi(
        KotlinRxBindingInfo(KotlinRxTypeNames.Rx.RxTextView,
            "textChanges",
            """@return an observable of character sequences for text changes on this TextView."""),
        CharSequence::class.asClassName()))
  }
}
