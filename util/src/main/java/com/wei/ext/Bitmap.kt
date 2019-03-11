package com.wei.ext

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.wei.util.DeviceUtil
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


const val MAX_DOUBLE = Int.MAX_VALUE.toDouble()
const val MIN_DOUBLE = 1.0

/**
 * Creates a new [Canvas] to draw on this bitmap and executes the specified
 * [block] on the newly created canvas. Example:
 *
 * ```
 * return Bitmap.createBitmap(…).applyCanvas {
 *    drawLine(…)
 *    translate(…)
 *    drawRect(…)
 * }
 * ```
 */
fun Bitmap.applyCanvas(block: Canvas.() -> Unit): Bitmap {
    val dest = if (isMutable) this else {
        copy(config, true)
    }

    Canvas(dest).apply {
        setAntialias()
        block()

        // Avoids warnings in M+.
        setBitmap(null)
    }
    return dest
}

/**
 * 旋转图片，使图片保持正确的方向。
 *
 * @param degrees    要旋转的角度
 * @param isVertical 垂直显示图片
 * @return 旋转后的图片
 */
fun Bitmap.rotate(@IntRange(from = 0, to = 360) degrees: Int, isVertical: Boolean): Bitmap {
    var needRotate = false
    if (isVertical) {
        if (height < width) {
            needRotate = true
        }
    } else {
        if (height > width) {
            needRotate = true
        }
    }

    return if (needRotate) {
        rotate(degrees)
    } else {
        this
    }
}

/**
 * 旋转图片
 *
 * @param degrees
 * @return 不可变的图片
 */
fun Bitmap.rotate(@IntRange(from = 0, to = 360) degrees: Int): Bitmap {
    if (degrees == 0) {
        return this
    }

    val matrix = Matrix()
    matrix.setRotate(degrees.toFloat())

    /**
     * 不建议使用系统的createScaleBitmap, 不是最清晰的
     */
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

/**
 * 根据宽高缩放, 返回的图片是可变的, 可装载入Canvas
 *
 * @param w   目标宽
 * @param h   目标高
 * @return
 */
fun Bitmap.resize(@FloatRange(from = MIN_DOUBLE, to = MAX_DOUBLE) w: Float,
                  @FloatRange(from = MIN_DOUBLE, to = MAX_DOUBLE) h: Float): Bitmap {
    val scaleW = w / width
    val scaleH = h / height

    val matrix = Matrix()
    matrix.setScale(scaleW, scaleH)

    return copy(w, h, matrix)
}

fun Bitmap.resize(w: Int,
                  h: Int): Bitmap {
    val scaleW = w / width.toFloat()
    val scaleH = h / height.toFloat()

    val matrix = Matrix()
    matrix.setScale(scaleW, scaleH)

    return copy(w, h, matrix)
}

/**
 * 根据比例缩放, 返回的图片是可变的, 可装载入Canvas
 *
 * @param scale
 * @return
 */
fun Bitmap.resize(@FloatRange(from = MIN_DOUBLE, to = MAX_DOUBLE) scale: Float): Bitmap {
    var w = width * scale
    var h = height * scale

    if (w < MIN_DOUBLE) {
        w = MIN_DOUBLE.toFloat()
    }

    if (h < MIN_DOUBLE) {
        h = MIN_DOUBLE.toFloat()
    }

    return copy(w, h, Matrix().apply { setScale(scale, scale) })
}

/**
 * 同时缩放和旋转
 *
 * @param scale  比例
 * @param degree 角度
 * @return 不可变的图片(如果角度为0, scale为1, 那么返回的就是原图)
 */
fun Bitmap.resize(@FloatRange(from = MIN_DOUBLE, to = MAX_DOUBLE) scale: Float,
                  @IntRange(from = 0, to = 360) degree: Int): Bitmap {

    val matrix = Matrix()
    matrix.preScale(scale, scale)
    matrix.postRotate(degree.toFloat())

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}


/**
 * 根据[matrix]复制一张图片
 */
fun Bitmap.copy(w: Int, h: Int, matrix: Matrix, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
    return Bitmap.createBitmap(w, h, config).applyCanvas {
        drawBitmap(this@copy, matrix, null)
    }
}

fun Bitmap.copy(w: Float, h: Float, matrix: Matrix, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
    return copy(w.toInt(), h.toInt(), matrix, config)
}

fun Bitmap.copy(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
    return copy(config, true)
}

fun Bitmap.getAlphaSafeConfig(): Bitmap.Config {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Avoid short circuiting the sdk check.
        if (Bitmap.Config.RGBA_F16 == config) { // NOPMD
            return Bitmap.Config.RGBA_F16
        }
    }

    return Bitmap.Config.ARGB_8888
}

