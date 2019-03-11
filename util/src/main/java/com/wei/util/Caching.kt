package com.wei.util

import com.wei.ext.CachingEx


/**
 * @author CaiXiang
 * @since 2018/10/22
 */
object Caching : CachingEx() {

    private const val CAMERA_CACHE_DISK_NAME = "/camera/"
    private const val DOWNLOAD_CACHE_NAME = "/download"
    const val BMP_CACHE_DISK_NAME = "/bmp/"

    var cameraCacheDir: String? = null
        private set

    var bmpCacheDir: String? = null
        private set

    var downloadCacheDir: String? = null
        private set

    init {
        cameraCacheDir = makeDir(CAMERA_CACHE_DISK_NAME)
        bmpCacheDir = makeDir(BMP_CACHE_DISK_NAME)
        downloadCacheDir = makeDir(DOWNLOAD_CACHE_NAME)
    }
}
