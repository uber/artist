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

package com.uber.artist.traits.rx

import com.squareup.javapoet.ClassName

class JavaRxTypeNames {
  class Rx {
    companion object {
      // Rx
      val Consumer = ClassName.get(io.reactivex.functions.Consumer::class.java)
      val Disposable = ClassName.get(io.reactivex.disposables.Disposable::class.java)
      val Function = ClassName.get(io.reactivex.functions.Function::class.java)
      val Observable = ClassName.get(io.reactivex.Observable::class.java)

      // RxRelay
      val BehaviorRelay = ClassName.get(com.jakewharton.rxrelay2.BehaviorRelay::class.java)
      val PublishRelay = ClassName.get(com.jakewharton.rxrelay2.PublishRelay::class.java)

      // RxBinding
      val RecyclerViewScrollEvent = ClassName.get("com.jakewharton.rxbinding3.recyclerview", "RecyclerViewScrollEvent")
      val RxView = ClassName.get("com.jakewharton.rxbinding3.view", "RxView")
      val RxCompoundButton = ClassName.get("com.jakewharton.rxbinding3.widget", "RxCompoundButton")
      val RxNestedScrollView = ClassName.get("com.jakewharton.rxbinding3.core", "RxNestedScrollView")
      val RxRecyclerView = ClassName.get("com.jakewharton.rxbinding3.recyclerview", "RxRecyclerView")
      val RxSearchView = ClassName.get("com.jakewharton.rxbinding3.appcompat", "RxSearchView")
      val RxSeekBar = ClassName.get("com.jakewharton.rxbinding3.widget", "RxSeekBar")
      val SeekBarChangeEvent = ClassName.get("com.jakewharton.rxbinding3.widget", "SeekBarChangeEvent")
      val SeekBarProgressChangeEvent = ClassName.get("com.jakewharton.rxbinding3.widget", "SeekBarProgressChangeEvent")
      val SeekBarStartChangeEvent = ClassName.get("com.jakewharton.rxbinding3.widget", "SeekBarStartChangeEvent")
      val RxSwipeRefreshLayout = ClassName.get("com.jakewharton.rxbinding3.swiperefreshlayout", "RxSwipeRefreshLayout")
      val RxTabLayout = ClassName.get("com.jakewharton.rxbinding3.material", "RxTabLayout")
      val RxTextView = ClassName.get("com.jakewharton.rxbinding3.widget", "RxTextView")
      val RxToolbar = ClassName.get("com.jakewharton.rxbinding3.widget", "RxToolbar")
      val RxViewPager = ClassName.get("com.jakewharton.rxbinding3.viewpager", "RxViewPager")
      val RxViewAttachEvent = ClassName.get("com.jakewharton.rxbinding3.view", "ViewAttachEvent")
      val RxViewAttachAttachedEvent = ClassName.get("com.jakewharton.rxbinding3.view", "ViewAttachAttachedEvent")
      val RxViewAttachDetachedEvent = ClassName.get("com.jakewharton.rxbinding3.view", "ViewAttachDetachedEvent")
      val SearchViewQueryTextEvent = ClassName.get("com.jakewharton.rxbinding3.appcompat", "SearchViewQueryTextEvent")
      val ViewScrollChangeEvent = ClassName.get("com.jakewharton.rxbinding3.view", "ViewScrollChangeEvent")
    }
  }
}
