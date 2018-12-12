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

abstract class ViewStencil<OutputType, FunType, ClassName, CodeBlock>(
    val extendedType: String,
    val constructorCount: Int = 4,
    val defaultAttrRes: String? = null,
    protected val addedTraits: Set<Class<out Trait<*, *, *>>>
) {

    val globalTraits = mutableSetOf<Class<out Trait<*, *, *>>>()
    var namePrefix: String = ""

    fun traits(): Set<Class<out Trait<*, *, *>>> = globalTraits.plus(addedTraits)

    /**
     * The name of the view class.
     */
    open fun name(): String {
      val sourceName = extendedType.split('.').last()
      return "$namePrefix${sourceName.removePrefix("AppCompat")}"
    }

    /**
     * Hook for when attributes are being pulled out of the attribute set.
     * Can safely assume the following values exist:
     *   - context: Context
     *   - attrs: AttributeSet
     *   - defStyleAttr: int
     *   - a: TypedArray
     *
     * Should *not* recycle `a`. Safe to assume `a` is null-checked before code would execute.
     */
    abstract fun attrsHook(type: OutputType, initMethod: FunType): CodeBlock?

    /**
     * Hook for implementing the `init()` method.
     */
    abstract fun initMethodHook(type: OutputType, initMethod: FunType)

    /**
     * Hook for the type builder implementation.
     */
    abstract fun typeHook(type: OutputType)

    fun setGlobalTraits(traits: Set<Class<out Trait<*, *, *>>>) {
      globalTraits.addAll(traits)
    }

    fun setPrefix(namePrefix: String) {
      this.namePrefix = namePrefix
    }
}
