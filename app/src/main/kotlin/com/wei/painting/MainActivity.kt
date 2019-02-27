package com.wei.painting

import android.content.Intent
import android.databinding.ViewDataBinding
import com.wei.libbase.BaseActivity

class MainActivity : BaseActivity() {

    override fun createDataBinding(): ViewDataBinding {
        val dataBinding = com.wei.painting.databinding.ActivityMainBinding.inflate(layoutInflater)
        dataBinding.owner = this
        return dataBinding
    }

    fun paint() {
        startActivity(Intent(context, PaintActivity::class.java))
    }

}
