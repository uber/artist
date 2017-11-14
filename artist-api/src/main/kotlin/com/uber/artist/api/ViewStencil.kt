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

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec

open class ViewStencil(
        val extendedType: String,
        val constructorCount: Int = 4,
        val defaultAttrRes: String? = null,
        vararg val addedTraits: Class<out Trait> = emptyArray()) {

    val sourcePackage = "${extendedType.substringBeforeLast('.')}"
    val sourceName = extendedType.split('.').last()
    val sourceType = ClassName.get(sourcePackage, sourceName)
    val globalTraits = mutableSetOf<Class<out Trait>>()
    var namePrefix: String = ""

    open fun traits(): Set<Class<out Trait>> = globalTraits.plus(addedTraits.toSet())

    /**
     * The name of the view class.
     */
    open fun name() = "$namePrefix${sourceName.removePrefix("AppCompat")}"

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
    open fun attrsHook(type: TypeSpec.Builder, initMethod: MethodSpec.Builder): CodeBlock? {
        return null
    }

    /**
     * Hook for implementing the `init()` method.
     */
    open fun initMethodHook(type: TypeSpec.Builder, initMethod: MethodSpec.Builder) {
    }

    /**
     * Hook for the type builder implementation.
     */
    open fun typeHook(type: TypeSpec.Builder) {
    }

    fun setGlobalTraits(traits: Set<Class<out Trait>>) {
        globalTraits.addAll(traits)
    }

    fun setPrefix(namePrefix: String) {
        this.namePrefix = namePrefix
    }
}
