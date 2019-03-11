package com.wei.ext

import java.io.Closeable
import java.io.IOException

/**
 * 安全关闭
 */
fun Closeable?.safeClose() {
    this?.apply {
        try {
            close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

/**
 * 安全使用
 */
inline fun <T : Closeable?, R> T.safeUse(block: (T) -> R): R? {
    return try {
        use(block)
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }
}