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

package com.chenupt.device.adapter

import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.util.SortedListAdapterCallback
import android.view.ViewGroup
import com.chenupt.device.bean.PackageInfo
import com.chenupt.device.ui.PackageVH

/**
 * Created by chenupt on 16/4/19.
 */
class RecyclerAdapter : RecyclerView.Adapter<PackageVH>() {

    var items = SortedList<PackageInfo>(PackageInfo::class.java, Callback(this))

    override fun onBindViewHolder(holder: PackageVH?, position: Int) {
        holder?.bindData(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageVH? {
        return PackageVH.createVH(parent)
    }

    override fun getItemCount(): Int = items.size()

    fun updateData(data: List<PackageInfo>) {
        items.beginBatchedUpdates()
        items.clear()
        for (packageInfo in data) {
            items.add(packageInfo)
        }
        items.endBatchedUpdates()

        notifyItemRangeChanged(0, itemCount)
    }

}

class Callback(adapter: RecyclerView.Adapter<PackageVH>) : SortedListAdapterCallback<PackageInfo?>(adapter){
    override fun areContentsTheSame(oldItem: PackageInfo?, newItem: PackageInfo?): Boolean {
        return oldItem?.installTime == newItem?.installTime
    }

    override fun compare(o1: PackageInfo?, o2: PackageInfo?): Int {
        o1?:return -1
        o2?:return -1
        return -o1.installTime.compareTo(o2.installTime)
    }

    override fun areItemsTheSame(item1: PackageInfo?, item2: PackageInfo?): Boolean {
        return item1?.packageName == item2?.packageName
    }

}