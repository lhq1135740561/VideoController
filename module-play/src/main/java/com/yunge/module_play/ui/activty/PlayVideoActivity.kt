package com.yunge.module_play.ui.activty

import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.yunge.module_core.base.router.ARouterPath
import com.yunge.module_play.R
import com.yunge.module_play.ui.view.PlayControllerView


@Route(path = ARouterPath.Main.PATH_MAIN_TO_PLAY)
class PlayVideoActivity : AppCompatActivity() {


    private lateinit var playControllerView: PlayControllerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_activity_play_video)


        playControllerView = PlayControllerView(this)

        initPlayView()
    }


    private fun initPlayView() {
        //设置拖动条的条弧度数
//        playControllerView.setProgressDegree(80f)
//        Log.d(PlayControllerView.TAG,playControllerView.getDegree().toString() + "1111")
    }


}
