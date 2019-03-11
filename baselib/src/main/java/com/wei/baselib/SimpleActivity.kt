package com.wei.libbase

import android.content.Context
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding

/**
 *
 * @author XiangWei
 * @since 2019/1/30
 */
abstract class SimpleActivity : AppCompatActivity() {

    protected var TAG = ""

    val context: Context
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        TAG = javaClass.simpleName
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