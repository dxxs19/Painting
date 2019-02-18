package com.wei.painting.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.wei.painting.R

/**
 *
 * @author XiangWei
 * @since 2019/2/16
 */
class PaintView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet?= null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private var path = Path()
    private lateinit var bufferBitmap: Bitmap
    private lateinit var bufferCanvas: Canvas

    private var lastX = 0.0f
    private var lastY = 0.0f

    init {
        initPaint()
        initBg()
    }

    private fun initPaint() {
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeJoin = Paint.Join.ROUND // 使画笔更加圆润
        paint.strokeCap = Paint.Cap.ROUND // 同上
        paint.strokeWidth = 10f
    }

    private fun initBg() {
//        setBackgroundColor(Color.WHITE)
        setBackgroundResource(R.drawable.ic_paint_bg)
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        bufferBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
//        bufferCanvas = Canvas(bufferBitmap)
//    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        canvas?.drawBitmap(bufferBitmap, 0f, 0f, null)
        canvas?.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> { // 手指按下时触发，每次触摸事件只会触发一次 ACTION_DOWN
                // 将起始点移动到当前坐标
                path.moveTo(event.x, event.y)

                // 记录上次触摸的坐标
                lastX = event.x
                lastY = event.y
            }

            MotionEvent.ACTION_MOVE -> { // 手指移动的时候触发，每次触摸事件可能多次触发 ACTION_MOVE
                //绘制圆滑曲线，即贝塞尔曲线
                path.quadTo(lastX, lastY, event.x, event.y)

                lastX = event.x
                lastY = event.y
            }

            MotionEvent.ACTION_UP -> { // 手指离开屏幕时触发，每次触摸事件只会触发一次 ACTION_UP

            }
        }

        // 重新绘制，会调用 onDraw 方法
        invalidate()
        return true
    }

}