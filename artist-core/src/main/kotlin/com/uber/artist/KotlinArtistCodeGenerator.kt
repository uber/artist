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

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.uber.artist.api.KotlinTrait
import com.uber.artist.api.KotlinTraitService
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

  override fun generateFileSpecFor(viewPackageName: String, typeSpecBuilder: TypeSpec.Builder): FileSpec = TODO("not implemented")
  override fun generateTypeSpecFor(stencil: KotlinViewStencil, packageName: String, traitMap: Map<Class<out KotlinTrait>, KotlinTrait>, superinterfaceClassName: String?): TypeSpec.Builder = TODO("not implemented")
  override fun createInitBuilderFor(stencil: KotlinViewStencil, type: TypeSpec.Builder): FunSpec.Builder = TODO("not implemented")
  override fun generateConstructorsFor(stencil: KotlinViewStencil, type: TypeSpec.Builder, rClass: ClassName) = TODO("not implemented")
  override fun constructorBlock(stencil: KotlinViewStencil, rClass: ClassName, total: Int, currentIndex: Int): CodeBlock = TODO("not implemented")
  override fun fallthroughConstructorStatement(stencil: KotlinViewStencil, rClass: ClassName, count: Int): CodeBlock = TODO("not implemented")
  override fun superinterface(className: String): ClassName = TODO("not implemented")
  override fun writeFile(fileSpec: FileSpec, outputDir: File) = TODO("not implemented")
  override fun writeFileWithFormatting(fileSpec: FileSpec, outputDir: File, outputType: TypeSpec.Builder, packageName: String) = TODO("not implemented")
}