/**
 * 图片无损转换为bytes
 *
 * @param bmp
 * @return
 */
fun Bitmap.toBytes(): ByteArray? {
    return compress(100, Bitmap.CompressFormat.PNG)
}

/**
 * 图片转换成字节数
 *
 * @param quality
 * @param format
 * @return
 */
fun Bitmap.compress(@IntRange(from = 1, to = 100) quality: Int, format: Bitmap.CompressFormat): ByteArray? {
    return ByteArrayOutputStream().safeUse {
        if (compress(format, quality, it)) {
            it.toByteArray()
        } else {
            null
        }
    }
}

/**
 * 转换成圆形图片
 */
fun Bitmap.toCircle(): Bitmap {
    val paint = Paint()
    paint.isAntiAlias = true

    val w = width
    val h = height
    val min = if (w > h) h else w

    return Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888).applyCanvas {
        drawCircle(min / 2f, min / 2f, min / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        drawBitmap(this@toCircle, 0f, 0f, paint)
    }
}

fun Bitmap?.recycle() {
    if (this != null && !isRecycled) {
        recycle()
    }
}

/**
 * 压缩图片到文件
 *
 * @param toFile    文件
 * @param format  图片格式
 * @param quality 图片质量
 * @return 是否成功保存
 */
fun Bitmap.compress(toFile: File, @IntRange(from = 1, to = 100) quality: Int, format: Bitmap.CompressFormat): Boolean {
    return FileOutputStream(toFile).safeUse { compress(format, quality, it) } ?: false
}

/**
 * 正片叠底
 * @param mask
 * @param useAlpha 是否让alpha透明度参与到运算中
 */
fun Bitmap.multiply(mask: Bitmap?, useAlpha: Boolean = false): Bitmap {
    requireNotNull(mask) { "mask can not be null" }

    // 系统的做法, 暂时保留代码
//    val new = Bitmap.createBitmap(width, height, config)
//    new.applyCanvas {
//        val p = Paint()
//        p.isFilterBitmap = true
//
////        val layerID = saveLayer(0f, 0f, width.toFloat(), height.toFloat(), p, Canvas.ALL_SAVE_FLAG)
//
//        drawBitmap(this@multiply, 0f, 0f, p)
//
//        p.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
//
//        drawBitmap(mask, 0f, 0f, p)
////        val m = Matrix()
////        val scaleW = width / mask.width.toFloat()
////        val scaleH = height / mask.height.toFloat()
////        m.setScale(scaleW, scaleH)
////        drawBitmap(mask, m, p)
//
//
////        p.xfermode = null
////        restoreToCount(layerID)
//    }
//    return new
//
    val use: Bitmap = if (isMutable) {
        this
    } else {
        copy(config, true)
    }

    val w = use.width
    val h = use.height
    val maskW = mask.width
    val maskH = mask.height

    val useMask: Bitmap
    useMask = if (w != maskW || h != maskH) {
        val m = mask.resize(w, h)
        mask.recycle()
        m
    } else {
        mask
    }

    val pix = IntArray(w * h)
    val maskPix = IntArray(w * h)
    getPixels(pix, 0, w, 0, 0, w, h)
    useMask.getPixels(maskPix, 0, w, 0, 0, w, h)

    var color: Int
    var maskColor: Int

    for (i in pix.indices) {
        color = pix[i]
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        maskColor = maskPix[i]
        val rMask = Color.red(maskColor)
        val gMask = Color.green(maskColor)
        val bMask = Color.blue(maskColor)

        val a = Color.alpha(color)
        val aMask = Color.alpha(maskColor)

        if (aMask == 0) {
            // 透明的时候使用原图颜色
            pix[i] = Color.argb(a, r, g, b)
        } else {
            val max = 255
            val rResult = r * rMask / max
            val gResult = g * gMask / max
            val bResult = b * bMask / max
            val aResult = if (useAlpha) a * aMask / max else a

            pix[i] = Color.argb(aResult, rResult, gResult, bResult)
        }
    }

    use.setPixels(pix, 0, w, 0, 0, w, h)
    return use
}

/**
 * 根据蒙版叠加图片, 白色保留黑色过滤, 其他颜色自动根据色级调整透明度
 *
 * @param mask 蒙版图
 * @return 叠加后的图片
 */
