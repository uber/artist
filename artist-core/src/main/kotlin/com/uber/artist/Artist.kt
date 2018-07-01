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

package com.uber.artist

import com.google.common.annotations.VisibleForTesting
import com.google.googlejavaformat.java.Formatter
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import com.uber.artist.api.Trait
import com.uber.artist.api.TraitService
import com.uber.artist.api.TypeNames
import com.uber.artist.api.ViewStencil
import com.uber.artist.api.ViewStencilService
import java.io.File
import javax.lang.model.element.Modifier

fun generateViewsFor(
        outputDir: File,
        packageName: String,
        viewPackageName: String,
        superinterfaceClassName: String?,
        viewNamePrefix: String,
        formatSource: Boolean) {
    val traits = TraitService.newInstance().get()
    val viewStencilService = ViewStencilService.newInstance()
    val globalTraits = viewStencilService.getGlobalTraits()
    val stencils = viewStencilService.getStencils()
    generateViewsForStencils(stencils, traits, globalTraits, outputDir, packageName,
            viewPackageName, superinterfaceClassName, viewNamePrefix, formatSource)
}

@VisibleForTesting
fun generateViewsForStencils(
        stencils: Set<ViewStencil>,
        traits: Set<Trait>,
        globalTraits: Set<Class<out Trait>>,
        outputDir: File,
        packageName: String,
        viewPackageName: String,
        superinterfaceClassName: String?,
        viewNamePrefix: String,
        formatSource: Boolean) {

    val formatter = Formatter()
    val traitMap: Map<Class<out Trait>, Trait> = traits.associateBy { it.javaClass }

    stencils.forEach {
        it.setGlobalTraits(globalTraits)
        it.setPrefix(viewNamePrefix)

        val spec = generateTypeSpecFor(it, packageName, traitMap, superinterfaceClassName)
        val javaFile = JavaFile.builder(viewPackageName, spec).build()
        if (formatSource) {
            javaFile.writeWithFormattingTo(formatter, outputDir)
        } else {
            javaFile.writeTo(outputDir)
        }
    }
}

private fun generateTypeSpecFor(
        stencil: ViewStencil,
        packageName: String,
        traitMap: Map<Class<out Trait>, Trait>,
        superinterfaceClassName: String?): TypeSpec {
    val rClass = ClassName.get(packageName, "R")
    val typeBuilder = TypeSpec.classBuilder(stencil.name())
            .addModifiers(Modifier.PUBLIC)
            .superclass(stencil.sourceType)

    superinterfaceClassName?.let { typeBuilder.addSuperinterface(superinterface(superinterfaceClassName)) }

    generateConstructorsFor(stencil, typeBuilder, rClass)
    val initMethod = createInitBuilderFor(stencil, typeBuilder)

    stencil.traits()
            .map { traitName -> traitMap[traitName] }
            .forEach { it?.generateFor(typeBuilder, initMethod, rClass, stencil.name()) }

    typeBuilder.addMethod(initMethod.build())
    stencil.typeHook(typeBuilder)
    return typeBuilder.build()
}

private fun createInitBuilderFor(
        stencil: ViewStencil,
        type: TypeSpec.Builder): MethodSpec.Builder {
    val initMethod = MethodSpec.methodBuilder("init")
            .addAnnotation(TypeNames.Annotations.CallSuper)
            .addModifiers(Modifier.PROTECTED)
            .addParameter(ParameterSpec.builder(TypeNames.Android.Context, "context")
                    .build())
            .addParameter(ParameterSpec.builder(TypeNames.Android.AttributeSet, "attrs")
                    .addAnnotation(TypeNames.Annotations.Nullable)
                    .build())
            .addParameter(ParameterSpec.builder(ClassName.INT, "defStyleAttr")
                    .addAnnotation(TypeNames.Annotations.AttrRes)
                    .build())
            .addParameter(ParameterSpec.builder(ClassName.INT, "defStyleRes")
                    .addAnnotation(TypeNames.Annotations.StyleRes)
                    .build())

    stencil.initMethodHook(type, initMethod)

    return initMethod
}

