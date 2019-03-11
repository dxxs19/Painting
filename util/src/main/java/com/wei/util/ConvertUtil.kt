package com.wei.util

import com.wei.ext.safeClose
import java.io.*
import java.util.regex.Pattern

object ConvertUtil {

    /**
     * 获得指定文件的byte数组
     *
     * @param filePath
     * @return
     */
    fun fileToBytes(filePath: String): ByteArray? {
        var inputStream: FileInputStream? = null
        var outputStream: ByteArrayOutputStream? = null
        return try {
            inputStream = FileInputStream(File(filePath))
            outputStream = ByteArrayOutputStream()
            inputStream.copyTo(outputStream)
            outputStream.toByteArray()
        } catch (e: Exception) {
            null
        } finally {
            inputStream.safeClose()
            outputStream.safeClose()
        }
    }

    fun toString(any: Any): String {
        return when (any) {
            is Double -> java.lang.Double.toString(any)
            is Int -> Integer.toString(any)
            else -> this.toString()
        }
    }

    @Throws(IOException::class)
    fun toString(inputStream: InputStream): String {
        val outputStream = ByteArrayOutputStream()
        inputStream.copyTo(outputStream)
        return outputStream.toString("UTF-8").replace("\n", "")
    }


    /**
     * Unicode转 String 字符串
     *
     * @param text \u003d
     * @return
     */
    @JvmStatic
    fun unicodeToString(text: String): String {
        var str = text
        //  \p{XDigit} 匹配任何十六进制字符
        val pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))")
        val matcher = pattern.matcher(str)
        var ch: Char
        while (matcher.find()) {
            val group = matcher.group(2)
            ch = Integer.parseInt(group, 16).toChar()
            val group1 = matcher.group(1)
            str = str.replace(group1, ch + "")
        }
        return str
    }
}
