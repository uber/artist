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
 *
 */

package com.uber.artist.myproviders.trait;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.uber.artist.api.Trait;
import com.uber.artist.myproviders.SampleTypeNames;

import javax.lang.model.element.Modifier;

/**
 * A somewhat arbitrary example of a custom Trait.
 */
public class SampleTrait implements Trait {

  @Override
  public void generateFor(TypeSpec.Builder type, MethodSpec.Builder initMethod, ClassName rClass,
      String sourceType) {

    type.addMethod(MethodSpec.methodBuilder("sampleMethodFromCustomTrait")
        .addModifiers(Modifier.PUBLIC)
        .returns(SampleTypeNames.VIEW)
        .addStatement("return this")
        .build()
    );
  }
}
