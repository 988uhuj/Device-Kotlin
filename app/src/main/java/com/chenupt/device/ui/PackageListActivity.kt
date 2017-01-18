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

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.chenupt.device.R
import com.chenupt.device.adapter.RecyclerAdapter
import com.chenupt.device.bean.PackageInfo
import com.chenupt.device.d
import kotlinx.android.synthetic.main.activity_package_list.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.*

/**
 * Created by chenupt on 16/4/19.
 */
class PackageListActivity : AppCompatActivity() {

    lateinit var adapter: RecyclerAdapter
    lateinit var data: List<PackageInfo>
    var filter = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = RecyclerAdapter()
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.itemAnimator = object : DefaultItemAnimator() {
            override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
                return true
            }
        }
        recyclerview.adapter = adapter

        refreshLayout.setOnRefreshListener { load(true) }
        refreshLayout.post { refreshLayout.isRefreshing = true }

        load(true)
    }


    fun load(update: Boolean) {
        async() {
            if (update) {
                data = getAllPackage()
            }
            var result = data
            if (filter.isNotBlank()) {
                result = data.filter {
                    it.packageName?.contains(filter) ?: false || it.appName?.contains(filter) ?: false
                }
            }
            uiThread {
                adapter.updateData(result)
                supportActionBar?.title = "AppList (${result.size})"
                refreshLayout.isRefreshing = false
            }
        }
    }

    fun getAllPackage(): List<PackageInfo> {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pkgAppsList = packageManager.getInstalledPackages(0)

        val dataList = ArrayList<PackageInfo>()
        for (appInfo in pkgAppsList) {
            appInfo ?: continue
            dataList.add(PackageInfo(packageManager.getApplicationLabel(appInfo.applicationInfo).toString(),
                    appInfo.packageName,
                    File(appInfo.applicationInfo.sourceDir).lastModified(),
                    appInfo.versionName,
                    appInfo.versionCode,
                    File(appInfo.applicationInfo.sourceDir).length()))
        }

        d("size: ${dataList.size}")
        return dataList
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_app, menu)
        val menuItem = menu?.findItem(R.id.action_search)
        val searchView = menuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(q: String): Boolean {
                filter = q
                load(false)
                return true
            }

            override fun onQueryTextSubmit(q: String): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }


}
