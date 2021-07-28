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

package com.uber.artist.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast

import com.uber.artist.mylibrary.MyButton
import com.uber.artist.mylibrary.MyEditText
import com.uber.artist.mylibrary.MyImageView
import com.uber.artist.mylibrary.MyNestedScrollView
import com.uber.artist.mylibrary.MySwitch
import com.uber.artist.mylibrary.MyTextView

import java.util.concurrent.TimeUnit

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers

import android.widget.Toast.LENGTH_SHORT

/**
 * Sample activity.
 */
class MainActivity : AppCompatActivity() {

  private val button by lazy { findViewById<MyButton>(R.id.button) }
  private val editText by lazy { findViewById<MyEditText>(R.id.edittext) }
  private val imageView by lazy { findViewById<MyImageView>(R.id.image) }
  private val scrollView by lazy { findViewById<MyNestedScrollView>(R.id.scrollView) }
  private val textView by lazy { findViewById<MyTextView>(R.id.text) }
  private val toggle by lazy { findViewById<MySwitch>(R.id.toggle) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    demoArtistViewUsage()
  }

  @SuppressLint("CheckResult")
  private fun demoArtistViewUsage() {
    textView.sampleMethodFromCustomTrait()

    button.clicks()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { toast("Click from MyButton's clicks() stream!") }

    editText.textChanges()
        .skip(1)
        .debounce(200, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { charSequence: CharSequence ->
          toast("MyEditText's textChanges() stream sent: $charSequence")
        }

    scrollView.scrollChangeEvents()
        .debounce(200, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { toast("Scroll from MyScrollView's debounced scrollEvents() stream") }

    toggle.checkedChanges()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { isChecked: Boolean ->
          toast("MySwitch's checkedChanges() stream sent: $isChecked")
        }
  }

  private fun toast(msg: CharSequence) = Toast.makeText(this, msg, LENGTH_SHORT).show()
}
