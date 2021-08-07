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
      val f = AliasTypeNames.Rx.RecyclerViewScrollEvent

      // Rx
      val Consumer = Consumer::class.asClassName()
      val Disposable = Disposable::class.asClassName()
      val Function = Function::class.asClassName()
      val Observable = Observable::class.asClassName()

      // RxRelay
      val BehaviorRelay = BehaviorRelay::class.asClassName()
      val PublishRelay = PublishRelay::class.asClassName()

      // RxBinding
      val RecyclerViewScrollEvent = AliasTypeNames.Rx.RecyclerViewScrollEvent
      val RxView = AliasTypeNames.Rx.RxView
      val RxCompoundButton = AliasTypeNames.Rx.RxCompoundButton
      val RxNestedScrollView = AliasTypeNames.Rx.RxNestedScrollView
      val RxRecyclerView = AliasTypeNames.Rx.RxRecyclerView
      val RxSearchView = AliasTypeNames.Rx.RxSearchView
      val RxSeekBar =  AliasTypeNames.Rx.RxSeekBar
      val SeekBarChangeEvent =  AliasTypeNames.Rx.SeekBarChangeEvent
      val SeekBarProgressChangeEvent =  AliasTypeNames.Rx.SeekBarProgressChangeEvent
      val SeekBarStartChangeEvent =  AliasTypeNames.Rx.SeekBarStartChangeEvent
      val RxSwipeRefreshLayout = AliasTypeNames.Rx.RxSwipeRefreshLayout
      val RxTabLayout = AliasTypeNames.Rx.RxTabLayout
      val RxTextView = AliasTypeNames.Rx.RxTextView
      val RxToolbar = AliasTypeNames.Rx.RxToolbar
      val RxViewPager = AliasTypeNames.Rx.RxViewPager
      val RxViewAttachEvent = AliasTypeNames.Rx.RxViewAttachEvent
      val RxViewAttachAttachedEvent = AliasTypeNames.Rx.RxViewAttachAttachedEvent
      val RxViewAttachDetachedEvent = AliasTypeNames.Rx.RxViewAttachDetachedEvent
      val SearchViewQueryTextEvent = AliasTypeNames.Rx.SearchViewQueryTextEvent
      val ViewScrollChangeEvent = AliasTypeNames.Rx.ViewScrollChangeEvent
    }
  }
}
