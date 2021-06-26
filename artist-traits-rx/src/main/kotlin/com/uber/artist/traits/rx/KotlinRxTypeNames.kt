/*
 * Copyright (C) 2018. Uber Technologies
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

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function

class KotlinRxTypeNames {
  class Rx {
    companion object {
      // Rx
      val Consumer = Consumer::class.asClassName()
      val Disposable = Disposable::class.asClassName()
      val Function = Function::class.asClassName()
      val Observable = Observable::class.asClassName()

      // RxRelay
      val BehaviorRelay = BehaviorRelay::class.asClassName()
      val PublishRelay = PublishRelay::class.asClassName()

      // RxBinding
      val RecyclerViewScrollEvent = ClassName("com.jakewharton.rxbinding3.recyclerview", "RecyclerViewScrollEvent")
      val RxView = ClassName("com.jakewharton.rxbinding3.view", "RxView")
      val RxCompoundButton = ClassName("com.jakewharton.rxbinding3.widget", "RxCompoundButton")
      val RxNestedScrollView = ClassName("com.jakewharton.rxbinding3.core", "RxNestedScrollView")
      val RxRecyclerView = ClassName("com.jakewharton.rxbinding3.recyclerview", "RxRecyclerView")
      val RxSearchView = ClassName("com.jakewharton.rxbinding3.appcompat", "RxSearchView")
      val RxSeekBar = ClassName("com.jakewharton.rxbinding3.widget", "RxSeekBar")
      val SeekBarChangeEvent = ClassName("com.jakewharton.rxbinding3.widget", "SeekBarChangeEvent")
      val SeekBarProgressChangeEvent = ClassName("com.jakewharton.rxbinding3.widget", "SeekBarProgressChangeEvent")
      val SeekBarStartChangeEvent = ClassName("com.jakewharton.rxbinding3.widget", "SeekBarStartChangeEvent")
      val RxSwipeRefreshLayout = ClassName("com.jakewharton.rxbinding3.swiperefreshlayout", "RxSwipeRefreshLayout")
      val RxTabLayout = ClassName("com.jakewharton.rxbinding3.material", "RxTabLayout")
      val RxTextView = ClassName("com.jakewharton.rxbinding3.widget", "RxTextView")
      val RxToolbar = ClassName("com.jakewharton.rxbinding3.widget", "RxToolbar")
      val RxViewPager = ClassName("com.jakewharton.rxbinding3.viewpager", "RxViewPager")
      val RxViewAttachEvent = ClassName("com.jakewharton.rxbinding3.view", "ViewAttachEvent")
      val RxViewAttachAttachedEvent = ClassName("com.jakewharton.rxbinding3.view", "ViewAttachAttachedEvent")
      val RxViewAttachDetachedEvent = ClassName("com.jakewharton.rxbinding3.view", "ViewAttachDetachedEvent")
      val SearchViewQueryTextEvent = ClassName("com.jakewharton.rxbinding3.widget", "SearchViewQueryTextEvent")
      val ViewScrollChangeEvent = ClassName("com.jakewharton.rxbinding3.view", "ViewScrollChangeEvent")
    }
  }
}
