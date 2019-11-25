package com.yunge.module_play.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyView extends View {

    private Paint bgpaint;
    private Paint colorpaint;
    private Paint txtpaint;
    private Paint propaint;

    private int cx, cy;
    private int width, height;
    private int whsize;
    //判断是否按下
    private boolean isdown;

    private float angle = 180 / 13;
    private Paint cicpaint;
    private Paint cicwpaint;
    private int currentdit;

    public MyView(Context context)
    {
        super(context);
        init();
    }

    public MyView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        bgpaint = new Paint();
        colorpaint = new Paint();
        txtpaint = new Paint();
        propaint = new Paint();
        cicpaint = new Paint();
        cicwpaint = new Paint();

        bgpaint.setAntiAlias(true);
        bgpaint.setColor(Color.GREEN);
        bgpaint.setStyle(Paint.Style.STROKE);
        bgpaint.setStrokeWidth(35);
        bgpaint.setStrokeCap(Paint.Cap.ROUND);

        colorpaint.setAntiAlias(true);
        colorpaint.setColor(Color.WHITE);
        colorpaint.setStyle(Paint.Style.STROKE);
        colorpaint.setStrokeWidth(33);
        colorpaint.setStrokeCap(Paint.Cap.ROUND);

        propaint.setAntiAlias(true);
        propaint.setColor(Color.GREEN);
        propaint.setStyle(Paint.Style.STROKE);
        propaint.setStrokeWidth(33);
        propaint.setStrokeCap(Paint.Cap.ROUND);

        txtpaint.setAntiAlias(true);
        txtpaint.setColor(Color.BLACK);
        txtpaint.setStyle(Paint.Style.FILL);
        txtpaint.setStrokeWidth(1);
        txtpaint.setTextSize(30);

        cicpaint.setAntiAlias(true);
        cicpaint.setColor(Color.GREEN);
        cicpaint.setStyle(Paint.Style.FILL);
        cicpaint.setStrokeWidth(1);
        cicpaint.setTextSize(20);

        cicwpaint.setAntiAlias(true);
        cicwpaint.setColor(Color.WHITE);
        cicwpaint.setStyle(Paint.Style.FILL);
        cicwpaint.setStrokeWidth(1);
        cicwpaint.setTextSize(20);

        point.x = (int) Dp2Px(getContext(), 45);
        point.y = (int) (whsize - Dp2Px(getContext(), 45));

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawarc(canvas);
        Log.d("renk", "invar");
        if (isdown)
        {
            drawmap(canvas);
        } else
        {
            drawbit(canvas);
        }
    }



    /** 按下弹起后画点位置 **/
    private void drawbit(Canvas canvas)
    {
        RectF oval = new RectF(Dp2Px(getContext(), 45),
                Dp2Px(getContext(), 45), whsize - Dp2Px(getContext(), 45),
                whsize - Dp2Px(getContext(), 45));
        canvas.drawArc(oval, -180, (int) (angle * count), false, propaint);
        currentdit = (int) count + 16;
        canvas.drawCircle(point.x, point.y, 45, cicpaint);
        canvas.drawCircle(point.x, point.y, 43, cicwpaint);
        canvas.drawText(currentdit + "", point.x - 22, point.y + 15, txtpaint);

        canvas.drawText(currentdit + "", cx - Dp2Px(getContext(), 45), cy - 20,
                txtpaint);
    }

    /** 画点 **/
    private void drawmap(Canvas canvas)
    {
        RectF oval = new RectF(Dp2Px(getContext(), 45),
                Dp2Px(getContext(), 45), whsize - Dp2Px(getContext(), 45),
                whsize - Dp2Px(getContext(), 45));
        if (temp < 0)
        {
            canvas.drawArc(oval, -180, 180 + temp, false, propaint);
            int a = (int) (180 + temp);
            // canvas.drawText(100 * a / 180 + "%", cx - Dp2Px(getContext(),
            // 45), cy - 20, txtpaint);
        } else
        {
            canvas.drawArc(oval, -180, temp, false, propaint);
            // canvas.drawText(100 * (int) temp / 180 + "%", cx -
            // Dp2Px(getContext(), 45), cy - Dp2Px(getContext(), 45),
            // txtpaint);
        }
        canvas.drawCircle(point.x, point.y, 45, cicpaint);
        canvas.drawCircle(point.x, point.y, 43, cicwpaint);
        canvas.drawText(currentdit + "", point.x - 22, point.y + 15, txtpaint);

    }

    private void drawarc(Canvas canvas)
    {
        RectF oval = new RectF(Dp2Px(getContext(), 45),
                Dp2Px(getContext(), 45), whsize - Dp2Px(getContext(), 45),
                whsize - Dp2Px(getContext(), 45));
        canvas.drawArc(oval, -180, 180, false, bgpaint);
        canvas.drawArc(oval, -180, 180, false, colorpaint);
        // 中心圆点
        // canvas.drawCircle(cx, (int) ry + 32, 10, txtpaint);

        // 画刻度
        // canvas.save();
        drawsize(canvas);
        // canvas.restore();
    }

    /** 旋转画刻度 **/
    private void drawsize(Canvas canvas)
    {

    }

    public void getsize()
    {
        // startx = whsize / 2 - Math.cos(15 * Math.PI / 180) +
        // Dp2Px(getContext(), 45);
        // starty = whsize / 2 + Math.sin(15 * Math.PI / 180) +
        // Dp2Px(getContext(), 45);
        // ry = (cx - 40) / Math.cos(15 * Math.PI / 180);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        width = w;
        height = h;
        whsize = w > h ? h : w;
        cx = width / 2;
        cy = height / 2;
        //getsize();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    float x;
    float y;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                if (isNearBy(x, y))
                {
                    isdown = true;
                    move(getx(x, y), gety(x, y));
                } else
                {
                    isdown = false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                if (isdown)
                {
                    move(getx(x, y), gety(x, y));
                }
                break;
            case MotionEvent.ACTION_UP:
                x = event.getX();
                y = event.getY();
                if (isdown)
                {
                    isdown = false;
                    getposition(getx(x, y), gety(x, y));
                }
                Log.d("renk", "x:" + x + " y:" + y + " getx:" + getx(x, y)
                        + " gety:" + gety(x, y) + " " + isNearBy(x, y));
                break;
        }
        return true;
    }

    /** 在第几个刻度上 */
    int count = 0;

    /** 判断是否在刻度上 **/
    private void getposition(float getx, float gety)
    {
        /** 小圆在轨道上的 弧度 */
        float mangle = 0;

        point.x = (int) getx;
        point.y = (int) gety;
        if (temp > 0)
        {
            mangle = temp;
        } else
        {
            mangle = 180 + temp;
        }
        if (mangle % angle > 5)
        {
            count = (int) (mangle / angle + 1);
        } else
        {
            count = (int) (mangle / angle);
        }
        invalidate();
    }

    Point point = new Point();

    /** 移动，进度 */
    private void move(float x, float y)
    {
        point.x = (int) x;
        point.y = (int) y;
        Log.d("renk", "point:" + point.toString());
        if (x >= cx * 2 - Dp2Px(getContext(), 45)
                && y >= whsize - Dp2Px(getContext(), 45))
        {
            return;
        }
        invalidate();
    }

    /** 判断是否在圆环边上>20 */
    private boolean isNearBy(Float x, Float y)
    {
        boolean is = Math.abs(whsize / 2 - Dp2Px(getContext(), 45)
                - Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy))) < 20;
        return is;
    }

    /** 保存点的角度 **/
    private float temp = 0;

    /** 获取角度 */
    private float getxy(float x, float y)
    {
        float ao = (float) (Math.atan((cy - y) / (cx - x)) / Math.PI * 180);
        Log.d("renk", "ao" + ao);
        return ao;
    }

    private float getx(float x, float y)
    {
        Log.d("renk", "xxx:" + whsize / 2 * Math.cos(getxy(x, y) * Math.PI / 180));
        temp = getxy(x, y);
        if (getxy(x, y) < 0)
        {
            return (float) (cx + (whsize / 2 - Dp2Px(getContext(), 45))
                    * Math.cos(getxy(x, y) * Math.PI / 180));
        }
        return (float) (cx - (whsize / 2 - Dp2Px(getContext(), 45))
                * Math.cos(getxy(x, y) * Math.PI / 180));
    }

    private float gety(float x, float y)
    {
        Log.d("renk",
                "yyy:" + (whsize / 2) * Math.sin(getxy(x, y) * Math.PI / 180));

        if (getxy(x, y) < 0)
        {
            return (float) ((whsize / 2 - Dp2Px(getContext(), 45))
                    * Math.sin(getxy(x, y) * Math.PI / 180) + cy);
        }
        return (float) (cy - (whsize / 2 - Dp2Px(getContext(), 45))
                * Math.sin(getxy(x, y) * Math.PI / 180));

    }

    public float Dp2Px(Context context, float dp)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dp * scale + 0.5f);
    }

    public float Px2Dp(Context context, float px)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (px / scale + 0.5f);
    }

}
