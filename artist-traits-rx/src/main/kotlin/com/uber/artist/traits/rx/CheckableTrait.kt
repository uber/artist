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
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.uber.artist.api.Trait
import javax.lang.model.element.Modifier

class CheckableTrait : Trait {
    override fun generateFor(
            type: TypeSpec.Builder,
            initMethod: MethodSpec.Builder,
            rClass: ClassName,
            baseType: String) {

        val isTextView = baseType.endsWith("TextView")

        if (isTextView) {
            type.addField(
                    ParameterizedTypeName.get(RxTypeNames.Rx.BehaviorRelay, TypeName.BOOLEAN.box()), "checkedChanges",
                    Modifier.PRIVATE)
            type.addMethod(MethodSpec.methodBuilder("ensureCheckedChanges")
                    .addModifiers(Modifier.PRIVATE)
                    .beginControlFlow("if (checkedChanges == null)")
                    .addStatement("checkedChanges = \$T.create()", RxTypeNames.Rx.BehaviorRelay)
                    .endControlFlow()
                    .build())
            type.addMethod(MethodSpec.methodBuilder("checkedChanges")
                    .addJavadoc("""@return an observable of booleans representing the checked state of this view.
    """)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ParameterizedTypeName.get(RxTypeNames.Rx.Observable, TypeName.BOOLEAN.box()))
                    .addStatement("ensureCheckedChanges()")
                    .addStatement("return checkedChanges.hide()")
                    .build())
            type.addMethod(MethodSpec.methodBuilder("setChecked")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override::class.java)
                    .addParameter(TypeName.BOOLEAN, "val")
                    .addStatement("super.setChecked(val)")
                    .addStatement("ensureCheckedChanges()")
                    .addStatement("checkedChanges.accept(val)")
                    .build())
        } else {
            addRxBindingApiForSettable(type, SettableApi(
                    RxBindingInfo(RxTypeNames.Rx.RxCompoundButton,
                            "checkedChanges",
                            """@return an observable of booleans representing the checked state of this view.
    """),
                    ClassName.bestGuess("OnCheckedChangeListener"),
                    "setOnCheckedChangeListener",
                    TypeName.BOOLEAN.box(),
                    MethodSpec.methodBuilder("accept")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(TypeName.BOOLEAN.box(), "isChecked")
                            .addStatement("l.onCheckedChanged($baseType.this, isChecked)"),
                    true,
                    CodeBlock.of("\$T.createDefault(isChecked())", RxTypeNames.Rx.BehaviorRelay)))
        }
    }
}
