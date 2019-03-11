@file:Suppress("unused")

package com.wei.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.NinePatch
import android.graphics.drawable.Drawable
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.wei.ext.appContext
import com.wei.ext.string

enum class ResType(val text: String) {
    ID("id"),
    STRING("string"),
    DRAWABLE("drawable"),
    MIPMAP("mipmap"),
    LAYOUT("layout")
}

/**
 * @author yuansui
 * @since 2018/2/7
 */
object Res {

    @AnyRes
    const val ID_NULL = 0

    private val context: Context
        get() = appContext

    fun getIdentifier(name: String, type: ResType) = context.resources.getIdentifier(name, type.text, context.packageName)

    fun getString(@StringRes id: Int): String = context.getString(id)

    fun getStringArray(@ArrayRes id: Int): Array<String>? = context.resources.getStringArray(id)

    fun getBitmap(@DrawableRes id: Int, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = config
        return BitmapFactory.decodeResource(context.resources, id, options)
    }

    fun getBitmap(path: String, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = config
        return BitmapFactory.decodeFile(path, options)
    }

    fun getBitmap(@DrawableRes id: Int, options: BitmapFactory.Options): Bitmap? = BitmapFactory.decodeResource(context.resources, id, options)

    fun getBitmap(path: String, options: BitmapFactory.Options): Bitmap = BitmapFactory.decodeFile(path, options)

    fun getDrawable(@DrawableRes id: Int): Drawable? = ContextCompat.getDrawable(context, id)

    /**
     * 不载入内存, 只读取图片的信息
     */
    fun getBitmapInfo(@DrawableRes id: Int): BitmapFactory.Options {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(context.resources, id, options)
        return options
    }

    /**
     * 不载入内存, 只读取图片的信息
     */
    fun getBitmapInfo(filePath: String): BitmapFactory.Options {
        val opt = BitmapFactory.Options()
        opt.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, opt)
        return opt
    }

    fun getColor(@ColorRes id: Int): Int = ContextCompat.getColor(context, id)

    fun getColorStateList(@ColorRes id: Int): ColorStateList? = ContextCompat.getColorStateList(context, id)

    fun getDimen(@DimenRes id: Int) = context.resources.getDimension(id)

    fun getNinePatch(@DrawableRes id: Int): NinePatch? {
        val bmp = getBitmap(id)
        return when (bmp) {
            null -> null
            else -> NinePatch(bmp, bmp.ninePatchChunk, null)
        }
    }

    fun getRaw(@RawRes id: Int): String = context.resources.openRawResource(id).string()
}