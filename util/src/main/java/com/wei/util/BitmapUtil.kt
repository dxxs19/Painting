package com.wei.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import com.wei.ext.FileSuffix
import com.wei.ext.compress
import com.wei.ext.resize
import java.io.ByteArrayOutputStream
import java.io.File

/**
 *
 * @author XiangWei
 * @since 2018/12/29
 */
object BitmapUtil {

    /**
     *  分辨率 2000
     */
    const val MAX_RATIO = 2000

    /**
     *  大小 2M
     */
    const val MAX_SIZE = 2000

//    /**
//     *  将要进行压缩的图片集合的原路径传进来，得到压缩后的图片集合新路径
//     */
//    fun getResizePaths(photoPaths: List<String>): List<String> {
//        val resizePaths = arrayListOf<String>()
//        if (photoPaths.isNotEmpty()) {
//            photoPaths.forEach {
//                resizePaths.add(getResizePath(it))
//            }
//        }
//        return resizePaths
//    }

    /**
     *  将图片进行压缩保存，返回压缩后的图片路径
     */
    fun getResizePath(photoPath: String, reqWidth: Int = 1080, reqHeight: Int = 1440): String {
        var bm = Res.getBitmap(photoPath)
        bm?.let { it ->
            val realWidth = it.width
            val realHeight = it.height
            if (Math.min(realHeight, realWidth) > MAX_RATIO) {
                bm = getResizeBitmap(photoPath, reqWidth, reqHeight)
                val degree = readPictureDegree(photoPath)
                if (degree > 0) {
                    bm?.let {
                        bm = rotateBitmap(degree, it)
                    }
                }
                val file = File(Caching.bmpCacheDir, System.currentTimeMillis().toString().plus(FileSuffix.JPEG.text))
                bm?.compress(file, getQuality(bm), Bitmap.CompressFormat.JPEG)
//                L.e("resize path : ${file.path}")
                return file.path
            }
        }
        return photoPath
    }

    /**
     *  把图片调正
     */
    fun rotateBitmap(degree: Int, bm: Bitmap): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
    }

    /**
     *  图片是否发生了旋转
     */
    fun readPictureDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: Exception) {
//            L.e("读取图片偏转角度异常：${e.message}")
        }
        return degree
    }

    // 计算并获取压缩率（对超过2M的图片进行压缩）
    private fun getQuality(bitmap: Bitmap?, limit: Int = MAX_SIZE): Int {
        val baos = ByteArrayOutputStream()
        var quality = 100
        bitmap?.let {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)

            while (baos.toByteArray().size / 1024 > limit) {
                //重置baos即清空baos
                baos.reset()
                //这里压缩quality%，把压缩后的数据存放到baos中
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
                quality -= 10
            }
        }
//        L.e("质量压缩率：$quality")
        return quality
    }

    // 图片分辨率太高在有些低端机上无法显示，需要压缩。例如：红米6A
    fun getResizeBitmap(bitmap: Bitmap?, reqWidth: Int = 800, reqHeight: Int = 600): Bitmap? {
        bitmap?.let { bm ->
            val realWidth = bm.width
            val realHeight = bm.height
            if (Math.min(realHeight, realWidth) > MAX_RATIO) {
                return bm.resize(reqWidth, reqHeight)
            }
            return bm
        }
        return null
    }

    fun getResizeBitmap(path: String, reqWidth: Int = 1080, reqHeight: Int = 1440): Bitmap? {
        val opts = BitmapFactory.Options()
        // inJustDecodeBounds设置为true,这样使用该option decode出来的Bitmap是null，只是把长宽存放到option中
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, opts)

        opts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight)
        // 一定要将inJustDecodeBounds设为false，否则Bitmap为null
        opts.inJustDecodeBounds = false

        return BitmapFactory.decodeFile(path, opts)
    }

    // 计算并获取采样率
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

        val outWidth = options.outWidth
        val outHeight = options.outHeight

        var inSampleSize = 1 // 1是不缩放

        // 计算宽高缩放比例
        if (outHeight > reqHeight || outWidth > reqWidth) {
            val heightRatio = Math.round(outHeight.toFloat() / reqHeight)
            val widthRatio = Math.round(outWidth.toFloat() / reqWidth)
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = if (heightRatio < widthRatio) {
                heightRatio
            } else {
                widthRatio
            }
        }
        return inSampleSize
    }

}