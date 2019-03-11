package com.wei.ext

import android.graphics.*

/**
 * 设置canvas抗锯齿
 */
fun Canvas.setAntialias() {
    if (drawFilter == null) {
        drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    }
}

fun Canvas.removeFilter() {
    drawFilter = null
}

fun Canvas.clear() {
    drawPaint(Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) })
    //        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
}