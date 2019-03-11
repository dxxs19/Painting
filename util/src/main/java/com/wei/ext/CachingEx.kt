package com.wei.ext

import com.wei.util.DeviceUtil
import com.wei.util.FileUtil
import java.io.File

/**
 * 缓存管理者
 */
abstract class CachingEx {

    companion object {
        private const val NO_MEDIA_NAME = ".nomedia"
    }

    private val basePath: String by lazy {
        DeviceUtil.cachePath
    }

    init {
        makeDir(NO_MEDIA_NAME)
    }

    /**
     * @param dir 需要包含前置"/"分隔符
     */
    protected fun makeDir(dir: String): String? {
        val newPath = basePath + dir
        return if (ensureFileExist(newPath)) newPath else null
    }

    protected fun ensureFileExist(filePath: String): Boolean = FileUtil.ensureFileExist(filePath)

    protected fun ensureFileExist(file: File): Boolean = FileUtil.ensureFileExist(file)
}
