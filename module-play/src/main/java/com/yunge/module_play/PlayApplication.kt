package com.yunge.module_play

import android.annotation.SuppressLint
import android.content.Context
import com.yunge.module_core.BaseApplication

class PlayApplication : BaseApplication(){

    override fun onCreate() {
        super.onCreate()

        context = this
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context
    }
}