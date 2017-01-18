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

package com.chenupt.device.bean

/**
 * Created by chenupt on 16/4/19.
 */
data class PackageInfo(val appName: String? = "unknown"
                       , val packageName: String? = "unknown"
                       , val installTime: Long
                       , var versionName: String? = "unknown"
                       , var versionCode: Int
                       , var size: Long)