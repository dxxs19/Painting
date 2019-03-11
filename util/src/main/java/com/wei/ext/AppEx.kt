package com.wei.ext

import android.app.Application
import android.content.Context
import androidx.annotation.CallSuper

abstract class AppEx : Application() {

    @CallSuper
    final override fun onCreate() {
        super.onCreate()

        if (isMainProcess()) {
            onCreateInMainProcess()
        } else {
            onCreateInChildProcess()
        }
    }

    /**
     * 在主进程里初始化
     */
    @CallSuper
    open fun onCreateInMainProcess() {
        appContext = applicationContext

//        appConfig = configureApp()
//        listConfig = configureList()
//
//        NavBar.init(configureNavBar())
    }

    /**
     * 在子进程里初始化
     */
    open fun onCreateInChildProcess() {
    }

    /**
     * 设置App
     */
//    abstract fun configureApp(): AppConfig
//
//    /**
//     * 设置List
//     */
//    open fun configureList(): ListConfig = ListConfig.build { }
//
//    abstract fun configureNavBar(): NavBarConfig
}

//lateinit var appConfig: AppConfig
//lateinit var listConfig: ListConfig
lateinit var appContext: Context