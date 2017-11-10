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

import com.uber.artist.api.Trait;
import com.uber.artist.api.ViewStencil;
import com.uber.artist.api.ViewStencilProvider;
import com.uber.artist.traits.ForegroundTrait;
import com.uber.artist.traits.VisibilityTrait;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Sample ViewStencil provider.
 */
public class SampleViewStencilProvider implements ViewStencilProvider {

    @Override
    public Set<ViewStencil> stencils() {
        return new HashSet<>(Arrays.asList(
            new ViewStencil("android.support.v7.widget.AppCompatButton", 3, "buttonStyle"),
            new ViewStencil("android.support.v7.widget.AppCompatEditText", 3, "android.R.attr.editTextStyle"),
            new ViewStencil("android.widget.LinearLayout", 3, null),
            new ViewStencil("android.support.v7.widget.AppCompatImageView", 3, null),
            new ViewStencil("android.widget.TextView", 3, "android.R.attr.textViewStyle")
        ));
    }

    @Override
    public Set<Class<? extends Trait>> globalTraits() {
        return new HashSet<Class<? extends Trait>>(Arrays.asList(
            VisibilityTrait.class,
            ForegroundTrait.class
        ));
    }
}
