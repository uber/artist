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

package com.uber.artist.myproviders

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeName
import com.uber.artist.traits.rx.config.KotlinArtistRxConfig

/**
 * Sample Artist RxTrait Config.
 */
@AutoService(KotlinArtistRxConfig::class)
class KotlinSampleRxConfig : KotlinArtistRxConfig() {

  override fun processTap(codeBlockBuilder: CodeBlock.Builder) {
    super.processTap(codeBlockBuilder)
    codeBlockBuilder.add(".doOnNext(%T.createTapProcessor())", KotlinSampleTypeNames.MY_UTILS)
  }

  override fun processRxBindingSignalEvent(codeBlockBuilder: CodeBlock.Builder) {
    super.processRxBindingSignalEvent(codeBlockBuilder)
    codeBlockBuilder.add(".map(%T.createRxBindingSignalMapper())", KotlinSampleTypeNames.MY_UTILS)
  }

  override fun rxBindingSignalEventTypeName(): TypeName {
    return KotlinSampleTypeNames.SIGNAL
  }
}
