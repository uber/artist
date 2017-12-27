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
import com.uber.artist.api.TraitProvider;
import com.uber.artist.myproviders.trait.SampleTrait;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Sample ViewStencil provider.
 */
public class SampleTraitProvider implements TraitProvider {

  @Override
  public Set<Trait> traits() {
    return new LinkedHashSet<Trait>(Collections.singletonList(
        new SampleTrait()
    ));
  }
}