fun Bitmap.masking(mask: Bitmap?): Bitmap {
    requireNotNull(mask) { "mask can not be null" }

    val w = width
    val h = height
    val maskW = mask.width
    val maskH = mask.height

    val useMask: Bitmap
    useMask = if (w != maskW || h != maskH) {
        val m = mask.resize(w, h)
        mask.recycle()
        m
    } else {
        mask
    }

    val pix = IntArray(w * h)
    val maskPixArray = IntArray(w * h)
    getPixels(pix, 0, w, 0, 0, w, h)
    useMask.getPixels(maskPixArray, 0, w, 0, 0, w, h)

    var color: Int
    var red: Int
    var green: Int
    var blue: Int
    var maskColor: Int
    var maskAlpha: Int

    for (i in pix.indices) {
        color = pix[i]
        red = Color.red(color)
        green = Color.green(color)
        blue = Color.blue(color)

        maskColor = maskPixArray[i]
        maskAlpha = Color.red(maskColor) // 对比黑白, rgb都一样的值, 只需要获取其中一个的来当成比例计算就行了
        pix[i] = Color.argb(maskAlpha, red, green, blue)
    }

    mask.setPixels(pix, 0, w, 0, 0, w, h)
    return mask

}

/**
 * 模糊算法
 *
 * @param radius  模糊半径(1-25)
 * @param context
 * @return 如果是mutable的图片, 返回的是原图
 */
fun Bitmap.blur(@IntRange(from = 1, to = 25) radius: Int = 20, context: Context = appContext): Bitmap {
    return if (DeviceUtil.sdkVersion >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        rsBlur(this, radius, context)
    } else {
        fastBlur(this, radius)
    }
}

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
private fun rsBlur(bitmap: Bitmap, @IntRange(from = 1, to = 25) radius: Int = 20, context: Context): Bitmap {
    var rs: RenderScript? = null
    try {
        rs = RenderScript.create(context)
        rs!!.messageHandler = RenderScript.RSMessageHandler()
        val input = Allocation.createFromBitmap(rs,
                bitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT)
        val output = Allocation.createTyped(rs, input.type)
        val blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

        blur.setInput(input)
        blur.setRadius(radius.toFloat())
        blur.forEach(output)
        output.copyTo(bitmap)
    } finally {
        rs?.destroy()
    }

    return bitmap
}

/**
 * 对图像进行高斯模糊处理.**该算法耗时**
 *
 * @param bmp
 * @param radius 模糊半径，数值越大越模糊
 * @return
 */
