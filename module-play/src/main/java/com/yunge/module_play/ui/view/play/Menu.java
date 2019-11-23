package com.yunge.module_play.ui.view.play;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.yunge.module_play.ui.view.PlayControllerView;


/**
 * 菜单类
 *
 */

public class Menu {

    private Bitmap bmpMenuBg;//菜单背景图片
    private Bitmap bmpLogo;  //菜单Logo图片
    private Bitmap bmpStartButton;//菜单开始按钮图片
    private Bitmap bmpEndButton;  //菜单结束按钮图片
    private Bitmap bmpTextButton; //菜单按钮文字图片

    private RectF rect; //矩形区域
    private int btnx;
    private int btny,btnTextY;
    public boolean isPressed=false; //按钮的标识符

    private int screenW = Math.round(PlayControllerView.getScreenWidth());
    private int screenH = Math.round(PlayControllerView.getScreenHeight());

    public Menu(Bitmap bmpMenuBg, Bitmap bmpLogo, Bitmap bmpStartButton, Bitmap bmpEndButton, Bitmap bmpTextButton) {
        this.bmpMenuBg = bmpMenuBg;
        this.bmpLogo = bmpLogo;
        this.bmpStartButton = bmpStartButton;
        this.bmpEndButton = bmpEndButton;
        this.bmpTextButton = bmpTextButton;

        rect = new RectF(20.0f, screenH/3,screenW-20.0f,
                (screenH/3 + bmpLogo.getHeight()/2));
        btnx = screenW/2 - bmpStartButton.getWidth()/2; //按钮距离left的距离
        btny = screenH * 2/3;  //按钮距离top的距离
        btnTextY =btny + (bmpStartButton.getHeight()-bmpTextButton.getHeight())/2;

    }

    /**
     * 绘制菜单页面
     * @param canvas
     * @param paint
     */
    public void onMyDraw(Canvas canvas, Paint paint){
        //画出菜单背景
        canvas.drawBitmap(bmpMenuBg,0,0,paint);
        //画出菜单Logo
        canvas.drawBitmap(bmpLogo,null,rect,paint);
        //画出开始游戏按钮
        if(!isPressed){
            canvas.drawBitmap(bmpStartButton,btnx,btny,paint); //按下按钮时
        }else {
            canvas.drawBitmap(bmpEndButton,btnx,btny,paint); //没有按下按钮时
        }
        //画出按钮文字
        canvas.drawBitmap(bmpTextButton,screenW/2-bmpTextButton.getWidth()/2,
                btnTextY,paint);
    }


    public boolean onTouchEvent(MotionEvent event) {
        //获取点击屏幕的x轴和y轴的距离
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN: //手指按下屏幕的操作
                if (x > btnx && x <= btnx + bmpStartButton.getWidth()) {
                    if (y >= btny && y <= btny + bmpStartButton.getHeight()) {
                        isPressed = true;
                    } else {
                        isPressed = false;
                    }
                } else {
                    isPressed = false;
                }
            case MotionEvent.ACTION_MOVE: //手指移动屏幕的操作

                break;
            case MotionEvent.ACTION_UP: //手指离开屏幕时的操作
                isPressed = false;
//                if(x >= btnx && x<= btnx+bmpStartButton.getWidth()){
//                    if(y >= btny && y<= btny+bmpStartButton.getHeight()){
//                        isPressed = true;
//                        //从菜单页面切换到游戏页面
////                        AirSurfaceView.gameState = AirSurfaceView.GAME_ING;
//                    }
//                }


                break;
            default:
                break;
        }
        return false;
    }




}
