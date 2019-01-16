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

package com.uber.artist.traits

import com.google.auto.service.AutoService
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.uber.artist.api.JavaTrait
import com.uber.artist.api.Trait
import com.uber.artist.api.TypeNames
import javax.lang.model.element.Modifier

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
@AutoService(JavaTrait::class)
class JavaForegroundTrait : JavaTrait {
  override fun generateFor(
      type: TypeSpec.Builder,
      initMethod: MethodSpec.Builder,
      rClass: ClassName,
      sourceType: String) {

    val isLayout = sourceType.endsWith("Layout")

    // The field
    type.addField(TypeNames.Android.Drawable, "foreground", Modifier.PRIVATE)

    if (isLayout) {
      type.addField(FieldSpec.builder(TypeNames.Android.Rect, "selfBounds", Modifier.PRIVATE, Modifier.FINAL)
          .initializer("new \$T()", TypeNames.Android.Rect)
          .build())
      type.addField(FieldSpec.builder(TypeNames.Android.Rect, "overlayBounds", Modifier.PRIVATE, Modifier.FINAL)
          .initializer("new \$T()", TypeNames.Android.Rect)
          .build())
      type.addField(FieldSpec.builder(TypeName.BOOLEAN, "foregroundInPadding", Modifier.PRIVATE)
          .initializer("true")
          .build())
      type.addField(FieldSpec.builder(TypeName.BOOLEAN, "foregroundBoundsChanged", Modifier.PRIVATE)
          .initializer("false")
          .build())
      type.addField(FieldSpec.builder(TypeName.INT, "foregroundGravity", Modifier.PRIVATE)
          .initializer("\$T.FILL", TypeNames.Android.Gravity)
          .build())
    }

    // Pull out the value
    initMethod.addStatement(
        "\$T foregroundTA = context.obtainStyledAttributes(attrs, \$T.styleable.ForegroundView)",
        TypeNames.Android.TypedArray,
        rClass)
    initMethod.addStatement(
        "final \$T localForeground = foregroundTA.getDrawable(\$T.styleable.ForegroundView_android_foreground)",
        TypeNames.Android.Drawable,
        rClass)

    initMethod.beginControlFlow("if (localForeground != null)")
    initMethod.addCode("//noinspection AndroidLintNewApi\n")
    initMethod.addStatement("setForeground(localForeground)")
    initMethod.endControlFlow()

    if (isLayout) {
      initMethod.addStatement(
          "foregroundGravity = foregroundTA.getInt(\$T.styleable.ForegroundView_android_foregroundGravity, " +
              "foregroundGravity)", rClass)
      initMethod.addStatement(
          "foregroundInPadding = foregroundTA.getBoolean(" +
              "\$T.styleable.ForegroundView_foregroundInsidePadding, " +
              "true)", rClass)
    }

    initMethod.addStatement("foregroundTA.recycle()")

    val onSizeChangedMethod = MethodSpec.methodBuilder("onSizeChanged")
        .addAnnotation(Override::class.java)
        .addModifiers(Modifier.PROTECTED)
        .addParameter(TypeName.INT, "w")
        .addParameter(TypeName.INT, "h")
        .addParameter(TypeName.INT, "oldw")
        .addParameter(TypeName.INT, "oldh")
        .addStatement("super.onSizeChanged(w, h, oldw, oldh)")

    if (isLayout) {
      onSizeChangedMethod.addStatement("foregroundBoundsChanged = true")
    } else {
      onSizeChangedMethod.beginControlFlow("if (foreground != null)")
          .addStatement("foreground.setBounds(0, 0, w, h)")
          .endControlFlow()
    }

    type.addMethod(onSizeChangedMethod.build())

    if (sourceType.endsWith("ImageView")) {
      type.addMethod(MethodSpec.methodBuilder("hasOverlappingRendering")
          .addAnnotation(Override::class.java)
          .addModifiers(Modifier.PUBLIC)
          .returns(TypeName.BOOLEAN)
          .addStatement("return false")
          .build())
    }

    if (isLayout) {
      type.addMethod(MethodSpec.methodBuilder("getForegroundGravity")
          .addJavadoc("""Describes how the foreground is positioned.

    @return foreground gravity.
    @see #setForegroundGravity(int)
    """)
          .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("value", "\$S", "MissingOverride").build())
          .addModifiers(Modifier.PUBLIC)
          .returns(TypeName.INT)
          .addStatement("return foregroundGravity")
          .build())

      type.addMethod(MethodSpec.methodBuilder("setForegroundGravity")
          .addJavadoc("""Describes how the foreground is positioned. Defaults to START and TOP.

    @param foregroundGravity See {@link android.view.Gravity}
    @see #getForegroundGravity()
    """)
          .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("value", "\$S", "MissingOverride").build())
          .addModifiers(Modifier.PUBLIC)
          .addParameter(TypeName.INT, "foregroundGravity")
          .beginControlFlow("if (this.foregroundGravity != foregroundGravity)")
          .beginControlFlow("if ((foregroundGravity & \$T.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0)",
              TypeNames.Android.Gravity)
          .addStatement("foregroundGravity |= \$T.START", TypeNames.Android.GravityCompat)
          .endControlFlow()
          .beginControlFlow("if ((foregroundGravity & \$T.VERTICAL_GRAVITY_MASK) == 0)",
              TypeNames.Android.Gravity)
          .addStatement("foregroundGravity |= \$T.TOP", TypeNames.Android.Gravity)
          .endControlFlow()
          .addStatement("this.foregroundGravity = foregroundGravity")
          .beginControlFlow("if (this.foregroundGravity == \$T.FILL && foreground != null)",
              TypeNames.Android.Gravity)
          .addStatement("\$T padding = new \$T()", TypeNames.Android.Rect, TypeNames.Android.Rect)
          .addStatement("foreground.getPadding(padding)")
          .endControlFlow()
          .addStatement("requestLayout()")
          .endControlFlow()
          .build())
    }

    type.addMethod(MethodSpec.methodBuilder("verifyDrawable")
        .addAnnotation(Override::class.java)
        .addModifiers(Modifier.PROTECTED)
        .returns(TypeName.BOOLEAN)
        .addParameter(TypeNames.Android.Drawable, "who")
        .addStatement("return super.verifyDrawable(who) || (who == foreground)")
        .build())

    type.addMethod(MethodSpec.methodBuilder("jumpDrawablesToCurrentState")
        .addAnnotation(Override::class.java)
        .addModifiers(Modifier.PUBLIC)
        .addStatement("super.jumpDrawablesToCurrentState()")
        .beginControlFlow("if (foreground != null)")
        .addStatement("foreground.jumpToCurrentState()")
        .endControlFlow()
        .build())

    type.addMethod(MethodSpec.methodBuilder("drawableStateChanged")
        .addAnnotation(Override::class.java)
        .addModifiers(Modifier.PROTECTED)
        .addStatement("super.drawableStateChanged()")
        .beginControlFlow("if (foreground != null && foreground.isStateful())")
        .addStatement("foreground.setState(getDrawableState())")
        .endControlFlow()
        .build())

    type.addMethod(MethodSpec.methodBuilder("getForeground")
        .addJavadoc("""Returns the drawable used as the foreground of this view. The
    foreground drawable, if non-null, is always drawn on top of the children.

    @return A Drawable or null if no foreground was set.
    """)
        .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("value", "\$S", "MissingOverride").build())
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeNames.Android.Drawable)
        .addStatement("return foreground")
        .build())

    val setForegroundMethod = MethodSpec.methodBuilder("setForeground")
        .addJavadoc("""Supply a Drawable that is to be rendered on top of all of the child
    views in this layout.  Any padding in the Drawable will be taken
    into account by ensuring that the children are inset to be placed
    inside of the padding area.

    @param drawable The Drawable to be drawn on top of the children.
    """)
        .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java).addMember("value", "\$S", "MissingOverride").build())
        .addAnnotation(AnnotationSpec.builder(ClassName.get("android.annotation", "SuppressLint"))
            .addMember("value", "\"NewApi\"")
            .build())
        .addModifiers(Modifier.PUBLIC)
        .addParameter(TypeNames.Android.Drawable, "drawable")
        .beginControlFlow("if (foreground != drawable)")
        .beginControlFlow("if (foreground != null)")
        .addStatement("foreground.setCallback(null)")
        .addStatement("unscheduleDrawable(foreground)")
        .endControlFlow()
        .addStatement("foreground = drawable")
        .beginControlFlow("if (drawable != null)")

    if (!isLayout) {
      setForegroundMethod.addStatement("foreground.setBounds(0, 0, getWidth(), getHeight())")
    }

    setForegroundMethod.addStatement("setWillNotDraw(false)")
        .addStatement("drawable.setCallback(this)")
        .beginControlFlow("if (drawable.isStateful())")
        .addStatement("drawable.setState(getDrawableState())")
        .endControlFlow()

    if (isLayout) {
      setForegroundMethod.beginControlFlow("if (foregroundGravity == \$T.FILL)", TypeNames.Android.Gravity)
      setForegroundMethod.addStatement("\$T padding = new \$T()", TypeNames.Android.Rect, TypeNames.Android.Rect)
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

    type.addMethod(setForegroundMethod.build())

    if (isLayout) {
      type.addMethod(MethodSpec.methodBuilder("onLayout")
          .addAnnotation(Override::class.java)
          .addModifiers(Modifier.PROTECTED)
          .addParameter(TypeName.BOOLEAN, "changed")
          .addParameter(TypeName.INT, "left")
          .addParameter(TypeName.INT, "top")
          .addParameter(TypeName.INT, "right")
          .addParameter(TypeName.INT, "bottom")
          .addStatement("super.onLayout(changed, left, top, right, bottom)")
          .beginControlFlow("if (changed)")
          .addStatement("foregroundBoundsChanged = true")
          .endControlFlow()
          .build())
    }

    val drawMethod = MethodSpec.methodBuilder("draw")
        .addAnnotation(Override::class.java)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(TypeNames.Android.Canvas, "canvas")
        .addStatement("super.draw(canvas)")
        .beginControlFlow("if (foreground != null)")

    if (isLayout) {
      drawMethod.addStatement("final \$T localForeground = foreground", TypeNames.Android.Drawable)
          .beginControlFlow("if (foregroundBoundsChanged)")
          .addStatement("foregroundBoundsChanged = false")
          .addStatement("final \$T localSelfBounds = selfBounds", TypeNames.Android.Rect)
          .addStatement("final \$T localOverlayBounds = overlayBounds", TypeNames.Android.Rect)
          .addStatement("final int w = getRight() - getLeft()")
          .addStatement("final int h = getBottom() - getTop()")
          .beginControlFlow("if (foregroundInPadding)")
          .addStatement("localSelfBounds.set(0, 0, w, h)")
          .nextControlFlow("else")
          .addStatement("localSelfBounds.set(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - " +
              "getPaddingBottom())")
          .endControlFlow()
          .addStatement("\$T.apply(foregroundGravity, localForeground.getIntrinsicWidth(), localForeground" +
              ".getIntrinsicHeight(), localSelfBounds, localOverlayBounds)", TypeNames.Android.Gravity)
          .addStatement("localForeground.setBounds(localOverlayBounds)")
          .endControlFlow()
          .addStatement("localForeground.draw(canvas)")
    } else {
      drawMethod.addStatement("foreground.draw(canvas)")
    }
    drawMethod.endControlFlow()
    type.addMethod(drawMethod.build())

    type.addMethod(MethodSpec.methodBuilder("drawableHotspotChanged")
        .addAnnotation(AnnotationSpec.builder(TypeNames.Annotations.TargetApi)
            .addMember("value", "\$L", "android.os.Build.VERSION_CODES.LOLLIPOP")
            .build())
        .addAnnotation(Override::class.java)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(TypeName.FLOAT, "x")
        .addParameter(TypeName.FLOAT, "y")
        .addStatement("super.drawableHotspotChanged(x, y)")
        .beginControlFlow("if (foreground != null)")
        .addStatement("foreground.setHotspot(x, y)")
        .endControlFlow()
        .build())
  }
}
