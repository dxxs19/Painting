package com.wei.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.wei.ext.appContext
import kotlin.reflect.KClass

/**
 * PS: host仅限于activity或fragment
 *
 * @author yuansui
 */
object LaunchUtil {

    @JvmStatic
    fun startActivity(clz: Class<*>, vararg extras: Bundle) {
        val intent = Intent(appContext, clz)
        startActivity(intent, *extras)
    }

    fun startActivity(clz: KClass<*>, vararg extras: Bundle) {
        val intent = Intent(appContext, clz.java)
        startActivity(intent, *extras)
    }

    fun startActivity(intent: Intent, vararg extras: Bundle) {
        putExtras(intent, *extras)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(intent)
    }

    fun startActivity(host: Any?, intent: Intent, vararg extras: Bundle) {
        putExtras(intent, *extras)

        when (host) {
            is Activity -> host.startActivity(intent)
            is androidx.fragment.app.Fragment -> host.startActivity(intent)
            is Context -> {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                host.startActivity(intent)
            }
            else -> {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                appContext.startActivity(intent)
            }
        }
    }

    fun startActivity(host: Any?, clz: KClass<*>, vararg extras: Bundle) {
        val intent = Intent(appContext, clz.java)
        startActivity(host, intent, *extras)
    }

    fun startActivityForResult(host: Any?, intent: Intent, code: Int, vararg extras: Bundle) {
        putExtras(intent, *extras)

        when (host) {
            is Activity -> host.startActivityForResult(intent, code)
            is androidx.fragment.app.Fragment -> host.startActivityForResult(intent, code)
            else -> throw IllegalArgumentException("host can only be activity or fragment")
        }
    }

    fun startActivityForResult(host: Any?, clz: KClass<*>, code: Int, vararg extras: Bundle) {
        startActivityForResult(host, clz.java, code, *extras)
    }

    @JvmStatic
    fun startActivityForResult(host: Any?, clz: Class<*>, code: Int, vararg extras: Bundle) {
        val intent = Intent(appContext, clz)
        startActivityForResult(host, intent, code, *extras)
    }

    fun putExtras(intent: Intent, vararg extras: Bundle) {
        if (extras.isNotEmpty()) {
            extras.forEach { intent.putExtras(it) }
        }
    }
}
