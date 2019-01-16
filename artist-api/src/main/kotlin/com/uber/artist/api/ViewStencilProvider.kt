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

package com.uber.artist.api

interface ViewStencilProvider<ViewStencilType, TraitType> {

  /**
   * Provide a set of [ViewStencil]s to be used during code generation.
   *
   * @return The set of [ViewStencil]s.
   */
  fun stencils(): Set<ViewStencilType>

  /**
   * Provide a set of [Trait] classes that should be applied to all [ViewStencil]s.
   *
   * @return The set of [Trait] classes.
   */
  fun globalTraits(): Set<Class<out TraitType>>
}
