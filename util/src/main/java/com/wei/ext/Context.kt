package com.wei.ext

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wei.util.DeviceUtil
import com.wei.util.LaunchUtil
import kotlin.reflect.KClass

fun Context.inflate(res: Int, parent: ViewGroup? = null, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(this).inflate(res, parent, attachToRoot)
}

/**
 * 给Context拓展获取关于Context Service的manager
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@SuppressLint("ServiceCast")
inline fun <reified T> Context.systemService(): T {
    val clz = T::class
    return when (clz) {
        ConnectivityManager::class -> applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as T
        ActivityManager::class -> applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as T
        WifiManager::class -> applicationContext.getSystemService(Context.WIFI_SERVICE) as T
        PowerManager::class -> applicationContext.getSystemService(Context.POWER_SERVICE) as T
        NotificationManager::class -> applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as T
        ClipboardManager::class -> applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as T
        else -> {
            if (isCameraManager(clz)) {
                applicationContext.getSystemService(Context.CAMERA_SERVICE) as T
            } else {
                throw IllegalArgumentException("can not find class")
            }
        }
    }
}

fun isCameraManager(clz: KClass<*>): Boolean {
    return if (DeviceUtil.sdkVersion >= Build.VERSION_CODES.LOLLIPOP) {
        clz == CameraManager::class
    } else {
        @Suppress("DEPRECATION")
        clz == Camera::class
    }
}


/**
 * 获取当前进程名
 */
fun Context.currProcessName(): String? {
    val pid = Process.myPid()
    return systemService<ActivityManager>().runningAppProcesses
            .firstOrNull {
                it.pid == pid
            }
            ?.processName
}

/**
 * 是否为主进程
 */
fun Context.isMainProcess(): Boolean = packageName == currProcessName()

fun Context.startActivity(clz: KClass<*>, vararg extras: Bundle) {
    LaunchUtil.startActivity(this, clz, *extras)
}

fun Context.startActivity(intent: Intent, vararg extras: Bundle) {
    LaunchUtil.startActivity(this, intent, *extras)
}

fun Context.startService(clz: KClass<*>, vararg extras: Bundle) {
    val intent = Intent(this, clz.java)
    LaunchUtil.putExtras(intent, *extras)
    startService(intent)
}

fun Context.startService(intent: Intent, vararg extras: Bundle) {
    LaunchUtil.putExtras(intent, *extras)
    startService(intent)
}
