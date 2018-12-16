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

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName

class KotlinRxTypeNames {
    class Rx {
        companion object {
            // Rx
            val Consumer = io.reactivex.functions.Consumer::class.asClassName()
            val Disposable = io.reactivex.disposables.Disposable::class.asClassName()
            val Function = io.reactivex.functions.Function::class.asClassName()
            val Observable = io.reactivex.Observable::class.asClassName()

            // RxRelay
            val BehaviorRelay = com.jakewharton.rxrelay2.BehaviorRelay::class.asClassName()
            val PublishRelay = com.jakewharton.rxrelay2.PublishRelay::class.asClassName()

            // RxBinding
            val RecyclerViewScrollEvent = ClassName("com.jakewharton.rxbinding2.support.v7.widget", "RecyclerViewScrollEvent")
            val RxView = ClassName("com.jakewharton.rxbinding2.view", "RxView")
            val RxCompoundButton = ClassName("com.jakewharton.rxbinding2.widget", "RxCompoundButton")
            val RxNestedScrollView = ClassName("com.jakewharton.rxbinding2.support.v4.widget", "RxNestedScrollView")
            val RxRecyclerView = ClassName("com.jakewharton.rxbinding2.support.v7.widget", "RxRecyclerView")
            val RxSearchView = ClassName("com.jakewharton.rxbinding2.support.v7.widget", "RxSearchView")
            val RxSeekBar = ClassName("com.jakewharton.rxbinding2.widget", "RxSeekBar")
            val SeekBarChangeEvent = ClassName("com.jakewharton.rxbinding2.widget", "SeekBarChangeEvent")
            val SeekBarProgressChangeEvent = ClassName("com.jakewharton.rxbinding2.widget", "SeekBarProgressChangeEvent")
            val SeekBarStartChangeEvent = ClassName("com.jakewharton.rxbinding2.widget", "SeekBarStartChangeEvent")
            val RxSwipeRefreshLayout = ClassName("com.jakewharton.rxbinding2.support.v4.widget", "RxSwipeRefreshLayout")
            val RxTabLayout = ClassName("com.jakewharton.rxbinding2.support.design.widget", "RxTabLayout")
            val RxTextView = ClassName("com.jakewharton.rxbinding2.widget", "RxTextView")
            val RxToolbar = ClassName("com.jakewharton.rxbinding2.support.v7.widget", "RxToolbar")
            val RxViewPager = ClassName("com.jakewharton.rxbinding2.support.v4.view", "RxViewPager")
            val RxViewAttachEvent = ClassName("com.jakewharton.rxbinding2.view", "ViewAttachEvent")
            val RxViewAttachAttachedEvent = ClassName("com.jakewharton.rxbinding2.view", "ViewAttachAttachedEvent")
            val RxViewAttachDetachedEvent = ClassName("com.jakewharton.rxbinding2.view", "ViewAttachDetachedEvent")
            val SearchViewQueryTextEvent = ClassName("com.jakewharton.rxbinding2.support.v7.widget", "SearchViewQueryTextEvent")
            val ViewScrollChangeEvent = ClassName("com.jakewharton.rxbinding2.view", "ViewScrollChangeEvent")
        }
    }
}