private fun fastBlur(bmp: Bitmap, @IntRange(from = 1, to = 25) radius: Int): Bitmap {
    val useBmp: Bitmap = if (bmp.isMutable) {
        bmp
    } else {
        bmp.copy(bmp.config, true)
    }

    if (radius < 1) {
        return useBmp
    }

    val w = useBmp.width
    val h = useBmp.height

    val pix = IntArray(w * h)
    useBmp.getPixels(pix, 0, w, 0, 0, w, h)

    val wm = w - 1
    val hm = h - 1
    val wh = w * h
    val div = radius + radius + 1

    val r = IntArray(wh)
    val g = IntArray(wh)
    val b = IntArray(wh)
    var rsum: Int
    var gsum: Int
    var bsum: Int
    var x: Int
    var y: Int
    var i: Int
    var p: Int
    var yp: Int
    var yi: Int
    var yw: Int
    val vmin = IntArray(Math.max(w, h))

    var divsum = div + 1 shr 1
    divsum *= divsum
    val dv = IntArray(256 * divsum)
    i = 0
    while (i < 256 * divsum) {
        dv[i] = i / divsum
        i++
    }

    yi = 0
    yw = yi

    val stack = Array(div) { IntArray(3) }
    var stackPointer: Int
    var stackStart: Int
    var sir: IntArray
    var rbs: Int
    val r1 = radius + 1
    var routsum: Int
    var goutsum: Int
    var boutsum: Int
    var rinsum: Int
    var ginsum: Int
    var binsum: Int

    y = 0
    while (y < h) {
        bsum = 0
        gsum = bsum
        rsum = gsum
        boutsum = rsum
        goutsum = boutsum
        routsum = goutsum
        binsum = routsum
        ginsum = binsum
        rinsum = ginsum
        i = -radius
        while (i <= radius) {
            p = pix[yi + Math.min(wm, Math.max(i, 0))]
            sir = stack[i + radius]
            sir[0] = p and 0xff0000 shr 16
            sir[1] = p and 0x00ff00 shr 8
            sir[2] = p and 0x0000ff
            rbs = r1 - Math.abs(i)
            rsum += sir[0] * rbs
            gsum += sir[1] * rbs
            bsum += sir[2] * rbs
            if (i > 0) {
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
            } else {
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
            }
            i++
        }
        stackPointer = radius

        x = 0
        while (x < w) {

            r[yi] = dv[rsum]
            g[yi] = dv[gsum]
            b[yi] = dv[bsum]

            rsum -= routsum
            gsum -= goutsum
            bsum -= boutsum

            stackStart = stackPointer - radius + div
            sir = stack[stackStart % div]

            routsum -= sir[0]
            goutsum -= sir[1]
            boutsum -= sir[2]

            if (y == 0) {
                vmin[x] = Math.min(x + radius + 1, wm)
            }
            p = pix[yw + vmin[x]]

            sir[0] = p and 0xff0000 shr 16
            sir[1] = p and 0x00ff00 shr 8
            sir[2] = p and 0x0000ff

            rinsum += sir[0]
            ginsum += sir[1]
            binsum += sir[2]

            rsum += rinsum
            gsum += ginsum
            bsum += binsum

            stackPointer = (stackPointer + 1) % div
            sir = stack[stackPointer % div]

            routsum += sir[0]
            goutsum += sir[1]
            boutsum += sir[2]

            rinsum -= sir[0]
            ginsum -= sir[1]
            binsum -= sir[2]

            yi++
            x++
        }
        yw += w
        y++
    }
    x = 0
    while (x < w) {
        bsum = 0
        gsum = bsum
        rsum = gsum
        boutsum = rsum
        goutsum = boutsum
        routsum = goutsum
        binsum = routsum
        ginsum = binsum
        rinsum = ginsum
        yp = -radius * w
        i = -radius
        while (i <= radius) {
            yi = Math.max(0, yp) + x

            sir = stack[i + radius]

            sir[0] = r[yi]
            sir[1] = g[yi]
            sir[2] = b[yi]

            rbs = r1 - Math.abs(i)

            rsum += r[yi] * rbs
            gsum += g[yi] * rbs
            bsum += b[yi] * rbs

            if (i > 0) {
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
            } else {
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
            }

            if (i < hm) {
                yp += w
            }
            i++
        }
        yi = x
        stackPointer = radius
        y = 0
        while (y < h) {
            // Preserve alpha channel: ( 0xff000000 & pix[yi] )
            pix[yi] = -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

            rsum -= routsum
            gsum -= goutsum
            bsum -= boutsum

            stackStart = stackPointer - radius + div
            sir = stack[stackStart % div]

            routsum -= sir[0]
            goutsum -= sir[1]
            boutsum -= sir[2]

            if (x == 0) {
                vmin[y] = Math.min(y + r1, hm) * w
            }
            p = x + vmin[y]

            sir[0] = r[p]
            sir[1] = g[p]
            sir[2] = b[p]

            rinsum += sir[0]
            ginsum += sir[1]
            binsum += sir[2]

            rsum += rinsum
            gsum += ginsum
            bsum += binsum

            stackPointer = (stackPointer + 1) % div
            sir = stack[stackPointer]

            routsum += sir[0]
            goutsum += sir[1]
            boutsum += sir[2]

            rinsum -= sir[0]
            ginsum -= sir[1]
            binsum -= sir[2]

            yi += w
            y++
        }
        x++
    }

    useBmp.setPixels(pix, 0, w, 0, 0, w, h)

    return useBmp
}

/**
 * 将bitmap保存到文件中
 */
fun Bitmap.saveToFile(
        file: File,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 100
): Boolean {
    val bos = BufferedOutputStream(FileOutputStream(file))
    val result = compress(format, quality, bos)
    bos.use {
        bos.flush()
        bos.close()
    }
    return result
}

/**
 * 将bitmap保存到相册中
 */
fun Bitmap.saveToAlbum(
        name: String,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 100
): Boolean {
    // 获取相册文件路径：xxx/DCIM
    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), name)
    val result = saveToFile(file, format, quality)
    if (result) {
        // 发广播告诉相册有图片需要更新。这样便可以在相册下看到新保存的图片
        val uri = Uri.fromFile(file)
        appContext.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
    }
    return result
}