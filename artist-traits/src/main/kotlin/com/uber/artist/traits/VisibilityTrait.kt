package com.uber.artist.traits

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec.Builder
import com.uber.artist.api.Trait
import com.uber.artist.api.TypeNames
import javax.lang.model.element.Modifier

class VisibilityTrait : Trait {
    override fun generateFor(
        type: Builder,
        initMethod: MethodSpec.Builder,
        rClass: ClassName,
        baseType: String) {

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
