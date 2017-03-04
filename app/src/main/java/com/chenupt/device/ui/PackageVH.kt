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

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.chenupt.device.R
import com.chenupt.device.bean.PackageInfo
import kotlinx.android.synthetic.main.vh_package.view.*

/**
 * Created by chenupt on 16/4/19.
 */
class PackageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    lateinit var pkg: PackageInfo

    companion object {
        fun createVH(parent: ViewGroup): PackageVH {
            return PackageVH(LayoutInflater.from(parent.context).inflate(R.layout.vh_package, parent, false))
        }
    }

    init {
        itemView.container.setOnClickListener { toAppInfo() }
    }

    fun bindData(pkg: PackageInfo) {
        this.pkg = pkg
        itemView.tvTitle.text = "${pkg.appName} (${pkg.versionName})"
        itemView.tvContent.text = pkg.packageName
        itemView.tvTargetVersion.text = getTargetSdkVersion(itemView.context, pkg.packageName)
        itemView.tvSize.text = "${DateFormat.format("yyyy-MM-dd HH:mm:ss", pkg.installTime)}" +
                "   ${android.text.format.Formatter.formatFileSize(itemView.context, pkg.size)}"
        //        itemView.ivIcon.setImageDrawable(itemView.context.packageManager.getApplicationIcon(pkg.title))

        Glide.with(itemView.context).load("").placeholder(itemView.context.packageManager.getApplicationIcon(pkg.packageName)).into(itemView.ivIcon)
    }

    fun toAppInfo() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${pkg.packageName}")
            itemView.context.startActivity(intent)
        } catch(e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    fun getTargetSdkVersion(context: Context, packageName: String?): String {
        try {
            val applicationInfo = context.packageManager.getApplicationInfo(packageName, 0)
            if (applicationInfo != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return "targetVersion: ${applicationInfo.targetSdkVersion}, minVersion: ${applicationInfo.minSdkVersion}"
                }
                return "targetVersion: ${applicationInfo.targetSdkVersion}"
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return "unknown"
    }
}

