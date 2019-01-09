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
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.uber.artist.api.KotlinTrait
import com.uber.artist.api.KotlinTypeNames

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec

@AutoService(KotlinTrait::class)
class KotlinCheckableTrait : KotlinTrait {
    override fun generateFor(
        type: TypeSpec.Builder,
        initMethod: FunSpec.Builder,
        rClass: ClassName,
        baseType: String) {

        val isTextView = baseType.endsWith("TextView")

        if (isTextView) {
            type.addProperty(PropertySpec.builder(
                    "checkedChanges",
                    KotlinRxTypeNames.Rx.BehaviorRelay.parameterizedBy(BOOLEAN).copy(nullable = true),
                    KModifier.PRIVATE)
                    .addAnnotation(KotlinTypeNames.Annotations.Nullable)
                    .mutable()
                    .initializer("null")
                    .build())
            type.addFunction(FunSpec.builder("ensureCheckedChanges")
                    .addModifiers(KModifier.PRIVATE)
                    .beginControlFlow("if (checkedChanges == null)")
                    .addStatement("checkedChanges = %T.create()", KotlinRxTypeNames.Rx.BehaviorRelay)
                    .endControlFlow()
                    .build())
            type.addFunction(FunSpec.builder("checkedChanges")
                    .addKdoc("""@return an observable of booleans representing the checked state of this view.
    """)
                    .addModifiers(KModifier.PUBLIC, KModifier.OPEN)
                    .returns(KotlinRxTypeNames.Rx.Observable.parameterizedBy(BOOLEAN))
                    .addStatement("ensureCheckedChanges()")
                    .addStatement("return checkedChanges!!.hide()")
                    .build())
            type.addFunction(FunSpec.builder("setChecked")
                    .addModifiers(KModifier.PUBLIC, KModifier.OPEN, KModifier.OVERRIDE)
                    .addParameter("value", BOOLEAN)
                    .addStatement("super.setChecked(value)")
                    .addStatement("ensureCheckedChanges()")
                    .addStatement("checkedChanges!!.accept(value)")
                    .build())
        } else {
          addRxBindingApiForSettable(type, KotlinSettableApi(
              KotlinRxBindingInfo(KotlinRxTypeNames.Rx.RxCompoundButton,
                  "checkedChanges",
                  """@return an observable of booleans representing the checked state of this view.
    """),
              ClassName.bestGuess("OnCheckedChangeListener"),
              "setOnCheckedChangeListener",
              BOOLEAN,
              FunSpec.builder("accept")
                  .addModifiers(KModifier.PUBLIC)
                  .addParameter("isChecked", BOOLEAN)
                  .addStatement("l.onCheckedChanged(this@$baseType, isChecked)"),
              true,
              CodeBlock.of("%T.createDefault(isChecked())\n", KotlinRxTypeNames.Rx.BehaviorRelay),
              setListenerMethodAnnotations = listOf(KotlinTypeNames.Annotations.Nullable)
          ))
        }
    }
}
