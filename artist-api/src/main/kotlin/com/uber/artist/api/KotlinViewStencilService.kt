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

package com.uber.artist.api

import java.util.ServiceLoader

class KotlinViewStencilService private constructor() : ViewStencilService<KotlinViewStencil, KotlinTrait> {

  private val serviceLoader = ServiceLoader.load(KotlinViewStencilProvider::class.java)

  /**
   * Gets the [ViewStencil] implementations loaded.
   *
   * @return The located [ViewStencil]s.
   */
  override fun getStencils(): Set<KotlinViewStencil> {
    val stencils = LinkedHashSet<KotlinViewStencil>()
    serviceLoader.iterator()
        .forEach { stencils.addAll(it.stencils()) }
    return stencils
  }

  /**
   * Gets the [Trait] implementations that should be applied to every [ViewStencil].
   *
   * @return The located global [Trait]s.
   */
  override fun getGlobalTraits(): Set<Class<out KotlinTrait>> {
    val globalTraits = LinkedHashSet<Class<out KotlinTrait>>()
    serviceLoader.iterator()
        .forEach { globalTraits.addAll(it.globalTraits()) }
    return globalTraits
  }

  companion object {
    fun newInstance(): KotlinViewStencilService {
      return KotlinViewStencilService()
    }
  }
}
