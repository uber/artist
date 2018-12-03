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

package com.uber.artist.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.jakewharton.rxbinding3.view.ViewScrollChangeEvent;
import com.uber.artist.mylibrary.MyButton;
import com.uber.artist.mylibrary.MyEditText;
import com.uber.artist.mylibrary.MyImageView;
import com.uber.artist.mylibrary.MyNestedScrollView;
import com.uber.artist.mylibrary.MySwitch;
import com.uber.artist.mylibrary.MyTextView;
import com.uber.artist.mylibrary.Signal;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Sample activity.
 */
public class MainActivity extends AppCompatActivity {

  private MyButton button;
  private MyEditText editText;
  private MyImageView imageView;
  private MyNestedScrollView scrollView;
  private MyTextView textView;
  private MySwitch toggle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    button = findViewById(R.id.button);
    editText = findViewById(R.id.edittext);
    imageView = findViewById(R.id.image);
    scrollView = findViewById(R.id.scrollView);
    textView = findViewById(R.id.text);
    toggle = findViewById(R.id.toggle);
    demoArtistViewUsage();
  }

  private void demoArtistViewUsage() {
    textView.sampleMethodFromCustomTrait();

    button.clicks()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Signal>() {
          @Override
          public void accept(Signal signal) throws Exception {
            toast("Click from MyButton's clicks() stream!");
          }
        });

    editText.textChanges()
        .skip(1)
        .debounce(200, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<CharSequence>() {
          @Override
          public void accept(CharSequence charSequence) throws Exception {
            toast("MyEditText's textChanges() stream sent: " + charSequence);
          }
        });

    scrollView.scrollChangeEvents()
        .debounce(200, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<ViewScrollChangeEvent>() {
          @Override
          public void accept(ViewScrollChangeEvent event) throws Exception {
            toast("Scroll from MyScrollView's debounced scrollEvents() stream");
          }
        });

    toggle.checkedChanges()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Boolean>() {
          @Override
          public void accept(Boolean isChecked) throws Exception {
            toast("MySwitch's checkedChanges() stream sent: " + isChecked);
          }
        });
  }

  private void toast(CharSequence msg) {
    Toast.makeText(this, msg, LENGTH_SHORT).show();
  }
}
