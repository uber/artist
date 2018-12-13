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

package com.uber.artist.myproviders;

import com.google.auto.service.AutoService;
import com.uber.artist.api.JavaTrait;
import com.uber.artist.api.JavaViewStencil;
import com.uber.artist.api.JavaViewStencilProvider;
import com.uber.artist.myproviders.trait.JavaSampleTrait;
import com.uber.artist.traits.ForegroundTrait;
import com.uber.artist.traits.VisibilityTrait;
import com.uber.artist.traits.rx.CheckableTrait;
import com.uber.artist.traits.rx.ScrollableTrait;
import com.uber.artist.traits.rx.TextInputTrait;
import com.uber.artist.traits.rx.ViewTrait;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Sample ViewStencil provider.
 */
@AutoService(JavaViewStencilProvider.class)
public class JavaSampleViewStencilProvider implements JavaViewStencilProvider {

  @Override
  public Set<JavaViewStencil> stencils() {
    return new LinkedHashSet<>(Arrays.asList(
        new JavaViewStencil("androidx.appcompat.widget.AppCompatButton", 3, "buttonStyle"),
        new JavaViewStencil("androidx.appcompat.widget.AppCompatEditText", 3,
            "android.R.attr.editTextStyle", TextInputTrait.class),
        new JavaViewStencil("android.widget.LinearLayout", 3, null),
        new JavaViewStencil("androidx.appcompat.widget.AppCompatImageView", 3, null),
        new JavaViewStencil("androidx.core.widget.NestedScrollView", 3, null, ScrollableTrait.class),
        new JavaViewStencil("android.widget.TextView", 3, "android.R.attr.textViewStyle"),
        new SwitchStencil()
    ));
  }

  @Override
  public Set<Class<? extends JavaTrait>> globalTraits() {
    return new LinkedHashSet<>(Arrays.asList(
        JavaSampleTrait.class,
        VisibilityTrait.class,
        ForegroundTrait.class,
        ViewTrait.class
    ));
  }

  private static class SwitchStencil extends JavaViewStencil {
    public SwitchStencil() {
      super("androidx.appcompat.widget.SwitchCompat", 3, "switchStyle", CheckableTrait.class);
    }

    @Override
    public String name() {
      return "MySwitch";
    }
  }
}
