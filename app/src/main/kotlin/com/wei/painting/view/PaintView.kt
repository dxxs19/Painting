package com.wei.painting.view

import android.content.Context
import android.graphics.*
import android.support.annotation.IntDef
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

    companion object {
        const val EDIT_MODE_PEN = 0x1      //画笔模式
        const val EDIT_MODE_ERASER = 0x2    //橡皮擦模式
    }
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(EDIT_MODE_PEN, EDIT_MODE_ERASER)
    annotation class EditMode

    //当前编辑模式默认为画笔模式
    private var currentEditMode = EDIT_MODE_PEN

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private var path = Path()

    //想要绘制的内容先绘制到这个增加的canvas对应的bitmap上，
    // 写完后再把这个bitmap的ARGB信息一次提交给上下文的canvas去绘制
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
//        setBackgroundResource(R.drawable.ic_paint_bg)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        bufferBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)

        //canvas绘制的内容，将会在这个mBufferBitmap内
        bufferCanvas = Canvas(bufferBitmap)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //画出缓存bitmap的内容
        canvas?.drawBitmap(bufferBitmap, 0f, 0f, null)
//        canvas?.drawPath(path, paint)
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

                //在缓存里面绘制
                bufferCanvas.drawPath(path, paint)

                //重新绘制，会调用onDraw方法
                invalidate()
                lastX = event.x
                lastY = event.y
            }

            MotionEvent.ACTION_UP -> { // 手指离开屏幕时触发，每次触摸事件只会触发一次 ACTION_UP
                //清除路径的内容
                path.reset()
            }
        }

        return true
    }

    /**
     *  设置画笔模式
     */
    fun setModel(@EditMode model: Int) {
        currentEditMode = model
        when (model) {
            EDIT_MODE_PEN -> {
                paint.xfermode = null
            }

            EDIT_MODE_ERASER -> {
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }
        }
    }

    /**
     *  设置画笔宽度
     */
    fun setPaintWidth(width: Float) {
        paint.strokeWidth = width
    }

    /**
     *  清空画布
     */
    fun clear() {
        bufferCanvas.drawColor(0, PorterDuff.Mode.CLEAR)
        invalidate()
    }
}