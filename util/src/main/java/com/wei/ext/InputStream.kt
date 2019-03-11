package com.wei.ext

import java.io.ByteArrayOutputStream
import java.io.InputStream

fun InputStream.string(): String {
    return ByteArrayOutputStream().safeUse {
        if (available() == 0) {
            copyTo(it)
            String(it.toByteArray()).replace("\n", "")
        } else {
            val bytes = ByteArray(available())
            read(bytes)
            it.write(bytes)
            String(bytes).replace("\n", "")
        }
    } ?: EMPTY
}