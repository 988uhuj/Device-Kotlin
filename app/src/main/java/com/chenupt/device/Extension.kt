/*
 * Copyright 2016 chenupt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chenupt.device

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * Created by chenupt on 16/3/30.
 */
fun Context.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, "Debug: $text", duration).show()
}

inline fun <reified T : Activity> Activity.navigate(id: String = "") {
    val intent = Intent(this, T::class.java)
    intent.putExtra("id", id)
    startActivity(intent)
}

fun d(message: String, tag: String = "kotlin") {
    Log.d(tag, message)
}