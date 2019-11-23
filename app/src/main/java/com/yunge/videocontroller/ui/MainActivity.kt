package com.yunge.videocontroller.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.yunge.module_core.base.router.ARouterPath
import com.yunge.videocontroller.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //路由配置跳转
        ARouter.getInstance().build(ARouterPath.Main.PATH_MAIN_TO_PLAY).navigation()

//        main_btn.setOnClickListener {
//            //路由配置跳转
//            ARouter.getInstance().build(ARouterPath.Main.PATH_MAIN_TO_PLAY).navigation()
//        }
    }
}
