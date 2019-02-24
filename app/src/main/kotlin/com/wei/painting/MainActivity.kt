package com.wei.painting

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.wei.painting.view.PaintView

class MainActivity : AppCompatActivity() {

    private var paintView: PaintView?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        paintView = findViewById(R.id.view_paint)
    }

    fun setPenModel(view: View) {
        paintView?.setModel(PaintView.EDIT_MODE_PEN)
        paintView?.setPaintWidth(10f)
    }

    fun setEraseModel(view: View) {
        paintView?.setModel(PaintView.EDIT_MODE_ERASER)
        paintView?.setPaintWidth(50f)
    }

    fun clearCanvas(view: View) {
        paintView?.clear()
    }
}
