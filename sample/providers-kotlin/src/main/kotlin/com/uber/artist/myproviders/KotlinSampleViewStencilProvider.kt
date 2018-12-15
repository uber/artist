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
import com.uber.artist.api.KotlinTrait
import com.uber.artist.api.KotlinViewStencil
import com.uber.artist.api.KotlinViewStencilProvider
import com.uber.artist.myproviders.trait.KotlinSampleTrait
import com.uber.artist.traits.kotlin.ForegroundTrait
import com.uber.artist.traits.kotlin.SuppressNullabilityInitializerTrait
import com.uber.artist.traits.kotlin.VisibilityTrait

import java.util.Arrays
import java.util.LinkedHashSet

/**
 * Sample ViewStencil provider.
 */
@AutoService(KotlinViewStencilProvider::class)
class KotlinSampleViewStencilProvider : KotlinViewStencilProvider {

  override fun stencils(): Set<KotlinViewStencil> {
    return LinkedHashSet(Arrays.asList(
        KotlinViewStencil("androidx.appcompat.widget.AppCompatButton", 3, "buttonStyle"),
        KotlinViewStencil("androidx.appcompat.widget.AppCompatEditText", 3,
            "android.R.attr.editTextStyle"/*, TextInputTrait::class.java*/),
        KotlinViewStencil("android.widget.LinearLayout", 4, null),
        KotlinViewStencil("androidx.appcompat.widget.AppCompatImageView", 3, null),
        KotlinViewStencil("androidx.core.widget.NestedScrollView", 3, null/*, ScrollableTrait::class.java*/),
        KotlinViewStencil("android.widget.TextView", 3, "android.R.attr.textViewStyle"),
        SwitchStencil()
    ))
  }

  override fun globalTraits(): Set<Class<out KotlinTrait>> = setOf(
    KotlinSampleTrait::class.java,
    VisibilityTrait::class.java,
    ForegroundTrait::class.java,
    SuppressNullabilityInitializerTrait::class.java
//    ,ViewTrait::class.java
  )

  private class SwitchStencil : KotlinViewStencil("androidx.appcompat.widget.SwitchCompat", 3, "switchStyle"/*, CheckableTrait::class.java*/) {

    override fun name(): String {
      return "MySwitch"
    }
  }
}
