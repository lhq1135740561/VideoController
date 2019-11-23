package com.yunge.videocontroller

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter

class MainApplication : Application() {

    private var isDebug = true

    override fun onCreate() {
        super.onCreate()

        context = this

        initRouter()
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context
    }

    private fun initRouter(){
        if(isDebug){
            //一定要在ARouter.init之前调用openDebug
            ARouter.openLog()   //打印日志
            ARouter.openDebug()  //线上版本需要关闭
        }

        ARouter.init(this)
    }
}