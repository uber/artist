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

package com.uber.artist

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import com.uber.artist.api.JavaTrait
import com.uber.artist.api.JavaTraitService
import com.uber.artist.api.JavaViewStencil
import com.uber.artist.api.JavaViewStencilService
import com.uber.artist.api.TypeNames
import java.io.File
import javax.lang.model.element.Modifier

class JavaArtistCodeGenerator : ArtistCodeGenerator<JavaFile, TypeSpec.Builder, MethodSpec.Builder, ClassName, CodeBlock, JavaViewStencil, JavaTrait>() {

  override val viewStencils: Set<JavaViewStencil>
    get() = JavaViewStencilService.newInstance().getStencils()

  override val traits: Set<JavaTrait>
    get() = JavaTraitService.newInstance().get()

  override val globalTraits: Set<Class<out JavaTrait>>
    get() = JavaViewStencilService.newInstance().getGlobalTraits()

  override fun generateFileSpecFor(viewPackageName: String, typeSpecBuilder: TypeSpec.Builder): JavaFile {
    return JavaFile.builder(viewPackageName, typeSpecBuilder.build()).build()
  }

  override fun generateTypeSpecFor(
      stencil: JavaViewStencil,
      rPackageName: String,
      traitMap: Map<Class<out JavaTrait>, JavaTrait>,
      superinterfaceClassName: String?): TypeSpec.Builder {
    val rClass = ClassName.get(rPackageName, "R")
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
    return typeBuilder
  }

  override fun createInitBuilderFor(
      stencil: JavaViewStencil,
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

  override fun generateConstructorsFor(stencil: JavaViewStencil, type: TypeSpec.Builder, rClass: ClassName) {
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

  override fun constructorBlock(stencil: JavaViewStencil, rClass: ClassName, total: Int, currentIndex: Int): CodeBlock {
    val builder = CodeBlock.builder()
    if (currentIndex == total || currentIndex == 3) {
      builder.addStatement(superConstructorStatement(currentIndex))
      builder.addStatement(initStatement(currentIndex))
    } else {
      builder.add(fallthroughConstructorStatement(stencil, rClass, currentIndex))
    }
    return builder.build()
  }

  override fun fallthroughConstructorStatement(stencil: JavaViewStencil, rClass: ClassName, count: Int): CodeBlock {
    when (count) {
      1 -> return CodeBlock.of("this(context, null);\n")
      2 -> {
        return if (stencil.defaultAttrRes != null) {
          if ((stencil.defaultAttrRes as String).startsWith(prefix = "android.R")) {
            CodeBlock.of("this(context, attrs, ${stencil.defaultAttrRes});\n")
          } else {
            CodeBlock.of("this(context, attrs, \$T.attr.${stencil.defaultAttrRes});\n", rClass)
          }
        } else {
          CodeBlock.of("this(context, attrs, 0);\n")
        }
      }
      3 -> return CodeBlock.of("this(context, attrs, defStyleAttr, 0);\n")
    }
    throw IllegalArgumentException(count.toString())
  }

  override fun superinterface(className: String): ClassName {
    val packageName = className.substring(0, className.lastIndexOf('.'))
    val simpleName = className.substring(className.lastIndexOf('.') + 1)
    return ClassName.get(packageName, simpleName)
  }

  override fun writeFile(fileSpec: JavaFile, outputDir: File) {
    fileSpec.writeTo(outputDir)
  }

  override fun writeFileWithFormatting(fileSpec: JavaFile, outputDir: File, outputType: TypeSpec.Builder, packageName: String) {
    JavaFormattingFileWriter(fileSpec, outputType, packageName).writeWithFormattingTo(outputDir)
  }
}