private fun generateConstructorsFor(stencil: ViewStencil, type: TypeSpec.Builder, rClass: ClassName) {
    val count = stencil.constructorCount
    for (i in 1..count) {
        when (i) {
            1 -> // Context constructor
                type.addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(TypeNames.Android.Context, "context")
                                .build())
                        .addCode(constructorBlock(stencil, rClass, count, i))
                        .build())
            2 -> // Context, AttributeSet constructor
                type.addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(TypeNames.Android.Context, "context")
                                .build())
                        .addParameter(ParameterSpec.builder(TypeNames.Android.AttributeSet, "attrs")
                                .addAnnotation(TypeNames.Annotations.Nullable)
                                .build())
                        .addCode(constructorBlock(stencil, rClass, count, i))
                        .build())
            3 -> // Context, AttributeSet, defStyleAttr constructor
                type.addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(TypeNames.Android.Context, "context")
                                .build())
                        .addParameter(ParameterSpec.builder(TypeNames.Android.AttributeSet, "attrs")
                                .addAnnotation(TypeNames.Annotations.Nullable)
                                .build())
                        .addParameter(ParameterSpec.builder(ClassName.INT, "defStyleAttr")
                                .addAnnotation(TypeNames.Annotations.AttrRes)
                                .build())
                        .addCode(constructorBlock(stencil, rClass, count, i))
                        .build())
            4 -> // Context, AttributeSet, defStyleAttr, defStyleRes constructor
                type.addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(TypeNames.Android.Context, "context")
                                .build())
                        .addParameter(ParameterSpec.builder(TypeNames.Android.AttributeSet, "attrs")
                                .addAnnotation(TypeNames.Annotations.Nullable)
                                .build())
                        .addParameter(ParameterSpec.builder(ClassName.INT, "defStyleAttr")
                                .addAnnotation(TypeNames.Annotations.AttrRes)
                                .build())
                        .addParameter(ParameterSpec.builder(ClassName.INT, "defStyleRes")
                                .addAnnotation(TypeNames.Annotations.StyleRes)
                                .build())
                        .addAnnotation(AnnotationSpec.builder(TypeNames.Annotations.TargetApi)
                                .addMember("value", "\$T.\$L.\$L",
                                        ClassName.get("android.os", "Build"),
                                        "VERSION_CODES",
                                        "LOLLIPOP")
                                .build())
                        .addCode(constructorBlock(stencil, rClass, count, i))
                        .build())
        }
    }
}

private fun constructorBlock(stencil: ViewStencil, rClass: ClassName, total: Int, currentIndex: Int): CodeBlock {
    val builder = CodeBlock.builder()
    if (currentIndex == total || currentIndex == 3) {
        builder.addStatement(superConstructorStatement(currentIndex))
        builder.addStatement(initStatement(currentIndex))
    } else {
        builder.add(fallthroughConstructorStatement(stencil, rClass, currentIndex))
    }
    return builder.build()
}

private fun superConstructorStatement(count: Int): String {
    when (count) {
        1 -> return "super(context)"
        2 -> return "super(context, attrs)"
        3 -> return "super(context, attrs, defStyleAttr)"
        4 -> return "super(context, attrs, defStyleAttr, defStyleRes)"
    }
    throw IllegalArgumentException()
}

private fun fallthroughConstructorStatement(stencil: ViewStencil, rClass: ClassName, count: Int): CodeBlock {
    when (count) {
        1 -> return CodeBlock.of("this(context, null);\n")
        2 -> {
            if (stencil.defaultAttrRes != null) {
                if ((stencil.defaultAttrRes as String).startsWith(prefix = "android.R")) {
                    return CodeBlock.of("this(context, attrs, ${stencil.defaultAttrRes});\n")
                } else {
                    return CodeBlock.of("this(context, attrs, \$T.attr.${stencil.defaultAttrRes});\n", rClass)
                }
            } else {
                return CodeBlock.of("this(context, attrs, 0);\n")
            }
        }
        3 -> return CodeBlock.of("this(context, attrs, defStyleAttr, 0);\n")
    }
    throw IllegalArgumentException(count.toString())
}

private fun initStatement(count: Int): String {
    when (count) {
        1 -> return "init(context, null, 0, 0)"
        2 -> return "init(context, attrs, 0, 0)"
        3 -> return "init(context, attrs, defStyleAttr, 0)"
        4 -> return "init(context, attrs, defStyleAttr, defStyleRes)"
    }
    throw IllegalArgumentException()
}

private fun superinterface(className: String): ClassName {
    val packageName = className.substring(0, className.lastIndexOf('.'))
    val simpleName = className.substring(className.lastIndexOf('.') + 1)
    return ClassName.get(packageName, simpleName)
}
