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

import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import com.uber.artist.api.JavaTrait
import javax.lang.model.element.Modifier

@AutoService(JavaTrait::class)
class JavaScrollableTrait : JavaTrait {
  override fun generateFor(
      type: TypeSpec.Builder,
      initMethod: MethodSpec.Builder,
      rClass: ClassName,
      sourceType: String) {

    // ScrollView overrides
    if (sourceType.contains("ScrollView")) {
      addRxBindingApiForSettable(type, JavaSettableApi(
          JavaRxBindingInfo(JavaRxTypeNames.Rx.RxNestedScrollView,
              "scrollChangeEvents",
              """@return an observable of scroll-change events for this NestedScrollView.
    """),
          ClassName.bestGuess("OnScrollChangeListener"),
          "setOnScrollChangeListener",
          JavaRxTypeNames.Rx.ViewScrollChangeEvent,
          MethodSpec.methodBuilder("accept")
              .addModifiers(Modifier.PUBLIC)
              .addParameter(JavaRxTypeNames.Rx.ViewScrollChangeEvent, "event")
              .addStatement("l.onScrollChange($sourceType.this, event.getScrollX(), event" +
                  ".getScrollY" +
                  "(), event.getOldScrollX(), event.getOldScrollY())")))
    }

    // RecyclerView overrides
    if (sourceType.contains("RecyclerView")) {
      addRxBindingApiForAdditive(type, JavaAdditiveApi(
          JavaRxBindingInfo(JavaRxTypeNames.Rx.RxRecyclerView,
              "scrollEvents",
              "@return an observable of scroll events on this RecyclerView"),
          JavaRxTypeNames.Rx.RecyclerViewScrollEvent))
    }
  }
}
