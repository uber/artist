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

import com.squareup.javapoet.ClassName;

/**
 * Sample TypeNames.
 */
public final class JavaSampleTypeNames {
  public static final ClassName MY_UTILS = ClassName.get("com.uber.artist.mylibrary", "MyUtils");
  public static final ClassName SIGNAL = ClassName.get("com.uber.artist.mylibrary", "Signal");
  public static final ClassName VIEW = ClassName.get("android.view", "View");

  private JavaSampleTypeNames() { }
}
