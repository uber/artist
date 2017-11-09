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

import java.util.ServiceLoader

class ViewStencilService private constructor() {

    private val serviceLoader = ServiceLoader.load(ViewStencilProvider::class.java)

    /**
     * Gets the [ViewStencil] implementations loaded.
     *
     * @return The located [ViewStencil]s.
     */
    fun getStencils(): Set<ViewStencil> {
        val stencils = LinkedHashSet<ViewStencil>()
        serviceLoader.iterator()
                .forEach { stencils.addAll(it.stencils()) }
        return stencils
    }

    /**
     * Gets the [Trait] implementations that should be applied to every [ViewStencil].
     *
     * @return The located global [Trait]s.
     */
    fun getGlobalTraits(): Set<Class<out Trait>> {
        val globalTraits = LinkedHashSet<Class<out Trait>>()
        serviceLoader.iterator()
                .forEach { globalTraits.addAll(it.globalTraits()) }
        return globalTraits
    }

    companion object {
        fun newInstance(): ViewStencilService {
            return ViewStencilService()
        }
    }
}
