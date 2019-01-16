package com.uber.artist.traits

import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec.Builder
import com.uber.artist.api.JavaTrait
import com.uber.artist.api.TypeNames
import javax.lang.model.element.Modifier

@AutoService(JavaTrait::class)
class JavaVisibilityTrait : JavaTrait {
  override fun generateFor(
      type: Builder,
      initMethod: MethodSpec.Builder,
      rClass: ClassName,
      sourceType: String) {

    // Visibility convenience methods
    arrayOf("visible", "invisible", "gone")
        .forEach { type.addMethod(createVisibilityConvenienceMethod(it)) }
  }

  private fun createVisibilityConvenienceMethod(type: String): MethodSpec {
    return MethodSpec.methodBuilder("is${type.capitalize()}")
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeName.BOOLEAN)
        .addStatement("return getVisibility() == \$T.${type.toUpperCase()}", TypeNames.Android.View)
        .build()
  }
}
