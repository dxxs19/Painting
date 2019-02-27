package com.wei.painting

import android.databinding.ViewDataBinding
import com.wei.libbase.BaseActivity
import com.wei.painting.view.PaintView

/**
 * 画板
 * @author XiangWei
 * @since 2019/2/26
 */
class PaintActivity : BaseActivity() {

    private var paintView: PaintView?= null

    override fun createDataBinding(): ViewDataBinding {
        val dataBinding = com.wei.painting.databinding.ActivityPaintingBinding.inflate(layoutInflater)
        dataBinding.owner = this
        return dataBinding
    }

    override fun initView() {
        paintView = findViewById(R.id.view_paint)
    }

    fun setPenModel() {
        paintView?.setModel(PaintView.EDIT_MODE_PEN)
        paintView?.setPaintWidth(10f)
    }

    fun setEraseModel() {
        paintView?.setModel(PaintView.EDIT_MODE_ERASER)
        paintView?.setPaintWidth(50f)
    }

    fun clearCanvas() {
        paintView?.clear()
    }
}