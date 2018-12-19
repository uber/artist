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

package com.uber.artist.traits

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.uber.artist.api.KotlinTrait
import com.uber.artist.api.KotlinTypeNames
import com.uber.artist.api.Trait

/**
 * This [Trait] ports [FrameLayout]'s foreground functionality to other views. In order to use this, the module that
 * applies that [Artist] plugin must declare the ForegroundView styleable in res/values/attrs_foreground_view.xml.
 *
 * <?xml version="1.0" encoding="utf-8"?>
 * <resources>
 *   <declare-styleable name="ForegroundView">
 *     <attr name="android:foreground"/>
 *     <attr name="android:foregroundGravity"/>
 *     <attr name="foregroundInsidePadding"/>
 *   </declare-styleable>
 * </resources>
 */
@AutoService(KotlinTrait::class)
class KotlinForegroundTrait : KotlinTrait {
  override fun generateFor(
      type: TypeSpec.Builder,
      initMethod: FunSpec.Builder,
      rClass: ClassName,
      sourceType: String) {

    val isLayout = sourceType.endsWith("Layout")

    // The field
    type.addProperty(PropertySpec.builder("foreground", KotlinTypeNames.Android.Drawable.copy(nullable = true), KModifier.PRIVATE)
        .addAnnotation(KotlinTypeNames.Annotations.Nullable)
        .initializer("null")
        .mutable()
        .build())

    if (isLayout) {
      type.addProperty(PropertySpec.builder("selfBounds", KotlinTypeNames.Android.Rect, KModifier.PRIVATE, KModifier.FINAL)
          .initializer("%T()", KotlinTypeNames.Android.Rect)
          .build())
      type.addProperty(PropertySpec.builder("overlayBounds", KotlinTypeNames.Android.Rect, KModifier.PRIVATE, KModifier.FINAL)
          .initializer("%T()", KotlinTypeNames.Android.Rect)
          .build())
      type.addProperty(PropertySpec.builder("foregroundInPadding", BOOLEAN, KModifier.PRIVATE)
          .initializer("true")
          .mutable()
          .build())
      type.addProperty(PropertySpec.builder("foregroundBoundsChanged", BOOLEAN, KModifier.PRIVATE)
          .initializer("false")
          .mutable()
          .build())
      type.addProperty(PropertySpec.builder("foregroundGravity", INT, KModifier.PRIVATE)
          .initializer("%T.FILL", KotlinTypeNames.Android.Gravity)
          .mutable()
          .build())
    }

    // Pull out the value
    initMethod.apply {
      addStatement("val foregroundTA = context.obtainStyledAttributes(attrs, %T.styleable.ForegroundView)", rClass)
      beginControlFlow("foregroundTA.getDrawable(%T.styleable.ForegroundView_android_foreground)?.let", rClass)
      addCode("//noinspection AndroidLintNewApi\n")
      addStatement("setForeground(it)")
      endControlFlow()

      if (isLayout) {
        addStatement(
            "foregroundGravity = foregroundTA.getInt(%T.styleable.ForegroundView_android_foregroundGravity, foregroundGravity)", rClass)
        addStatement(
            "foregroundInPadding = foregroundTA.getBoolean(%T.styleable.ForegroundView_foregroundInsidePadding, true)", rClass)
      }

      addStatement("foregroundTA.recycle()")
    }

    val onSizeChangedMethod = FunSpec.builder("onSizeChanged")
        .addAnnotation(Override::class.java)
        .addModifiers(KModifier.OVERRIDE, KModifier.PROTECTED)
        .addParameter("w", INT)
        .addParameter("h", INT)
        .addParameter("oldw", INT)
        .addParameter("oldh", INT)
        .addStatement("super.onSizeChanged(w, h, oldw, oldh)")

    if (isLayout) {
      onSizeChangedMethod.addStatement("foregroundBoundsChanged = true")
    } else {
      onSizeChangedMethod.addStatement("foreground?.setBounds(0, 0, w, h)")
    }

    type.addFunction(onSizeChangedMethod.build())

    if (sourceType.endsWith("ImageView")) {
      type.addFunction(FunSpec.builder("hasOverlappingRendering")
          .addAnnotation(Override::class.java)
          .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
          .returns(BOOLEAN)
          .addStatement("return false")
          .build())
    }

    if (isLayout) {
      type.addFunction(FunSpec.builder("getForegroundGravity")
          .addKdoc("""Describes how the foreground is positioned.

    @return foreground gravity.
    @see #setForegroundGravity(int)
    """)
          .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("%S", "MissingOverride").build())
          .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
          .returns(INT)
          .addStatement("return foregroundGravity")
          .build())

      type.addFunction(FunSpec.builder("setForegroundGravity")
          .addKdoc("""Describes how the foreground is positioned. Defaults to START and TOP.

    @param foregroundGravity See {@link android.view.Gravity}
    @see #getForegroundGravity()
    """)
          .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("%S", "MissingOverride").build())
          .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
          .addParameter("foregroundGravity", INT)
          .beginControlFlow("if (this.foregroundGravity != foregroundGravity)")
          .beginControlFlow("if ((foregroundGravity and %T.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0)",
              KotlinTypeNames.Android.Gravity)
          .addStatement("this.foregroundGravity = foregroundGravity.or(%T.START)", KotlinTypeNames.Android.GravityCompat)
          .endControlFlow()
          .beginControlFlow("if ((foregroundGravity and %T.VERTICAL_GRAVITY_MASK) == 0)",
              KotlinTypeNames.Android.Gravity)
          .addStatement("this.foregroundGravity = foregroundGravity.or(%T.TOP)", KotlinTypeNames.Android.Gravity)
          .endControlFlow()
          .beginControlFlow("if (this.foregroundGravity == %T.FILL && foreground != null)",
              KotlinTypeNames.Android.Gravity)
          .addStatement("val padding = %T()", KotlinTypeNames.Android.Rect)
          .addStatement("foreground?.getPadding(padding)")
          .endControlFlow()
          .addStatement("requestLayout()")
          .endControlFlow()
          .build())
    }

    type.addFunction(FunSpec.builder("verifyDrawable")
        .addAnnotation(Override::class.java)
        .addModifiers(KModifier.OVERRIDE, KModifier.PROTECTED)
        .returns(BOOLEAN)
        .addParameter("who", KotlinTypeNames.Android.Drawable)
        .addStatement("return super.verifyDrawable(who) || (who == foreground)")
        .build())

    type.addFunction(FunSpec.builder("jumpDrawablesToCurrentState")
        .addAnnotation(Override::class.java)
        .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
        .addStatement("super.jumpDrawablesToCurrentState()")
        .addStatement("foreground?.jumpToCurrentState()")
        .build())

    type.addFunction(FunSpec.builder("drawableStateChanged")
        .addAnnotation(Override::class.java)
        .addModifiers(KModifier.OVERRIDE, KModifier.PROTECTED)
        .addStatement("super.drawableStateChanged()")
        .beginControlFlow("if (foreground?.isStateful() ?: false)")
        .addStatement("foreground?.setState(getDrawableState())")
        .endControlFlow()
        .build())

    type.addFunction(FunSpec.builder("getForeground")
        .addKdoc("""Returns the drawable used as the foreground of this view. The
    foreground drawable, if non-null, is always drawn on top of the children.

    @return A Drawable or null if no foreground was set.
    """)
        .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("%S", "MissingOverride").build())
        .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
        .returns(KotlinTypeNames.Android.Drawable.copy(nullable = true))
        .addStatement("return foreground")
        .build())

    val setForegroundMethod = FunSpec.builder("setForeground")
        .addKdoc("""Supply a Drawable that is to be rendered on top of all of the child
    views in this layout.  Any padding in the Drawable will be taken
    into account by ensuring that the children are inset to be placed
    inside of the padding area.

    @param drawable The Drawable to be drawn on top of the children.
    """)
        .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("%S", "MissingOverride").build())
        .addAnnotation(AnnotationSpec.builder(ClassName("android.annotation", "SuppressLint"))
            .addMember("%S", "NewApi")
            .build())
        .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
        .addParameter("drawable", KotlinTypeNames.Android.Drawable.copy(nullable = true))
        .beginControlFlow("if (foreground != drawable)")
        .beginControlFlow("if (foreground != null)")
        .addStatement("foreground?.setCallback(null)")
        .addStatement("unscheduleDrawable(foreground)")
        .endControlFlow()
        .addStatement("foreground = drawable")
        .beginControlFlow("if (drawable != null)")

    if (!isLayout) {
      setForegroundMethod.addStatement("foreground?.setBounds(0, 0, getWidth(), getHeight())")
    }

    setForegroundMethod.addStatement("setWillNotDraw(false)")
        .addStatement("drawable.setCallback(this)")
        .beginControlFlow("if (drawable.isStateful())")
        .addStatement("drawable.setState(getDrawableState())")
        .endControlFlow()

    if (isLayout) {
      setForegroundMethod.beginControlFlow("if (foregroundGravity == %T.FILL)", KotlinTypeNames.Android.Gravity)
      setForegroundMethod.addStatement("val padding = %T()", KotlinTypeNames.Android.Rect)
      setForegroundMethod.addStatement("drawable.getPadding(padding)")
      setForegroundMethod.endControlFlow()
    }

    setForegroundMethod.nextControlFlow("else")
        .addStatement("setWillNotDraw(true)")
        .endControlFlow()

    if (isLayout) {
      setForegroundMethod.addStatement("requestLayout()")
    }
    setForegroundMethod.addStatement("invalidate()")
        .endControlFlow()

    type.addFunction(setForegroundMethod.build())

    if (isLayout) {
      type.addFunction(FunSpec.builder("onLayout")
          .addAnnotation(Override::class.java)
          .addModifiers(KModifier.OVERRIDE, KModifier.PROTECTED)
          .addParameter("changed", BOOLEAN)
          .addParameter("left", INT)
          .addParameter("top", INT)
          .addParameter("right", INT)
          .addParameter("bottom", INT)
          .addStatement("super.onLayout(changed, left, top, right, bottom)")
          .beginControlFlow("if (changed)")
          .addStatement("foregroundBoundsChanged = true")
          .endControlFlow()
          .build())
    }

    val drawMethod = FunSpec.builder("draw")
        .addAnnotation(Override::class.java)
        .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
        .addParameter("canvas", KotlinTypeNames.Android.Canvas)
        .addStatement("super.draw(canvas)")

    if (isLayout) {
      drawMethod
          .beginControlFlow("foreground?.let")
          .addStatement("val localForeground = it")
          .beginControlFlow("if (foregroundBoundsChanged)")
          .addStatement("foregroundBoundsChanged = false")
          .addStatement("val localSelfBounds: %T = selfBounds", KotlinTypeNames.Android.Rect)
          .addStatement("val localOverlayBounds: %T = overlayBounds", KotlinTypeNames.Android.Rect)
          .addStatement("val w: Int = getRight() - getLeft()")
          .addStatement("val h: Int = getBottom() - getTop()")
          .beginControlFlow("if (foregroundInPadding)")
          .addStatement("localSelfBounds.set(0, 0, w, h)")
          .nextControlFlow("else")
          .addStatement("localSelfBounds.set(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - " +
              "getPaddingBottom())")
          .endControlFlow()
          .addStatement("%T.apply(foregroundGravity, localForeground.getIntrinsicWidth(), localForeground" +
              ".getIntrinsicHeight(), localSelfBounds, localOverlayBounds)", KotlinTypeNames.Android.Gravity)
          .addStatement("localForeground.setBounds(localOverlayBounds)")
          .endControlFlow()
          .addStatement("localForeground.draw(canvas)")
          .endControlFlow()
    } else {
      drawMethod.addStatement("foreground?.draw(canvas)")
    }
    type.addFunction(drawMethod.build())

    type.addFunction(FunSpec.builder("drawableHotspotChanged")
        .addAnnotation(AnnotationSpec.builder(KotlinTypeNames.Annotations.TargetApi)
            .addMember("%T.VERSION_CODES.LOLLIPOP", ClassName("android.os", "Build"))
            .build())
        .addAnnotation(Override::class.java)
        .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
        .addParameter("x", FLOAT)
        .addParameter("y", FLOAT)
        .addStatement("super.drawableHotspotChanged(x, y)")
        .addStatement("foreground?.setHotspot(x, y)")
        .build())
  }
}
