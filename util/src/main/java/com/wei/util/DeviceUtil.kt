package com.wei.util

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.os.StrictMode
import android.provider.Settings
import android.provider.Settings.Secure
import androidx.core.net.ConnectivityManagerCompat
import com.wei.ext.appContext
import com.wei.ext.systemService
import java.io.File
import java.util.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
object DeviceUtil {

    val tag: String?
        get() = this::class.simpleName

    val isNetworkEnabled: Boolean
        get() {
            val connectivityManager = appContext.systemService() as ConnectivityManager
            return if (connectivityManager.activeNetworkInfo == null)
                false
            else
                connectivityManager.activeNetworkInfo.isConnected
        }

    /**
     * 对大数据传输时，需要调用该方法做出判断，如果流量敏感，应该提示用户
     *
     * @return true表示流量敏感，false表示不敏感
     */
    val isActiveNetworkMetered: Boolean
        get() = ConnectivityManagerCompat.isActiveNetworkMetered(appContext.systemService())

    /**
     * sdcard是否装好
     */
    val isSdcardMounted: Boolean
        get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    /**
     * sdcard是否可用
     *
     * @return
     */
    val isSdcardEnable: Boolean
        get() = isSdcardMounted && Environment.getExternalStorageState() != Environment.MEDIA_SHARED

    val wifiInfo: WifiInfo
        get() = appContext.systemService<WifiManager>().connectionInfo

    val runtimeMaxMemory: Long
        get() = Runtime.getRuntime().maxMemory()

    /**
     * 有效存储路径
     * 兼容6.0的文件动态权限问题, 尽量不进行申请, 某些机型申请不过
     * FIXME: 某些机型在sdcard enable的情况下并不能获取到ExternalCacheDir
     */
    val cachePath: String
        get() {
            var diskDir = if (isSdcardEnable) {
                // 路径: sdcard/Android/data/包名/cache
                appContext.externalCacheDir
            } else {
                appContext.cacheDir
            }

            if (diskDir == null) {
                diskDir = sdcardDir
            }

            return diskDir.path
        }

    /**
     * 获取当前SDK的版本号
     *
     * @return
     */
    val sdkVersion: Int
        get() = Build.VERSION.SDK_INT

    val brand: String
        get() = Build.BRAND

    // 获取手机型号
    val mobileType: String
        get() = Build.MODEL.replace(" ", "")

    /**
     * 获取系统版本号
     *
     * @return
     */
    val systemVersion: String
        get() = Build.VERSION.RELEASE

    val sdcardDir: File
        get() {
            val dir = Environment.getExternalStorageDirectory()
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return dir
        }

    /**
     * 是否>=6.0
     */
    val isOverMarshmallow: Boolean
        get() = Build.VERSION.SDK_INT >= VERSION_CODES.M

    @Suppress("DEPRECATION")
    val isWifi: Boolean
        get() = appContext.systemService<ConnectivityManager>().activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI

    val isAirplaneModeOn: Boolean
        get() = 0 != Settings.System.getInt(appContext.contentResolver, "airplane_mode_on", 0)

    var clipboardText: String? = null
        get() {
            val cm = appContext.systemService<ClipboardManager>()
            return cm.primaryClip?.getItemAt(0)?.text?.let {
                it.toString()
            }
        }

    /**
     * 获取android id
     *
     * @return
     */
    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String {
        return Secure.getString(context.contentResolver, Secure.ANDROID_ID)
    }

    /**
     * 设置资源加载的语言版本
     *
     * @param context
     * @param l
     */
    @Suppress("DEPRECATION")
    @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
    fun setResLocale(context: Context, l: Locale) {
        val resources = context.resources
        val config = resources.configuration
        if (sdkVersion >= VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(l)
        } else {
            config.locale = l
        }
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR2)
    fun closeStickMode() {
        if (DeviceUtil.sdkVersion >= Build.VERSION_CODES.N) {
            // 7.0打开相机,https://stackoverflow.com/questions/42251634/android-os-fileuriexposedexception-file-jpg-exposed-beyond-app-through-clipdata
            val builder = StrictMode.VmPolicy.Builder()
            builder.detectFileUriExposure()
            StrictMode.setVmPolicy(builder.build())
        }
    }
}
