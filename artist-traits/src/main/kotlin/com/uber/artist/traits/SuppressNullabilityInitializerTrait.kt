package com.uber.artist.traits

import com.google.auto.service.AutoService
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec.Builder
import com.uber.artist.api.Trait

@AutoService(Trait::class)
class SuppressNullabilityInitializerTrait: Trait {
  override fun generateFor(
      type: Builder,
      initMethod: MethodSpec.Builder,
      rClass: ClassName,
      sourceType: String) {

    initMethod.addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java)
        .addMember("value", "\$S", "CheckNullabilityTypes")
        .build())
  }
}

