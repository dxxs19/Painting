package com.wei.ext

const val EMPTY = ""

object Constants {
    const val ERR_NOT_FOUND = -1
    const val ERR_DEFAULT = -1
    const val INVALID_INT = Int.MIN_VALUE
    const val INVALID_FLOAT = INVALID_INT.toFloat()

    // 魅族品牌
    const val BRAND_MEIZU = "Meizu"

    const val INTERPOLATOR_MAX = 1.0f

    /**
     * alpha
     */
    const val ALPHA_MAX = 255L
    const val ALPHA_MIN = 0L
}

enum class FileSuffix(val text: String) {
    TXT(".txt"),
    XML(".xml"),
    HTML(".html"),
    JPEG(".jpg"),
    PNG(".png"),
    JS(".js"),
    PPT(".ppt"),
    PPTX(".pptx"),
    PDF(".pdf"),
    AMR(".amr"),
    MP4(".mp4"),
}