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
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.uber.artist.api.KotlinTrait

@AutoService(KotlinTrait::class)
class KotlinScrollableTrait : KotlinTrait {
    override fun generateFor(
            type: TypeSpec.Builder,
            initMethod: FunSpec.Builder,
            rClass: ClassName,
            sourceType: String) {

        // ScrollView overrides
        if (sourceType.contains("ScrollView")) {
          addRxBindingApiForSettable(type, KotlinSettableApi(
              KotlinRxBindingInfo(KotlinRxTypeNames.Rx.RxNestedScrollView,
                  "scrollChangeEvents",
                  """@return an observable of scroll-change events for this NestedScrollView.
    """),
              ClassName.bestGuess("OnScrollChangeListener"),
              "setOnScrollChangeListener",
              KotlinRxTypeNames.Rx.ViewScrollChangeEvent,
              FunSpec.builder("accept")
                  .addModifiers(KModifier.PUBLIC)
                  .addParameter("event", KotlinRxTypeNames.Rx.ViewScrollChangeEvent)
                  .addStatement("l.onScrollChange(this@$sourceType, event.scrollX(), event.scrollY(), event.oldScrollX(), event.oldScrollY())")))
        }

        // RecyclerView overrides
        if (sourceType.contains("RecyclerView")) {
          addRxBindingApiForAdditive(type, KotlinAdditiveApi(
              KotlinRxBindingInfo(KotlinRxTypeNames.Rx.RxRecyclerView,
                  "scrollEvents",
                  "@return an observable of scroll events on this RecyclerView"),
              KotlinRxTypeNames.Rx.RecyclerViewScrollEvent))
        }
    }
}
