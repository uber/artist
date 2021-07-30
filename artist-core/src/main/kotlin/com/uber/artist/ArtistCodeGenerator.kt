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

import com.google.common.annotations.VisibleForTesting
import com.squareup.kotlinpoet.TypeAliasSpec
import com.uber.artist.api.Trait
import com.uber.artist.api.ViewStencil
import java.io.File

abstract class ArtistCodeGenerator<
    OutputFileType,
    OutputType,
    FunType,
    ClassName,
    CodeBlock,
    ViewStencilType : ViewStencil<OutputType, FunType, ClassName, CodeBlock>,
    TraitType : Trait<OutputType, FunType, ClassName>> {

  abstract val viewStencils: Set<ViewStencilType>
  abstract val traits: Set<TraitType>
  abstract val globalTraits: Set<Class<out TraitType>>

  fun generateViews(
      outputDir: File,
      viewPackageName: String,
      rPackageName: String,
      superinterfaceClassName: String?,
      viewNamePrefix: String,
      formatSource: Boolean
  ) {
    generateViewsForStencils(viewStencils, traits, globalTraits, outputDir, viewPackageName, rPackageName, superinterfaceClassName, viewNamePrefix, formatSource)
  }

  @VisibleForTesting
  fun generateViewsForStencils(
      viewStencils: Set<ViewStencilType>,
      traits: Set<TraitType>,
      globalTraits: Set<Class<out TraitType>>,
      outputDir: File,
      viewPackageName: String,
      rPackageName: String,
      superinterfaceClassName: String?,
      viewNamePrefix: String,
      formatSource: Boolean) {
    val traitMap: Map<Class<out TraitType>, TraitType> = traits.associateBy { it.javaClass }

    viewStencils.forEach {
      it.setGlobalTraits(globalTraits)
      it.setPrefix(viewNamePrefix)

      val typeSpecBuilder = generateTypeSpecFor(it, rPackageName, traitMap, superinterfaceClassName)
      val fileSpec = generateFileSpecFor(viewPackageName, typeSpecBuilder)
      if (formatSource) {
        writeFileWithFormatting(fileSpec, outputDir, typeSpecBuilder, viewPackageName)
      } else {
        writeFile(fileSpec, outputDir)
      }
    }
  }

  protected abstract fun generateFileSpecFor(viewPackageName: String, typeSpecBuilder: OutputType): OutputFileType

  protected abstract fun generateTypeSpecFor(
      stencil: ViewStencilType,
      rPackageName: String,
      traitMap: Map<Class<out TraitType>, TraitType>,
      superinterfaceClassName: String?): OutputType

  protected abstract fun createInitBuilderFor(stencil: ViewStencilType, type: OutputType): FunType

  protected abstract fun generateConstructorsFor(stencil: ViewStencilType, type: OutputType, rClass: ClassName)

  protected abstract fun superinterface(className: String): ClassName

  protected abstract fun writeFile(fileSpec: OutputFileType, outputDir: File)

  protected abstract fun writeFileWithFormatting(fileSpec: OutputFileType, outputDir: File, outputType: OutputType, viewPackageName: String)

  protected fun superConstructorStatement(count: Int): String {
    when (count) {
      1 -> return "super(context)"
      2 -> return "super(context, attrs)"
      3 -> return "super(context, attrs, defStyleAttr)"
      4 -> return "super(context, attrs, defStyleAttr, defStyleRes)"
    }
    throw IllegalArgumentException()
  }

  protected fun initStatement(count: Int): String {
    when (count) {
      1 -> return "init(context, null, 0, 0)"
      2 -> return "init(context, attrs, 0, 0)"
      3 -> return "init(context, attrs, defStyleAttr, 0)"
      4 -> return "init(context, attrs, defStyleAttr, defStyleRes)"
    }
    throw IllegalArgumentException()
  }
}
