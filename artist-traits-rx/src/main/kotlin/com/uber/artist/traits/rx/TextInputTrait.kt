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

@AutoService(JavaTrait::class)
class TextInputTrait : JavaTrait {
    override fun generateFor(
            type: TypeSpec.Builder,
            initMethod: MethodSpec.Builder,
            rClass: ClassName,
            sourceType: String) {

        // TextChanges
        addRxBindingApiForAdditive(type, JavaAdditiveApi(
                JavaRxBindingInfo(JavaRxTypeNames.Rx.RxTextView,
                        "textChanges",
                        """@return an observable of character sequences for text changes on this TextView."""),
                ClassName.get(CharSequence::class.java)))
    }
}
