package com.wei.libbase

import android.content.Context
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window

/**
 *
 * @author XiangWei
 * @since 2019/1/30
 */
abstract class SimpleActivity : AppCompatActivity() {

    val context: Context
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)
        val db = createDataBinding()
        setContentView(db.root)
        initData()
        initNavBar()
        initViews()
    }

    /**
     *  创建 DataBinding
     */
    abstract fun createDataBinding(): ViewDataBinding

    /**
     *  初始化数据
     */
    open fun initData() {}

    /**
     *  初始化导航栏
     */
    open fun initNavBar() {}

    /**
     *  初始化布局控件
     */
    open fun initViews() {}

}