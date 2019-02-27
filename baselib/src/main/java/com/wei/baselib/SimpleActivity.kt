package com.wei.libbase

import android.content.Context
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 *
 * @author XiangWei
 * @since 2019/1/30
 */
abstract class SimpleActivity : AppCompatActivity() {

    val context: Context
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = createDataBinding()
        setContentView(db.root)
        initView()
    }

    abstract fun createDataBinding(): ViewDataBinding

    abstract fun initView()
}