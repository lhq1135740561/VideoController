package com.yunge.module_play.data.db

import android.content.Context
import android.content.SharedPreferences




/**
 * 播放器数据
 */
object PlayControllerDao {



    //存储音量拖动条的弧度值和按钮的X轴、Y轴坐标
    fun cacheVolumeProgressDegree(context: Context,degree: Float,x : Float){
        if( x <= -2.0f) return
        context.getSharedPreferences("play_volume_progress",Context.MODE_PRIVATE).edit {
            putFloat("degree",degree)
            putFloat("progressX",x)
        }
    }

    fun cacheVolumeProgressDegreeY(context: Context,y: Float){
        if( y <= -2.0f) return
        context.getSharedPreferences("play_volume_progress",Context.MODE_PRIVATE).edit {
            putFloat("progressY",y)
        }
    }

    //获取音量拖动条的弧度值
    fun getVolumeProgressDegree(context: Context) =
        context.getSharedPreferences("play_volume_progress",Context.MODE_PRIVATE).getFloat("degree",-1.0f)


    //获取音量拖动条按钮的X轴坐标
    fun getVolumeProgressDegreeX(context: Context) =
        context.getSharedPreferences("play_volume_progress",Context.MODE_PRIVATE).getFloat("progressX",-1.0f)

    //获取音量拖动条按钮的X轴坐标
    fun getVolumeProgressDegreeY(context: Context) =
        context.getSharedPreferences("play_volume_progress",Context.MODE_PRIVATE).getFloat("progressY",-1.0f)




    //获取屏幕的横屏方式
    fun getScreenWOrientation(context: Context) =
        context.getSharedPreferences("screen",Context.MODE_PRIVATE).getBoolean("orientationW",false)

    //设置屏幕的横屏方式
    fun setScreenWOrientation(context: Context,orientation: Boolean) =
        context.getSharedPreferences("screen",Context.MODE_PRIVATE).getBoolean("orientationW",orientation)

    //获取屏幕的竖屏方式
    fun getScreenHOrientation(context: Context) =
        context.getSharedPreferences("screen",Context.MODE_PRIVATE).getBoolean("orientationH",false)

    //设置屏幕的竖屏方式
    fun setScreenHOrientation(context: Context,orientation: Boolean) =
        context.getSharedPreferences("screen",Context.MODE_PRIVATE).getBoolean("orientationH",orientation)


    private fun SharedPreferences.edit(action: SharedPreferences.Editor.() -> Unit){
        val editor = edit()
        action(editor)
        editor.apply()
    }
}