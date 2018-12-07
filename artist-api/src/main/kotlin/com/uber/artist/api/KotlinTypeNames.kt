@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

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

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName

class KotlinTypeNames {
  class Android {
    companion object {
      val AccessibilityEvent: ClassName = ClassName("android.view.accessibility", "AccessibilityEvent")
      val AccessibilityNodeInfo: ClassName = ClassName("android.view.accessibility", "AccessibilityNodeInfo")
      val AttributeSet: ClassName = ClassName("android.util", "AttributeSet")
      val Canvas: ClassName = ClassName("android.graphics", "Canvas")
      val Context: ClassName = ClassName("android.content", "Context")
      val Drawable: ClassName = ClassName("android.graphics.drawable", "Drawable")
      val Gravity: ClassName = ClassName("android.view", "Gravity")
      val GravityCompat: ClassName = ClassName("androidx.core.view", "GravityCompat")
      val MenuItem: ClassName = ClassName("android.view", "MenuItem")
      val Rect: ClassName = ClassName("android.graphics", "Rect")
      val TabLayout: ClassName = ClassName("com.google.android.material.tabs", "TabLayout")
      val TabLayoutTab: ClassName = TabLayout.nestedClass("Tab")
      val TypedArray: ClassName = ClassName("android.content.res", "TypedArray")
      val View: ClassName = ClassName("android.view", "View")
      val MotionEvent: ClassName = ClassName("android.view", "MotionEvent")
    }
  }

  class Annotations {
    companion object {
      val AttrRes: ClassName = ClassName("androidx.annotation", "AttrRes")
      val CallSuper: ClassName = ClassName("androidx.annotation", "CallSuper")
      val IdRes: ClassName = ClassName("androidx.annotation", "IdRes")
      val Nullable: ClassName = ClassName("androidx.annotation", "Nullable")
      val StyleRes: ClassName = ClassName("androidx.annotation", "StyleRes")
      val TargetApi: ClassName = ClassName("android.annotation", "TargetApi")
      val VisibleForTesting: ClassName = ClassName("androidx.annotation", "VisibleForTesting")
    }
  }

  class Java {
    companion object {
      val Map: ClassName = java.util.Map::class.asClassName()
      val String: ClassName = java.lang.String::class.asClassName()
    }
  }
}
