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

import com.squareup.javapoet.ClassName

class TypeNames {
    class Android {
        companion object {
            val AccessibilityEvent: ClassName = ClassName.get("android.view.accessibility", "AccessibilityEvent")
            val AccessibilityNodeInfo: ClassName = ClassName.get("android.view.accessibility", "AccessibilityNodeInfo")
            val AttributeSet: ClassName = ClassName.get("android.util", "AttributeSet")
            val Canvas: ClassName = ClassName.get("android.graphics", "Canvas")
            val Context: ClassName = ClassName.get("android.content", "Context")
            val Drawable: ClassName = ClassName.get("android.graphics.drawable", "Drawable")
            val Gravity: ClassName = ClassName.get("android.view", "Gravity")
            val GravityCompat: ClassName = ClassName.get("android.support.v4.view", "GravityCompat")
            val MenuItem: ClassName = ClassName.get("android.view", "MenuItem")
            val Rect: ClassName = ClassName.get("android.graphics", "Rect")
            val TabLayout: ClassName = ClassName.get("android.support.design.widget", "TabLayout")
            val TabLayoutTab: ClassName = TabLayout.nestedClass("Tab")
            val TypedArray: ClassName = ClassName.get("android.content.res", "TypedArray")
            val View: ClassName = ClassName.get("android.view", "View")
            val MotionEvent: ClassName = ClassName.get("android.view", "MotionEvent")
        }
    }

    class Annotations {
        companion object {
            val AttrRes: ClassName = ClassName.get("android.support.annotation", "AttrRes")
            val CallSuper: ClassName = ClassName.get("android.support.annotation", "CallSuper")
            val IdRes: ClassName = ClassName.get("android.support.annotation", "IdRes")
            val Nullable: ClassName = ClassName.get("android.support.annotation", "Nullable")
            val StyleRes: ClassName = ClassName.get("android.support.annotation", "StyleRes")
            val TargetApi: ClassName = ClassName.get("android.annotation", "TargetApi")
            val VisibleForTesting: ClassName = ClassName.get("android.support.annotation", "VisibleForTesting")
        }
    }

    class Java {
        companion object {
            val Map: ClassName = ClassName.get(java.util.Map::class.java)
            val String: ClassName = ClassName.get(java.lang.String::class.java)
        }
    }
}
