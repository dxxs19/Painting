package com.wei.painting.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import com.wei.painting.R

/**
 *
 * @author XiangWei
 * @since 2019/2/16
 */
class PaintView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?= null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private var path = Path()
    private lateinit var bufferBitmap: Bitmap
    private lateinit var bufferCanvas: Canvas
    private var lastX = 0.0f
    private var lastY = 0.0f

    init {
        setBackgroundResource(R.drawable.ic_paint_bg)
        initPaint()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        bufferBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        bufferCanvas = Canvas(bufferBitmap)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(bufferBitmap, 0f, 0f, null)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.x
                    lastY = event.y

                    path.moveTo(lastX, lastY)
                }

                MotionEvent.ACTION_MOVE -> {
                    path.quadTo(lastX, lastY, event.x, event.y)
                }

                MotionEvent.ACTION_UP -> {

                }
            }
        }
        return true
    }

    private fun initPaint() {
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeJoin = Paint.Join.ROUND // 使画笔更加圆润
        paint.strokeCap = Paint.Cap.ROUND // 同上
        paint.strokeWidth = 10f
    }

}