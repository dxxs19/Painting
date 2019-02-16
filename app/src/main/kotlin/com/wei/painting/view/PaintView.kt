package com.wei.painting.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ImageView

/**
 *
 * @author XiangWei
 * @since 2019/2/16
 */
class PaintView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?= null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private lateinit var bufferBitmap: Bitmap
    private lateinit var bufferCanvas: Canvas

    init {
        initPaint()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        bufferBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        bufferCanvas = Canvas(bufferBitmap)
    }

    private fun initPaint() {
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeCap = Paint.Cap.ROUND // 笔触为圆形
        paint.strokeWidth = 10f
    }

}