package com.wei.painting

import android.content.Intent
import android.util.Log
import androidx.databinding.ViewDataBinding
import com.wei.libbase.BaseActivity
import com.wei.util.BitmapUtil

class MainActivity : BaseActivity() {

    override fun createDataBinding(): ViewDataBinding {
        val dataBinding = com.wei.painting.databinding.ActivityMainBinding.inflate(layoutInflater)
        dataBinding.owner = this
        return dataBinding
    }

    fun paint() {
        startActivity(Intent(context, PaintActivity::class.java))
    }

    override fun initViews() {
        val photoPath = "图片/storage/emulated/0/bluetooth/photo1552266765515.jpg"
        val degree = BitmapUtil.readPictureDegree(photoPath)
        Log.e(TAG, "偏转了 $degree 度")
    }
}
