package com.yunge.module_play.ui.utils

import com.yunge.module_play.ui.view.PlayControllerView

object PlayControllerMethod {


    /**
     * when语句判断扬声器类型（对应四种不同的图片）
     */
    fun isWhenSpeakerBmpStyle(degree: Float): Int{
        var style = 0
        when(degree){
            in 0f..1.5f -> style = PlayControllerView.SPEAKER_MUTE
            in 1.6f..30f -> style = PlayControllerView.SPEAKER_MIN   //在区间 1到30度之间
            in 30.1f..60f -> style = PlayControllerView.SPEAKER_MIDDLE //在区间 30到60度之间
            in 60.1f..90f -> style = PlayControllerView.SPEAKER_MAX  //在区间 60到90度之间
            else -> {
                return -1
            }
        }

        return style
    }
}