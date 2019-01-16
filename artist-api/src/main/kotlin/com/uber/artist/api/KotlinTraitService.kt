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

class KotlinTraitService private constructor() : TraitService<KotlinTrait> {

  private val serviceLoader = ServiceLoader.load(KotlinTrait::class.java)

  /**
   * Gets the [Trait] implementations loaded.
   *
   * @return The located [Trait]s.
   */
  override fun get(): Set<KotlinTrait> {
    val traits = LinkedHashSet<KotlinTrait>()
    serviceLoader.iterator()
        .forEach { traits.add(it) }
    return traits
  }

  companion object {
    fun newInstance(): KotlinTraitService {
      return KotlinTraitService()
    }
  }
}
