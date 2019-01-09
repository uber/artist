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

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.uber.artist.api.KotlinTrait
import com.uber.artist.api.KotlinTraitService
import com.uber.artist.api.KotlinTypeNames
import com.uber.artist.api.KotlinViewStencil
import com.uber.artist.api.KotlinViewStencilService
import java.io.File

class KotlinArtistCodeGenerator : ArtistCodeGenerator<FileSpec, TypeSpec.Builder, FunSpec.Builder, ClassName, CodeBlock, KotlinViewStencil, KotlinTrait>() {

  override val viewStencils: Set<KotlinViewStencil>
    get() = KotlinViewStencilService.newInstance().getStencils()

  override val traits: Set<KotlinTrait>
    get() = KotlinTraitService.newInstance().get()

  override val globalTraits: Set<Class<out KotlinTrait>>
    get() = KotlinViewStencilService.newInstance().getGlobalTraits()

  override fun generateFileSpecFor(viewPackageName: String, typeSpecBuilder: TypeSpec.Builder): FileSpec {
    val typeSpec = typeSpecBuilder.build()
    return FileSpec.builder(viewPackageName, typeSpec.name ?: throw IllegalStateException("No name for type: $typeSpec"))
        .addType(typeSpec)
        .build()
  }

  override fun generateTypeSpecFor(stencil: KotlinViewStencil, rPackageName: String, traitMap: Map<Class<out KotlinTrait>, KotlinTrait>, superinterfaceClassName: String?): TypeSpec.Builder {
    val rClass = ClassName(rPackageName, "R")
    val typeBuilder = TypeSpec.classBuilder(stencil.name())
        .addModifiers(KModifier.OPEN)
        .superclass(stencil.sourceType)

    superinterfaceClassName?.let { typeBuilder.addSuperinterface(superinterface(superinterfaceClassName)) }

    generateConstructorsFor(stencil, typeBuilder, rClass)
    val initMethod = createInitBuilderFor(stencil, typeBuilder)

    stencil.traits()
        .map { traitName -> traitMap[traitName] }
        .forEach { it?.generateFor(typeBuilder, initMethod, rClass, stencil.name()) }

    typeBuilder.addFunction(initMethod.build())
    stencil.typeHook(typeBuilder)
    return typeBuilder
  }


  override fun createInitBuilderFor(stencil: KotlinViewStencil, type: TypeSpec.Builder): FunSpec.Builder {
    return FunSpec.builder("init")
        .addAnnotation(KotlinTypeNames.Annotations.CallSuper)
        .addModifiers(KModifier.PROTECTED, KModifier.OPEN)
        .addParameter(ParameterSpec.builder("context", KotlinTypeNames.Android.Context)
            .build())
        .addParameter(ParameterSpec.builder("attrs", KotlinTypeNames.Android.AttributeSet.copy(nullable = true))
            .addAnnotation(KotlinTypeNames.Annotations.Nullable)
            .build())
        .addParameter(ParameterSpec.builder("defStyleAttr", INT)
            .addAnnotation(KotlinTypeNames.Annotations.AttrRes)
            .build())
        .addParameter(ParameterSpec.builder("defStyleRes", INT)
            .addAnnotation(KotlinTypeNames.Annotations.StyleRes)
            .build())
        .also {
          stencil.initMethodHook(type, it)
        }
  }

  override fun generateConstructorsFor(stencil: KotlinViewStencil, type: TypeSpec.Builder, rClass: ClassName) {
    val paramContext = ParameterSpec.builder("context", KotlinTypeNames.Android.Context)
        .build()
    val paramAttrs = ParameterSpec.builder("attrs", KotlinTypeNames.Android.AttributeSet.copy(nullable = true))
        .addAnnotation(KotlinTypeNames.Annotations.Nullable)
        .defaultValue("null")
        .build()
    val paramDefStyleAttr = ParameterSpec.builder("defStyleAttr", INT)
        .addAnnotation(KotlinTypeNames.Annotations.AttrRes)
        .defaultValue(stencil.defaultAttrRes?.let {
            if ("." in it) {
              CodeBlock.of(it)
            } else {
              CodeBlock.of("%T.attr.$it", rClass)
            }
          } ?: CodeBlock.of("0")
        )
        .build()
    val paramDefStyleRes = ParameterSpec.builder("defStyleRes", INT)
        .addAnnotation(KotlinTypeNames.Annotations.StyleRes)
        .defaultValue("0")
        .build()

    val params = listOf(paramContext, paramAttrs, paramDefStyleAttr, paramDefStyleRes)
    val superConstructorArgs = listOf("context", "attrs", "defStyleAttr", "defStyleRes")

    val ctorOverloadsCount = stencil.constructorCount.coerceAtMost(3)
    val overloadsConstructor = FunSpec.constructorBuilder()
        .addAnnotation(JvmOverloads::class)
        .addParameters(params.subList(0, ctorOverloadsCount))
        .callSuperConstructor(*superConstructorArgs.subList(0, ctorOverloadsCount).toTypedArray())
        .addStatement(initStatement(ctorOverloadsCount))
        .build()

    val targetApiConstructor = FunSpec.constructorBuilder()
        .addAnnotation(AnnotationSpec.builder(KotlinTypeNames.Annotations.TargetApi)
            .addMember("%T.VERSION_CODES.LOLLIPOP", ClassName("android.os", "Build"))
            .build())
        .addParameters(params)
        .callSuperConstructor(*superConstructorArgs.toTypedArray())
        .addStatement(initStatement(4))
        .build()

    type
        .addFunction(overloadsConstructor)
        .apply {
          if (stencil.constructorCount > 3) addFunction(targetApiConstructor)
        }
  }

  override fun superinterface(className: String) = ClassName(
      className.substringBeforeLast('.'),
      className.substringAfterLast('.')
  )

  override fun writeFile(fileSpec: FileSpec, outputDir: File) {
    fileSpec.writeTo(outputDir)
  }

  override fun writeFileWithFormatting(fileSpec: FileSpec, outputDir: File, outputType: TypeSpec.Builder, packageName: String) {
    fileSpec.writeTo(outputDir)
  }
}
