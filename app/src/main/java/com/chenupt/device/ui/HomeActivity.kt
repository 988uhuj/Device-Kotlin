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

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.*
import com.chenupt.device.R
import com.chenupt.device.d
import com.chenupt.device.navigate
import kotlinx.android.synthetic.main.activity_home.*
import java.net.NetworkInterface
import java.net.SocketException
import java.text.NumberFormat
import java.util.*

/**
 * Created by chenupt on 16/4/6.
 */
class HomeActivity : AppCompatActivity() {

    var metrics = DisplayMetrics()
    var abi: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initAbi()
        initMetrics()

        ItemView(this).setData(getString(R.string.model), Build.MODEL).attach(vgBasic)
        ItemView(this).setData(getString(R.string.brand), Build.BRAND).attach(vgBasic)
        ItemView(this).setData(getString(R.string.system_version), "${Build.VERSION.RELEASE}(${Build.VERSION.SDK_INT})").attach(vgBasic)
        ItemView(this).setData(getString(R.string.cpu_count), "${Runtime.getRuntime().availableProcessors()}").attach(vgBasic)
        ItemView(this).setData(getString(R.string.abi), abi).attach(vgBasic)

        ItemView(this).setData(getString(R.string.size), getScreenInches()).attach(vgScreen)
        ItemView(this).setData(getString(R.string.resolution), screenWH()).attach(vgScreen)
        ItemView(this).setData(getString(R.string.dpi), "${metrics.densityDpi}").attach(vgScreen)
        ItemView(this).setData(getString(R.string.x_dpi), "${metrics.xdpi}").attach(vgScreen)
        ItemView(this).setData(getString(R.string.y_dpi), "${metrics.ydpi}").attach(vgScreen)
        ItemView(this).setData(getString(R.string.density), "${metrics.density}").attach(vgScreen)
        ItemView(this).setData(getString(R.string.status_height), "${getStatusBarHeight()}px").attach(vgScreen)
        ItemView(this).setData(getString(R.string.navigation_height), "${if (hasSoftKeys()) getNavigationBarHeight() else 0}px").attach(vgScreen)

        ItemView(this).setData(getString(R.string.ip), getHostIp()).attach(vgNet)
        ItemView(this).setData(getString(R.string.connect_type), connectType()).attach(vgNet)

        ItemView(this).setData(getString(R.string.memory), getTotalMemory()).attach(vgStorage)
        ItemView(this).setData(getString(R.string.max_memory), android.text.format.Formatter.formatFileSize(this, Runtime.getRuntime().maxMemory())).attach(vgStorage)
        ItemView(this).setData(getString(R.string.total_memory), android.text.format.Formatter.formatFileSize(this, Runtime.getRuntime().totalMemory())).attach(vgStorage)
        ItemView(this).setData(getString(R.string.free_memory), android.text.format.Formatter.formatFileSize(this, Runtime.getRuntime().freeMemory())).attach(vgStorage)
        ItemView(this).setData(getString(R.string.system), readSystem()).attach(vgStorage)
        ItemView(this).setData(getString(R.string.sd), readSDCard()).attach(vgStorage)
    }

    fun initMetrics() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.defaultDisplay.getRealMetrics(metrics)
        } else {
            windowManager.defaultDisplay.getMetrics(metrics)
        }
    }

    fun initAbi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abi = Arrays.toString(Build.SUPPORTED_ABIS)
        } else {
            abi = "${Build.CPU_ABI}, ABI2: ${Build.CPU_ABI2}"
        }
    }

    fun screenWH(): String {
        return "w: ${metrics.widthPixels}px, h: ${metrics.heightPixels}px"
    }

    fun connectType(): String {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork?.typeName ?: "No Connection"
    }

    fun getHostIp(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            for (e in en) {
                val addresses = e.inetAddresses
                for (address in addresses) {
                    d(address.hostAddress + "," + address.isLoopbackAddress + "," + address.isSiteLocalAddress)
                    if (!address.isLoopbackAddress && address.isSiteLocalAddress) {
                        return address.hostAddress;
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "null"
    }

    fun getStatusBarHeight(): Int {
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resId > 0) resources.getDimensionPixelSize(resId) else 0
    }

    fun getNavigationBarHeight(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            val hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey()
            val resId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return if (resId > 0 && !hasMenuKey) resources.getDimensionPixelSize(resId) else 0
        } else {
            return 0
        }
    }

    fun getTotalMemory(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            am.getMemoryInfo(memoryInfo)
            d("totalMem: ${memoryInfo.totalMem}")
            return android.text.format.Formatter.formatFileSize(this, memoryInfo.totalMem)
        } else {
            return ""
        }
    }

    fun hasSoftKeys(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return false
        }
        val hasSoftwareKeys: Boolean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            val m = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(m)

            val rm = DisplayMetrics()
            windowManager.defaultDisplay.getRealMetrics(rm)

            val displayHeight = m.heightPixels
            val displayWidth = m.widthPixels

            val realHeight = rm.heightPixels
            val realWidth = rm.widthPixels

            hasSoftwareKeys = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0
        } else {
            val hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey()
            val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
            hasSoftwareKeys = !hasMenuKey && !hasBackKey
        }
        return hasSoftwareKeys
    }

    fun readSystem(): String {
        val root = Environment.getRootDirectory()
        val sf = StatFs(root.path)
        val blockSize = sf.blockSizeLong
        val blockCount = sf.blockCountLong
        val availCount = sf.availableBlocksLong
        return "${format(availCount * blockSize)} / ${format(blockSize * blockCount)}"
    }

    fun readSDCard(): String {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            val sdcardDir = Environment.getExternalStorageDirectory()
            val sf = StatFs(sdcardDir.path)
            val blockSize = sf.blockSizeLong
            val blockCount = sf.blockCountLong
            val availCount = sf.availableBlocksLong
            return "${format(availCount * blockSize)} / ${format(blockSize * blockCount)}"
        } else {
            return "empty"
        }
    }

    fun getScreenInches(): String {
        val x = Math.pow((metrics.widthPixels / metrics.xdpi).toDouble(), 2.toDouble());
        val y = Math.pow((metrics.heightPixels / metrics.ydpi).toDouble(), 2.toDouble());
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 2
        return "${numberFormat.format(Math.sqrt(x+y))}"
    }

    fun format(size: Long): String {
        return android.text.format.Formatter.formatFileSize(this, size)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_about -> navigate<AboutActivity>()
            R.id.action_help -> navigate<WebViewActivity>()
            R.id.action_pkg -> navigate<PackageListActivity>()
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

}