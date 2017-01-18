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

package com.chenupt.device.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.webkit.WebSettings
import com.chenupt.device.R
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        webView.settings.loadWithOverviewMode = true
        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webView.settings.useWideViewPort = true
        webView.loadUrl("http://chenupt.leanapp.cn/privacy-device")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
