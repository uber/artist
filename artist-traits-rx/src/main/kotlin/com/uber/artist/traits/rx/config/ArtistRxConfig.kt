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

package com.uber.artist.traits.rx.config

/**
 * This configuration object describes various plugin points for rx-based traits.
 */
interface ArtistRxConfig<CodeBlockType, TypeNameType> {

  /**
   * Plugin point for generating additional code to invoke when a view has been tapped.
   */
  fun processTap(codeBlockBuilder: CodeBlockType) {}

  /**
   * Plugin point for generating additional code to invoke when a view has attached to the window.
   */
  fun processImpression(codeBlockBuilder: CodeBlockType) {}

  /**
   * Plugin point for generating additional code to invoke when a view has changed visibility.
   */
  fun processVisibilityChanges(codeBlockBuilder: CodeBlockType) {}

  /**
   * Plugin point for generating additional code to modify an RxBinding stream.
   */
  fun processRxBindingStream(codeBlockBuilder: CodeBlockType, streamTypeName: TypeNameType) {}

  /**
   * Plugin point for generating additional code to modify an RxBinding stream which notifies that something occurred.
   * This can be used along with rxBindingSignalEventTypeName() to map signal events to a different type.
   */
  fun processRxBindingSignalEvent(codeBlockBuilder: CodeBlockType) {}

  /**
   * This defines the type to be used for RxBinding event signals. It can be changed if processRxBindingSignalEvent()
   * has been overridden to map the signal events to a new type. The default type is Object.
   */
  fun rxBindingSignalEventTypeName(): TypeNameType
}
