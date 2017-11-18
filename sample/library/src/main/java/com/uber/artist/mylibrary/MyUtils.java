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

package com.uber.artist.mylibrary;

import android.util.Log;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class MyUtils {
  public static Consumer<Object> createTapProcessor() {
    return new Consumer<Object>() {
      @Override
      public void accept(Object o) {
        Log.d("Artist", "Tapped a MyView");
      }
    };
  }

  public static Function<Object, Signal> createRxBindingSignalMapper() {
    return new Function() {
      @Override
      public Signal apply(Object o) throws Exception {
        return Signal.INSTANCE;
      }
    };
  }
}
