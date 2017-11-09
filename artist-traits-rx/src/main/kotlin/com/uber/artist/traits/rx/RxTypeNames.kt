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

class RxTypeNames {
    class Rx {
        companion object {
            // Rx
            val Consumer: ClassName = ClassName.get(io.reactivex.functions.Consumer::class.java)
            val Disposable: ClassName = ClassName.get(io.reactivex.disposables.Disposable::class.java)
            val Function: ClassName = ClassName.get(io.reactivex.functions.Function::class.java)
            val Observable: ClassName = ClassName.get(io.reactivex.Observable::class.java)

            // RxRelay
            val BehaviorRelay: ClassName = ClassName.get(com.jakewharton.rxrelay2.BehaviorRelay::class.java)
            val PublishRelay: ClassName = ClassName.get(com.jakewharton.rxrelay2.PublishRelay::class.java)

            // RxBinding
            val RecyclerViewScrollEvent : ClassName = ClassName.get("com.jakewharton.rxbinding2.support.v7.widget", "RecyclerViewScrollEvent")
            val RxView: ClassName = ClassName.get("com.jakewharton.rxbinding2.view", "RxView")
            val RxCompoundButton: ClassName = ClassName.get("com.jakewharton.rxbinding2.widget", "RxCompoundButton")
            val RxNestedScrollView: ClassName = ClassName.get("com.jakewharton.rxbinding2.support.v4.widget", "RxNestedScrollView")
            val RxRecyclerView: ClassName = ClassName.get("com.jakewharton.rxbinding2.support.v7.widget", "RxRecyclerView")
            val RxSearchView: ClassName = ClassName.get("com.jakewharton.rxbinding2.support.v7.widget", "RxSearchView")
            val RxSeekBar: ClassName = ClassName.get("com.jakewharton.rxbinding2.widget", "RxSeekBar")
            val SeekBarChangeEvent: ClassName = ClassName.get("com.jakewharton.rxbinding2.widget", "SeekBarChangeEvent")
            val SeekBarProgressChangeEvent: ClassName = ClassName.get("com.jakewharton.rxbinding2.widget", "SeekBarProgressChangeEvent")
            val SeekBarStartChangeEvent: ClassName = ClassName.get("com.jakewharton.rxbinding2.widget", "SeekBarStartChangeEvent")
            val RxSwipeRefreshLayout: ClassName = ClassName.get("com.jakewharton.rxbinding2.support.v4.widget", "RxSwipeRefreshLayout")
            val RxTabLayout: ClassName = ClassName.get("com.jakewharton.rxbinding2.support.design.widget", "RxTabLayout")
            val RxTextView: ClassName = ClassName.get("com.jakewharton.rxbinding2.widget", "RxTextView")
            val RxToolbar: ClassName = ClassName.get("com.jakewharton.rxbinding2.support.v7.widget", "RxToolbar")
            val RxViewPager: ClassName = ClassName.get("com.jakewharton.rxbinding2.support.v4.view", "RxViewPager")
            val RxViewAttachEvent = ClassName.get("com.jakewharton.rxbinding2.view", "ViewAttachEvent")
            val RxViewAttachAttachedEvent = ClassName.get("com.jakewharton.rxbinding2.view", "ViewAttachAttachedEvent")
            val RxViewAttachDetachedEvent = ClassName.get("com.jakewharton.rxbinding2.view", "ViewAttachDetachedEvent")
            val SearchViewQueryTextEvent: ClassName = ClassName.get("com.jakewharton.rxbinding2.support.v7.widget", "SearchViewQueryTextEvent")
            val ViewScrollChangeEvent: ClassName = ClassName.get("com.jakewharton.rxbinding2.view", "ViewScrollChangeEvent")
        }
    }
}
