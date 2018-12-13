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

package com.uber.artist.myproviders;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import com.uber.artist.traits.rx.config.JavaArtistRxConfig;

import androidx.annotation.NonNull;

/**
 * Sample Artist RxTrait Config.
 */
@AutoService(JavaArtistRxConfig.class)
public class JavaSampleRxConfig extends JavaArtistRxConfig {

  @Override
  public void processTap(CodeBlock.Builder codeBlockBuilder) {
    super.processTap(codeBlockBuilder);
    codeBlockBuilder.add(".doOnNext($T.createTapProcessor())", JavaSampleTypeNames.MY_UTILS);
  }

  @Override
  public void processRxBindingSignalEvent(@NonNull CodeBlock.Builder codeBlockBuilder) {
    super.processRxBindingSignalEvent(codeBlockBuilder);
    codeBlockBuilder.add(".map($T.createRxBindingSignalMapper())", JavaSampleTypeNames.MY_UTILS);
  }

  @Override
  public TypeName rxBindingSignalEventTypeName() {
    return JavaSampleTypeNames.SIGNAL;
  }
}
