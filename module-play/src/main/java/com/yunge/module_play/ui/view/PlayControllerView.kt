package com.yunge.module_play.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.yunge.module_play.R
import com.yunge.module_play.data.db.PlayControllerDao
import com.yunge.module_play.ui.view.play.Menu
import java.lang.Math.toDegrees
import kotlin.math.*

class PlayControllerView : View {

    /**
     * 单独自定义子View测试实例对象Menu
     */
    private lateinit var menu: Menu

//    private Bitmap bmpMenuBg;//菜单背景图片
//    private Bitmap bmpLogo;  //菜单Logo图片
//    private Bitmap bmpStartButton;//菜单开始按钮图片
//    private Bitmap bmpEndButton;  //菜单结束按钮图片
//    private Bitmap bmpTextButton; //菜单按钮文字图片

    private lateinit var bmpMenuBg: Bitmap
    private lateinit var bmpLogo: Bitmap
    private lateinit var bmpStartButton: Bitmap
    private lateinit var bmpEndButton: Bitmap
    private lateinit var bmpTextButton: Bitmap

    companion object {

        const val TAG = "PlayControllerView"

        /**
         * 扬声器的四种状态
         */
         const val SPEAKER_MUTE = 0
         const val SPEAKER_MIN = 1
         const val SPEAKER_MIDDLE = 2
         const val SPEAKER_MAX = 3

        //获取屏幕宽高
        @JvmStatic
        var screenWidth = 0f
        @JvmStatic
        var screenHeight = 0f

        var screenProportion = 0f  //当前屏幕的比例
    }

    private val res = resources  //获取资源包


    //所有图片资源的画笔
    private val bmpPaint = generatePoint(Color.WHITE, Paint.Style.FILL_AND_STROKE, 255, 0f)

    /**
     * 扬声器区域
     */
    private var speakerPaint = generatePoint(Color.GREEN, Paint.Style.FILL_AND_STROKE, 40, 0f) //设置画笔

    /* 四种不同音量对应的图片 */
    private var speakerBmpStyle: Int

    private val speakerBmpMute = BitmapFactory.decodeResource(resources, R.drawable.ic_vol_mute)
    private val speakerBmpMin = BitmapFactory.decodeResource(resources, R.drawable.ic_vol_min)
    private val speakerBmpMiddle = BitmapFactory.decodeResource(resources, R.drawable.ic_vol_middle)
    private val speakerBmpMax = BitmapFactory.decodeResource(resources, R.drawable.ic_vol_max)


    //获取speakeBmp图片位置
    private var speakerBmpX = 0f
    private var speakerBmpY = 0f
    //矩形区域
    private lateinit var speakerRectF: RectF
    private val speakerRadius = dp2Px(context, 80f)    //矩形的一半，即圆形半径
    private var speakerX = 0f  //手指可触摸的x位置
    private var speakerY = 0f  //手指可触摸的y位置
    private var isSpeakerPressed = false  //区域是否被按下

    /**
     * 音量条
     */
    private val volumeToCenterMaxR = dp2Px(context,120f)  //原点到音量条圆环的最大半径值
    //渐变色
    private val volumeColors = intArrayOf(Color.parseColor("#F9D423"), Color.parseColor("#FF4E50"))

    private val colors = intArrayOf(Color.parseColor("#FFF68F"),Color.parseColor("#FFE700")
        ,Color.parseColor("#FFD700"),Color.parseColor("#FFC700")
        ,Color.parseColor("#FFB700"),Color.parseColor("#FFA700")
        ,Color.parseColor("#FF9700"),Color.parseColor("#FF7F00"))

    private  var volumeLinearGradient : LinearGradient

    //画笔
    private val volumeStoke = dp2Px(context, 30f)  //矩形1区域外圆环的宽度
    private var volumePaint : Paint
    private var volumeDragPaint: Paint   //渐变色画笔

    //矩形1区域宽高
    private lateinit var volumeRectF: RectF //矩形区域
    private val volumeToSpeaker: Float   //与Speaker之间的距离（备注：上面的描边宽度多少，这边设置的距离要是它的一半）
    private var volumeX: Float
    private var volumeY: Float

    //图片条
    private val volumeBmp = BitmapFactory.decodeResource(resources, R.drawable.ic_vol_indi_bar_right)

    //音量图片拖动条
    private val volumeDragBmpNormal = BitmapFactory.decodeResource(resources, R.drawable.btn_thumb_normal)
    private val volumeDragBmpPressed = BitmapFactory.decodeResource(resources, R.drawable.btn_thumb_pressed)
    private var volumeDragBmpX: Float  //拖动条图片位置的X,Y轴坐标
    private var volumeDragBmpY: Float

    private var isVolumePressed = false //判断是否被按下

    //横竖屏切换时(本地存储的x,y轴位置会改变)
    private val mConfiguration: Configuration = res.configuration  //获取设置的配置信息
    private val orientation = mConfiguration.orientation  //获取屏幕的方向


    //矩形区域2
    private val volume2Paint: Paint
    private lateinit var volumeRectF2: RectF
    private val volume2Tovolume: Float //距离volumeRectF的距离
    private var volumeX2: Float //宽高
    private var volumeY2: Float

    private var volumeRectFProgress: Float //音量图片拖动条默认圆弧角度
    //本地数据持久化
    private val degree: Float   //获取本地存储的拖动条圆弧角度
    //拖动条的默认X,Y轴(即中心位置)
    private val volumeDragBmpDegreeX: Float  //获取本地存储的x,y值
    private val volumeDragBmpDegreeY: Float


    /**
     * 播放控件
     */
    private val playToCenterMaxR = dp2Px(context,180f)  //原点到播放圆环的最大半径值
    private val playTovolumeMaxR: Float  //音量条圆环到播放圆环的最大半径值

    //正常区域
    private lateinit var playRectF: RectF   //播放的矩形区域
    private val playPaint: Paint  //画笔
    private var playViewX: Float  //播放控件区域到原点的x,y轴坐标
    private var playViewY: Float

    private val playToCenter: Float  //播放控件图形到原点之间的距离

    private val playToVolume = dp2Px(context,35f)  //播放控件图形到Volume(音量图形)之间的距离

    //覆盖区域
    private lateinit var playCoverRectF: RectF  //覆盖播放的区域的背景区域(用于手指点击触摸到后覆盖)
    private val playCoverPaint: Paint  //画笔


    /* 后退键 */
    private val btnBackNormalBmp = BitmapFactory.decodeResource(resources,R.drawable.btn_play_back_normal)
    private var btnBackBmpX: Float = 0f  //后退键图片的按钮x,y轴的坐标
    private var btnBackBmpY: Float = 0f

    private var isBackPressed = false  //判断是否在后退键区域被按下

    /* 播放暂停键 */
    private val btnPauseNormalBmp = BitmapFactory.decodeResource(resources,R.drawable.btn_pause_normal)
    private val btnPlayNormalBmp = BitmapFactory.decodeResource(resources,R.drawable.btn_play_normal)

    private var btnPauseBmpX: Float = 0f //暂停键图片按钮的x,y轴的坐标
    private var btnPauseBmpY: Float = 0f

    private var isPausePressed = false  //判断是否在暂停播放键区域被按下
    private var isPauseOrPlay = false //判断是否被按下，用于判断更换暂停播放图片

    /* 快进键 */
    private val btnForwardNormalBmp = BitmapFactory.decodeResource(resources,R.drawable.btn_play_forward_normal)
    private var btnForwardBmpX: Float = 0f //快进键图片按钮的x,y轴的坐标
    private var btnForwardBmpY: Float = 0f

    private var isForwardPressed = false  //判断是否在快进键区域被按下

    /**
     * 播放进度条
     */

    private val progressToCenterMaxR = dp2Px(context,235f)  //原点到进度条圆环的最大半径值 (180 + 55)dp
    private val progressToPlayMaxR = dp2Px(context,30f)   //播放控件圆弧到进度条圆环的最大半径值

    private val progressToPlay = dp2Px(context,55f)  //播放控件图形到进度条圆环之间的距离

    private val progressInsideToCenterMaxR = dp2Px(context,180f) + progressToPlay * 0.75f  //内进度条到原点的最大半径 (180 + 55*3/4)dp

    private val progressToCenter: Float

    private var progressX: Float   //播放进度条的x,y轴的坐标
    private var progressY: Float

    /* 背景阴影部分 */
    private lateinit var progressRectF: RectF  //播放进度条矩形区域
    private val progressShadowPaint: Paint  //阴影部分画笔

    /* 进度条背景部分 */
    private lateinit var progressInsideRectF: RectF //进度条内部背景矩形区域
    private val progressInsidePaint: Paint    //内部背景画笔

    private val progressInsideMaxR = progressToPlayMaxR / 2  //内部进度条圆弧半径大小

    private val progressInsideToCenter: Float  //内部进度条圆弧的x到原点的距离

    private var progressInsideX: Float   //内部进度条圆弧的x,y轴的坐标
    private var progressInsideY: Float

    /* 进度条覆盖背景部分 */
    private var progressCoverPaint: Paint   //渐变色画笔

    /* 进度条的拖动条按钮 */
    private var progressDragAngle: Float   //拖动条的角度

    private val progressDragBmpNormal = BitmapFactory.decodeResource(res,R.drawable.btn_thumb_normal)
    private val progressDragBmpPressed = BitmapFactory.decodeResource(res,R.drawable.btn_thumb_pressed)

    private var progressDrawBmpX: Float   //拖动条图片x,y轴的坐标值
    private var progressDrawBmpY: Float

    private var isProgressDrawPressed = false  //判断拖动条按钮是否被按下了


    //初始化
    init {


        /**
         * 自定义子View测试初始化
//         */
        bmpMenuBg = BitmapFactory.decodeResource(res, R.drawable.mainmenu)
        bmpLogo = BitmapFactory.decodeResource(res, R.drawable.logo)
        bmpStartButton = BitmapFactory.decodeResource(res, R.drawable.menustart)
        bmpEndButton = BitmapFactory.decodeResource(res, R.drawable.menustartpress)
        bmpTextButton = BitmapFactory.decodeResource(res, R.drawable.starttext)


        /**
         * 扬声器
         */

        //扬声器图片类型
        speakerBmpStyle = 2

        /**
         * 音量条
         */
        //渐变色
        volumeLinearGradient = LinearGradient(0f, 0f, 0f, height.toFloat(), colors, null, Shader.TileMode.MIRROR)

        //画笔

        volumePaint = generatePoint(Color.BLUE, Paint.Style.STROKE, 100, volumeStoke) // 区域1圆环画笔
        volumeDragPaint = generatePoint(Paint.Style.STROKE, 120, volumeStoke,volumeLinearGradient)  //渐变色边圆环画笔

        //矩形1区域宽高
        volumeToSpeaker = speakerRadius + volumeStoke/2
        volumeX = 0f
        volumeY = 0f

        //音量图片拖动条
        volumeDragBmpX = 0f   //拖动条图片位置的X,Y轴坐标
        volumeDragBmpY = 0f


        volumeRectFProgress = 45f  //音量图片拖动条默认圆弧角度为45度

        //设置默认的音量条圆弧度值
        degree = PlayControllerDao.getVolumeProgressDegree(context)  //获取本地存储的圆弧度值
        if (degree != (-1.0f)){
            volumeRectFProgress = degree
            when(volumeRectFProgress){  //根据圆弧角度数初始化扬声器图片类型
                in 0f..1.5f -> speakerBmpStyle = SPEAKER_MUTE
                in 1.6f..30f -> speakerBmpStyle = SPEAKER_MIN   //在区间 1到30度之间
                in 30.1f..60f -> speakerBmpStyle = SPEAKER_MIDDLE //在区间 30到60度之间
                in 60.1f..90f -> speakerBmpStyle = SPEAKER_MAX  //在区间 60到90度之间
                else -> {

                }
            }
        }

        volumeDragBmpDegreeX = PlayControllerDao.getVolumeProgressDegreeX(context)  //获取本地存储的拖动条图片x,y轴的坐标
        volumeDragBmpDegreeY = PlayControllerDao.getVolumeProgressDegreeY(context)

        //矩形区域2
        volume2Paint = generatePoint(Color.YELLOW, Paint.Style.STROKE, 100, dp2Px(context, 10f)) //区域2圆环画笔
        volume2Tovolume = volumeToSpeaker + dp2Px(context, 20f)
        volumeX2 = 0f
        volumeY2 = 0f


        /**
         * 播放控件
         */
        playTovolumeMaxR = dp2Px(context,60f)  //播放圆环到音量条的 R(半径)

        playCoverPaint = generatePoint(Color.RED,Paint.Style.STROKE,120,playTovolumeMaxR)  //覆盖矩形画笔

        playPaint = generatePoint(Color.GREEN,Paint.Style.STROKE,80,playTovolumeMaxR)  //播放矩形画笔
        playViewX = 0f
        playViewY = 0f

        playToCenter = volume2Tovolume + playToVolume  //播放控件图形到原点之间的距离


        /**
         * 播放进度条
         */
        progressX = 0f
        progressY = 0f
        progressToCenter = playToCenter + progressToPlay  //变量半径距离 progressToPlay 50dp
        /* 最大的阴影背景 */
        progressShadowPaint = generatePoint(Color.RED,Paint.Style.STROKE,100,progressToPlayMaxR)  //progressToPlayMaxR圆弧的宽度 30dp

        /* 进度条内部矩形区域 */
        progressInsideX = 0f
        progressInsideY = 0f
        progressInsideToCenter = playToCenter + progressToPlay
        progressInsidePaint = generatePoint(Color.GRAY,Paint.Style.STROKE,100,progressInsideMaxR,Paint.Cap.ROUND) //画笔 15dp(圆弧的宽度)

        /* 进度条覆盖背景部分 */
        progressCoverPaint = generatePoint(Color.BLUE,Paint.Style.STROKE,120,progressInsideMaxR,Paint.Cap.ROUND)  //画笔  15dp(圆弧的宽度)


        /* 进度条的拖动条按钮 */
        progressDragAngle = 40f   //默认45度角度

        progressDrawBmpX = 0f  //拖动条图片的x,y坐标
        progressDrawBmpY = 0f


        Log.d(TAG, "-----init")
    }


    constructor(context: Context?) : super(context) {
        Log.d(TAG, "-----context构造方法")


    }
    constructor(context: Context?, attrs: AttributeSet? = null) : super(context, attrs) {
        Log.d(TAG, "-----context构造方法")
    }
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {

        Log.d(TAG, "-----构造方法")

        Log.d(TAG,"$volumeRectFProgress")

    }





    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        Log.d(TAG,"----onLayout")
    }


    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        Log.d(TAG,"$volumeRectFProgress")

        //获取全屏宽高
        screenWidth = (measuredWidth - paddingLeft - paddingRight).toFloat()
        screenHeight = (measuredHeight - paddingTop - paddingBottom).toFloat()

        screenProportion = screenWidth / screenHeight  //获取宽度/高度比例 w/h

        /**
         * 扬声器
         */
        //获取speakerBmp位置
        speakerBmpX = screenWidth - speakerRadius / 2
        speakerBmpY = screenHeight - speakerRadius / 2
        //获取手指触摸范围
        speakerX = screenWidth - speakerRadius
        speakerY = screenHeight - speakerRadius

        /**
         * 音量
         */
        //获取矩形的位置大小
        volumeX = screenWidth - volumeToSpeaker  //x轴到原点距离
        volumeY = screenHeight - volumeToSpeaker //y轴到原点距离

        volumeX2 = screenWidth - volume2Tovolume //volume2矩形到原点的距离
        volumeY2 = screenHeight - volume2Tovolume


        //设置音量拖动图片按钮的默认x,y轴坐标
        if(volumeDragBmpDegreeX != -1.0f && volumeDragBmpDegreeY != -0.1f){
            if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
                volumeDragBmpX = volumeDragBmpDegreeX
                volumeDragBmpY = volumeDragBmpDegreeY
            }else{
                //设置默认音量拖动条图片的位置(默认45度角的位置，即一半x,y轴的位置上)
                val x = getAngleX(45f,volumeToCenterMaxR)  //volumeToCenterMaxR为音量条的最大半径
                val y = getAngleY(45f,volumeToCenterMaxR)
                volumeDragBmpX = abs(screenWidth - x)
                volumeDragBmpY = abs(screenHeight - y)
            }
            Log.d(TAG,"调用本地x,y轴值")
        }else{
            //设置默认音量拖动条图片的位置(默认45度角的位置，即一半x,y轴的位置上)
            val x = getAngleX(45f,volumeToCenterMaxR)  //volumeToCenterMaxR为音量条的最大半径
            val y = getAngleY(45f,volumeToCenterMaxR)
            volumeDragBmpX = abs(screenWidth - x)
            volumeDragBmpY = abs(screenHeight - y)
            Log.d(TAG,"调用默认x,y轴值---$volumeDragBmpX----$volumeDragBmpY")
        }



        /**
         * 播放控件
         */
        playViewX = screenWidth - playToCenter  //获取播放图形x,y轴相对于原点的位置
        playViewY = screenHeight - playToCenter

        //设置后退键扇形区域的按钮图片x,y轴的坐标 (调用三角形函数公式求x,y的坐标)  (两个点获取x,y)   会有一点误差在
        val backX = playToCenterMaxR - getAngleX(30f,volumeToCenterMaxR)  //165
        btnBackBmpX = (screenWidth - playToCenterMaxR) + backX/2 - btnBackNormalBmp.width/2 - 10f //1696  //计算出的后退键键按钮的X轴位置值
        btnBackBmpY = screenHeight - (getAngleY(30f,playToCenterMaxR) + btnBackNormalBmp.height) / 2 + 10f //计算出的后退键按钮的Y轴位置值
//        Log.d(TAG,"$playToCenterMaxR -----${getAngleX(30f,volumeToCenterMaxR)}---$x")
//        Log.d(TAG,"$btnBackBmpX -----$screenWidth")

        //设置暂停键扇形的按钮图片x,y轴都得坐标 (四个点获取x,y)
        val pauseY = (getAngleY(60f,playToCenterMaxR) - getAngleY(30f,volumeToCenterMaxR)) / 2 //获取一半的高度
        btnPauseBmpY = screenHeight - getAngleY(60f,playToCenterMaxR) + pauseY - btnPauseNormalBmp.height / 2  //计算出的暂停键按钮的Y轴位置值

        val pauseX = (getAngleX(30f,playToCenterMaxR) - getAngleY(60f,volumeToCenterMaxR))
        btnPauseBmpX = screenWidth - getAngleX(30f,playToCenterMaxR) - btnPauseNormalBmp.width/2 + pauseX  //计算出的暂停键按钮的X轴位置值

//        Log.d(TAG,"${getAngleX(30f,playToCenterMaxR)}----${getAngleY(60f,volumeToCenterMaxR)}---$pauseX")
//        Log.d(TAG,"${btnPauseNormalBmp.width / 2}")
        //390.14444----260.24063

        //设置快捷键扇形的按钮图片x,y轴的坐标 (两个点获取x,y)   x轴会有一点误差在
        btnForwardBmpX = screenWidth - getAngleX(60f,playToCenterMaxR)/2 - btnForwardNormalBmp.width/2 + 20f  //计算出的快进键按钮的X轴位置值

        val forwardY = (playToCenterMaxR - getAngleY(60f,volumeToCenterMaxR)) / 2
        btnForwardBmpY = (screenHeight - playToCenterMaxR) + forwardY - btnForwardNormalBmp.height / 2  ////计算出的快进键按钮的Y轴位置值

        Log.d(TAG, "----onMeasure")

        //实例化对象
        menu = Menu(bmpMenuBg,bmpLogo,bmpStartButton,bmpEndButton,bmpTextButton)

        /**
         * 播放进度条
         */

        //阴影
        progressX = screenWidth - progressToCenter
        progressY = screenHeight - progressToCenter

        //内部进度条
        progressInsideX = screenWidth - progressInsideToCenter
        progressInsideY = screenHeight - progressInsideToCenter

        //内部拖动条图片按钮默认x,y轴的坐标
        val x = getAngleX(45f,progressInsideToCenterMaxR)
        val y = getAngleY(45f,progressInsideToCenterMaxR)
        progressDrawBmpX = abs(screenWidth - x)
        progressDrawBmpY = abs(screenHeight - y)

    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //获取画布的宽高

        /**
         * 子View测试
         */
        menu.onMyDraw(canvas,bmpPaint)


        /**
         * 扬声器
         */
        //手指触摸的更改画笔背景
        speakerPaint = if(isSpeakerPressed) {
            generatePoint(Color.RED, Paint.Style.FILL_AND_STROKE, 40, 0f)
        } else {
            generatePoint(Color.GREEN, Paint.Style.FILL_AND_STROKE, 40, 0f) //设置画笔
        }

        speakerRectF = RectF(speakerX, speakerY, screenWidth + speakerRadius, screenHeight + speakerRadius) //矩形位置大小
        canvas?.drawArc(speakerRectF, 180f, 90f, true, speakerPaint) //画一分之四圆

        //绘制不同的扬声器图片
        when(speakerBmpStyle){
            SPEAKER_MUTE -> drawSpeakerBitmap(canvas,speakerBmpMute)
            SPEAKER_MIN -> drawSpeakerBitmap(canvas,speakerBmpMin)
            SPEAKER_MIDDLE -> drawSpeakerBitmap(canvas,speakerBmpMiddle)
            SPEAKER_MAX -> drawSpeakerBitmap(canvas,speakerBmpMax)
        }

        /**
         *  音量
         */
        //矩形区域1
        volumeRectF = RectF(volumeX, volumeY, screenWidth + volumeToSpeaker, screenHeight + volumeToSpeaker)
        canvas?.drawArc(volumeRectF, 180f, 90f, false, volumePaint)  //画边圆环
        canvas?.drawArc(volumeRectF, 180f, volumeRectFProgress, false, volumeDragPaint)  //画渐变色边圆环
        canvas?.drawBitmap(volumeBmp, volumeX - volumeStoke/2 , volumeY - volumeStoke/2, bmpPaint)  //画音量图片
        //矩形区域2
        volumeRectF2 = RectF(volumeX2, volumeY2, screenWidth + volume2Tovolume, screenHeight + volume2Tovolume)
        canvas?.drawArc(volumeRectF2, 180f, 90f, false, volume2Paint)  //画边圆环
        //音量拖动图
        if (!isVolumePressed){
            canvas?.drawBitmap(volumeDragBmpNormal, volumeDragBmpX, volumeDragBmpY, bmpPaint)
        }else{
            canvas?.drawBitmap(volumeDragBmpPressed, volumeDragBmpX, volumeDragBmpY, bmpPaint)
        }


        /**
         * 播放控件
         */

        /* 画背景 */
        playRectF = RectF(playViewX,playViewY,screenWidth + playToCenter,screenHeight + playToCenter) //正常矩形区域
        canvas?.drawArc(playRectF, 180f, 90f, false, playPaint)

        /* 按钮图片 */
        //画后退键的按钮图片
        canvas?.drawBitmap(btnBackNormalBmp,btnBackBmpX,btnBackBmpY,bmpPaint)
        //画暂停键的按钮图片
        if(isPauseOrPlay){
            canvas?.drawBitmap(btnPlayNormalBmp,btnPauseBmpX,btnPauseBmpY,bmpPaint)  //切换播放按钮图片
        }else{
            canvas?.drawBitmap(btnPauseNormalBmp,btnPauseBmpX,btnPauseBmpY,bmpPaint)  //切换播放按钮图片
        }
        //画快进键的按钮图片
        canvas?.drawBitmap(btnForwardNormalBmp,btnForwardBmpX,btnForwardBmpY,bmpPaint)

        /* 画按钮覆盖扇形 */
        playCoverRectF = RectF(playViewX,playViewY,screenWidth + playToCenter,screenHeight + playToCenter)  //覆盖矩形
        if(isBackPressed){  //后退键扇形区域是否被按下
            canvas?.drawArc(playCoverRectF, 180f, 30f, false, playCoverPaint)
        }

        if (isPausePressed){  //暂停键扇形区域是否被按下
            canvas?.drawArc(playCoverRectF, 210f, 30f, false, playCoverPaint)

        }

        if (isForwardPressed){  //快进键扇形区域是否被按下
            canvas?.drawArc(playCoverRectF, 240f, 30f, false, playCoverPaint)
        }


        /**
         * 播放进度条
         */

        /* 阴影背景部分 */
        progressRectF = RectF(progressX,progressY, screenWidth + progressToCenter, screenHeight + progressToCenter)
        canvas?.drawArc(progressRectF,180f,90f,false,progressShadowPaint)

        /* 内部进度条部分 */
        progressInsideRectF = RectF(progressInsideX,progressInsideY, screenWidth + progressInsideToCenter,
            screenHeight + progressInsideToCenter)
        canvas?.drawArc(progressInsideRectF,185f,80f,false,progressInsidePaint)  //画内部进度条

        if(progressDragAngle in 0f .. 80f) {  //拖动条控制的区域角度只能在80度内
            canvas?.drawArc(progressInsideRectF, 185f, progressDragAngle, false, progressCoverPaint)
        }

        /* 内部拖动条图片按钮部分 */
        canvas?.drawBitmap(progressDragBmpNormal,progressDrawBmpX,progressDrawBmpY,bmpPaint)



        Log.d(TAG, "----onDraw")


    }


    //绘制扬声器的四种不同的图片
    private fun drawSpeakerBitmap(canvas: Canvas?,speakerBmpMute: Bitmap) {
        canvas?.drawBitmap(speakerBmpMute, speakerBmpX, speakerBmpY, bmpPaint)  //画扬声器图片
    }


    /**
     * 手指触摸监听
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        menu.onTouchEvent(event)


        //获取点击屏幕的x轴和y轴的距离
        var x = event.x
        var y = event.y

        /**
         * 扬声器区域触摸
         */
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {  //手指点击屏幕的操作
                if (x in speakerX..screenWidth) {
                    if (y in speakerY..screenHeight) {  //判断x,y只能触摸的基本范围

                        //获取半圆中x,y的大小
                        x = abs(screenWidth - x)
                        y = abs(screenHeight - y)

                        if (sqrt(x.pow(2) + y.pow(2)) <= speakerRadius) {  //判断x,y触摸的具体范围(原点到圆环的半径相等)
                            isSpeakerPressed = true
                            Log.d(TAG, "移动true----onTouchEvent")
                            invalidate() //刷新重绘
                        }
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> { //手指移动屏幕的操作
                if (x in speakerX..screenWidth) {
                    if (y in speakerY..screenHeight) {

                        x = abs(screenWidth - x)
                        y = abs(screenHeight - y)

                        if (sqrt(x.pow(2) + y.pow(2)) <= speakerRadius) {
                            isSpeakerPressed = true
                            Log.d(TAG, "移动true----onTouchEvent")
                        } else {
                            isSpeakerPressed = false
                        }

//                            Toast.makeText(context, "true", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isSpeakerPressed = false
//                        Toast.makeText(context, "移动false", Toast.LENGTH_SHORT).show()
                }

                invalidate()  //刷新重绘
            }
            MotionEvent.ACTION_UP -> {   //手指抬起离开屏幕的操作
                if (x in speakerX..screenWidth) {
                    if (y in speakerY..screenHeight) {
                        //获取三角形对应的x,y轴的两条直角边的长度
                        x = abs(screenWidth - x)
                        y = abs(screenHeight - y)

                        if (sqrt(x.pow(2) + y.pow(2)) <= speakerRadius) {
                            isSpeakerPressed = false
                            invalidate()   //刷新重绘
                            Log.d(TAG, "false----onTouchEvent")
                        }
//                            Toast.makeText(context, "离开false", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

        /**
         * 音量条
         */
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {  //手指点击屏幕的操作
                val dx = event.x
                val dy = event.y
                isVolumePressed = isDragBmpNearBy(dx, dy,volumeDragBmpX,volumeDragBmpY,volumeDragBmpNormal) //判断是否在按钮图片区域点击
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {  //手指在屏幕移动的操作

                isVolumeMoveLogic(event)

            }
            MotionEvent.ACTION_UP -> {    //手指抬起离开屏幕的操作
                isVolumePressed = false
                invalidate()
            }
        }

        /**
         * 后退键扇形区域
         */
        when(event.action){
            MotionEvent.ACTION_DOWN -> {

                isBackPressed = isPlayBackNearBy(event,0f,30f)  //判断手指能够按下的区域(结果：手指只能在后退键扇形区域中被按下)

                isPausePressed = isPlayBackNearBy(event,30.1f,60f)  //手指只能在暂停键扇形区域中被按下


                isForwardPressed = isPlayBackNearBy(event,60.1f,90f)  //手指只能在快进键扇形区域中被按下

                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_UP -> {

                if(isPausePressed){  //如果手指在播放暂停扇形区域被按下后
                    isPauseOrPlay = !isPauseOrPlay   //用于手指切换暂停状态和播放状态

                    Log.d(TAG,"手指切换暂停状态和播放状态")
                }

                isBackPressed = false
                isForwardPressed = false
                isPausePressed = false

                invalidate()
            }
        }


        /**
         *  播放进度条
         */
        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                val dx =event.x
                val dy = event.y
                isProgressDrawPressed = isDragBmpNearBy(dx,dy,progressDrawBmpX,progressDrawBmpY,progressDragBmpNormal) //判断是否在按钮图片区域点击

                Log.d(TAG,"播放进度条手指按下了")
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {

                isProgressMoveLogic(event)

                Log.d(TAG,"播放进度条手指移动了")

            }
            MotionEvent.ACTION_UP -> {

                isProgressDrawPressed = false
                invalidate()

                Log.d(TAG,"播放进度条手指抬起了")
            }
        }


//        Log.d(TAG,"----onTouchEvent")

        return true

    }

    private fun isProgressMoveLogic(event: MotionEvent) {

        var mx = event.x
        var my = event.y

        val x = event.x
        val y = event.y

        //获取手指触摸x,y轴坐标的角度值
//        val angle = getAngle(x,y, screenWidth, screenHeight)

        //获取三角形对应的x,y轴的两条直角边的长度
        mx = abs(screenWidth - mx)
        my = abs(screenHeight - my)

        //获取区域2的cos角度
        val volumeRectF2Angle = (mx / sqrt(mx.pow(2) + my.pow(2))).toDouble()
        var angle = acos(volumeRectF2Angle) //获取反余弦函数值
        angle = toDegrees(angle)  //将圆弧度转化为角度数

        Log.e(TAG,"角度值：$angle")

        if(event.pointerCount == 1) {    //单手模式
            if (isProgressDrawPressed) {  //按钮图片被按下
                Log.e(TAG,"播放进度条被按下了")
                //判断角度具体范围
                if (angle in 5f..85f) {
                    Log.e(TAG,"角度执行")
                    //获取在(相同r半径下，angle角度不同)时，x,y轴的具体位置
                    val dragBmpX = screenWidth - getAngleX(angle.toFloat(), progressInsideToCenterMaxR)
                    val dragBmpY = screenHeight - getAngleY(angle.toFloat(), progressInsideToCenterMaxR)

                    //获取角度为5度时的x,y轴的值
                    val testX = getAngleX(angle.toFloat(), progressInsideToCenterMaxR)

                    //当角度为5度时，只能获取y轴的坐标值时，才能进度条距离x轴的最小距离
                    val dragBmpXYtoFive = getAngleY(5.0f, progressInsideToCenterMaxR)  //获取最小的

                    if (mx in 0f..screenWidth) {
                        if (my in 0f..screenHeight) {

                            Log.e(TAG,"手指触摸x,y范围执行")

                            //拖动条按钮图片最大限制x轴值

                            val bmpMaxX = screenWidth - progressDragBmpPressed.width / 2 - dragBmpXYtoFive

                            if (progressDrawBmpX in 0f..bmpMaxX) {   //拖动条按钮图片X轴坐标限制条件  -> 才能保证x轴不被拖出外面去
                                Log.e(TAG,"按钮图片X轴切换执行")
                                progressDrawBmpX = dragBmpX
                                if (progressDrawBmpX >= bmpMaxX) progressDrawBmpX = bmpMaxX - 10f
                            }

                            //拖动条按钮图片最大限制x轴值

                            val bmpMaxY = screenHeight - progressDragBmpPressed.height / 2 - dragBmpXYtoFive
                            if (progressDrawBmpY in 0f..bmpMaxY) {  //拖动条按钮图片Y轴坐标限制条件  -> 才能保证y轴不被拖出外面去
                                Log.e(TAG,"按钮图片Y轴切换执行")
                                progressDrawBmpY = dragBmpY
                                if (progressDrawBmpY >= bmpMaxY) progressDrawBmpY = bmpMaxY - 10f
                            }


                            progressDragAngle = angle.toFloat()  //设置覆盖区域的进度条角度

                            Log.e(TAG,"$bmpMaxX----$bmpMaxY---$dragBmpXYtoFive---$testX")
                            //1005.71533----1904.7153---48.284637---424.53964
                        }
                    }

                    invalidate() //刷新重绘
                }

            }
        }


    }

    /**
     * 判断是否在后退键基本范围内被按下
     */
    private fun isPlayBackNearBy(event: MotionEvent,startAngle: Float,endAngle: Float): Boolean {
        var dx = event.x
        var dy = event.y

        //获取手指三角形x,y的值大小
        dx = abs(screenWidth - dx)
        dy = abs(screenHeight - dy)

        //获取三角形的余弦角度
        val playBackAngle = dx / (sqrt(dx.pow(2) + dy.pow(2))).toDouble()  //cos角度 = 对边 / 斜边
        var playBackacos = acos(playBackAngle)  //获取反余弦函数值
        playBackacos = toDegrees(playBackacos)   //将反余弦函数值弧度转角度值


        return sqrt(dx*dx + dy*dy) in volumeToCenterMaxR..playToCenterMaxR &&  playBackacos in startAngle..endAngle
        //判断条件：1.不能大于播放矩形的最大半径，不能小于音量条矩形的最大半径 2.角度在0-30度之间
    }


    /**
     * 音量条手指移动事件监听
     */

    private fun isVolumeMoveLogic(event: MotionEvent) {
        /**
         * 用于计算的手指运动轨迹X，Y轴距离圆心的距离(利用轨迹1、轨迹2可以确定音量条的真正的运动轨迹)
         */
        //轨迹1X,Y轴
        var mx = event.x
        var my = event.y

        //获取x,y轴正常的焦点
        val firstMx = event.x
        val firstMy = event.y

        //音量拖动图片所在的最大半径大小(该半径要大于区域1的半径150dp,默认图片的半径也大于150dp)
        val volumeRadiusMax = dp2Px(context, 120f)


        if (event.pointerCount == 1) {  //判断是否是单手指操作
            if (isVolumePressed) {

                //获取三角形对应的x,y轴的两条直角边的长度
                mx = abs(screenWidth - mx)
                my = abs(screenHeight - my)

                //获取区域2的cos角度
                val volumeRectF2Angle = (mx / sqrt(mx.pow(2) + my.pow(2))).toDouble()
                var volumeRectF2cos = acos(volumeRectF2Angle) //获取反余弦函数值
                volumeRectF2cos = toDegrees(volumeRectF2cos)  //将圆弧度转化为角度数

                /**
                 * 1.测试代码块：根据(x和斜边)反推导出 余弦值 cos = x / 斜边
                 */
                val h = (1 / 2.0)  //余弦值 cos = x / 斜边
                var cos = acos(h)
                cos = toDegrees(cos)

                Log.d(TAG, "测试cos角度：$cos")

                /**
                 * 2.根据度数正推导出 x = 余弦值 cos * 斜边
                 */

                if (volumeRectF2cos in 0f..90f) {   //限制手指触摸x,y轴坐标计算的角度在 0 - 90 度之间
                    //1.角度转弧度
                    var radian = Math.toRadians(volumeRectF2cos)
                    //正余弦值
                    radian = cos(radian)

                    /**
                     * 确定最终具体的x,y坐标的值  (要用屏幕的宽度，高度减去计算的宽高)
                     *  最终x = 屏幕宽度 - 三角形x轴边长
                     *  最终y = 屏幕高度 - 三角形y轴边长
                     */
                    //三角形的x,y轴的边长值
                    val x = radian * volumeRadiusMax
                    val y = sqrt(volumeRadiusMax.pow(2) - x.pow(2.0))
                    //最终的x,y具体位置
                    val volumeDragBmpMx = abs(screenWidth - x)
                    val volumeDragBmpMy = abs(screenHeight - y)


                    Log.d(TAG, "测试cos角度：$volumeDragBmpMx-----$volumeDragBmpMy")


                    //手指移动到哪，X轴就到哪
                    if (firstMx in 0f..screenWidth) { //判断音量条x轴移动分大小基本范围
                        //移动mx,my轴的位置，不能超过圆弧线的范围
//                    if (sqrt(mx * mx + my * my) <= volumeRadiusMax) {  //限制只能在半圆弧内移动

                        //手指移动到哪，Y轴就到哪
                        if (firstMy in 0f..screenHeight) { //判断音量条y轴移动分大小基本范围



                            if(volumeDragBmpX in 0f..screenWidth-(volumeDragBmpPressed.width/2)){  //限制拖动条按钮图片最大的x轴位置(防止按钮图片拖出外面)
                                volumeDragBmpX = volumeDragBmpMx.toFloat()    //获取拖动移动角度后通过三角形公式计算出的X轴，并赋值
                                //如果x轴坐标值大于等于限定的具体时(最大值限定值screenWidth-(volumeDragBmpPressed.width/2))就重新赋值(一定要小于最大限制值)
                                if (volumeDragBmpX >= screenWidth-(volumeDragBmpPressed.width/2)){
                                    volumeDragBmpX = screenWidth-(volumeDragBmpPressed.width/2 + 10f)  //重新赋值，防止按钮图片的X轴坐标不更新角度值
                                }

                                Log.d(TAG,"限定值x------$volumeDragBmpX")
                            }
                            volumeRectFProgress = volumeRectF2cos.toFloat()  //设置移动的cos角度,即区域2的圆弧角度

                            when (volumeRectFProgress) {
                                in 0f..1.5f -> speakerBmpStyle = SPEAKER_MUTE
                                in 1.6f..30f -> speakerBmpStyle = SPEAKER_MIN   //在区间 1到30度之间
                                in 30.1f..60f -> speakerBmpStyle = SPEAKER_MIDDLE //在区间 30到60度之间
                                in 60.1f..90f -> speakerBmpStyle = SPEAKER_MAX  //在区间 60到90度之间
                                else -> {

                                }
                            }
                            //本地数据持久化存储
                            PlayControllerDao.cacheVolumeProgressDegree(context, volumeRectFProgress, volumeDragBmpX)

                            if(volumeDragBmpY in 0f..screenHeight - (volumeDragBmpPressed.height/2)){  //限制拖动条按钮图片最大的y轴位置(防止按钮图片拖出外面)
                                volumeDragBmpY = volumeDragBmpMy.toFloat()    //获取拖动移动角度后通过三角形公式计算出的Y轴，并赋值
                                //如果y轴坐标值大于等于限定的具体时(最大值限定值screenHeight - (volumeDragBmpPressed.height/2)就重新赋值(一定要小于最大限制值)
                                if (volumeDragBmpY >= screenHeight - (volumeDragBmpPressed.height/2)){
                                    volumeDragBmpY = screenHeight - (volumeDragBmpPressed.height/2 + 10f)  //重新赋值，防止按钮图片的Y轴坐标不更新角度值
                                }

                                Log.d(TAG,"限定值y------$volumeDragBmpY")
                            }
                            volumeRectFProgress = volumeRectF2cos.toFloat()
                            //本地数据持久化存储
                            PlayControllerDao.cacheVolumeProgressDegreeY(context, volumeDragBmpY)
                        }

                        Log.d(TAG, "圆弧的角度：$volumeRectFProgress")

                    }


                    invalidate() //刷新重绘
                }
            }
        }
    }

    /**
     * 判断音量条图片是否在具体的范围被按下
     */
    private fun isDragBmpNearBy(x: Float, y: Float,dragBmpX: Float,dragBmpY: Float,dragBmp: Bitmap): Boolean {

        /**
         *  1.dragBmpX、dragBmpY 拖动条按钮图片的x,y轴的坐标
         *  2.dragBmp 拖动条按钮图片
         */

        //判断音量条图片触摸的具体范围(就是音量条图片本身的图片区域范围)
        if (x >= dragBmpX - dragBmp.width && x <= dragBmpX + dragBmp.width) {
            return y >= dragBmpY - dragBmp.height && y <= dragBmpY + dragBmp.height
        }

        return false

    }

    //获取手指触摸的r半径
    private fun currentVolumeToCenter(x: Float, y: Float, screenWidth: Float, screenHeight: Float): Float {

        return sqrt( (screenWidth - x).pow(2) + (screenHeight - y))
    }

    /**
     * 设置拖动条的圆弧角度
     */
    fun setProgressDegree(degree: Float){

        volumeRectFProgress = degree
    }

    fun getDegree(): Float{
        return  volumeRectFProgress
    }


    /**
     * 获取画笔
     */
    private fun generatePoint(color: Int, style: Paint.Style, alpha: Int, width: Float,round: Paint.Cap? = Paint.Cap.SQUARE, shader: Shader? = null): Paint {
        //创建Paint对象，并使用apply函数
        Paint().apply {

            this.color = color
            this.style = style
            this.alpha = alpha
            this.strokeWidth = width

            this.strokeCap = round  //设置圆角
            this.isAntiAlias = true //设置抗锯齿
            this.isDither = true //设置抖动
            this.shader = shader

        }.let {
            return it
        }
    }

    /**
     * 获取画笔
     */
    private fun generatePoint(style: Paint.Style, alpha: Int, width: Float,shader: Shader? = null): Paint {
        //创建Paint对象，并使用apply函数
        Paint().apply {
            this.style = style
            this.alpha = alpha
            this.strokeWidth = width

//            this.strokeCap = Paint.Cap.ROUND  //设置圆角
            this.isAntiAlias = true //设置抗锯齿
            this.isDither = true //设置抖动
            this.shader = shader

        }.let {
            return it
        }
    }

    private fun dp2Px(context: Context, dp: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f)
    }

    private fun px2Dp(context: Context, px: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (px / scale + 0.5f)
    }

    /**
     * 三角形推导公式
     */
    private fun TriangleDerivation(){
        /**
         * 1. 根据(x和斜边)反推导出 余弦值 cos = x / 斜边  (条件：已知斜边、x大小 结果：求cos角度)
         */
        val h = (1 / 2.0)  //余弦值 cos = x / 斜边
        var cos = acos(h)
        cos = toDegrees(cos)

        Log.d(TAG, "测试cos角度：$cos")


        /**
         * 根据度数正推导出 x = 余弦值 cos * 斜边  (条件：已知斜边、cos角度 结果：求x,y值大小)
         */
        //1.角度转弧度
        var radian = Math.toRadians(45.0)
        //正余弦值
        radian = cos(radian)

        //确定具体的x,y坐标的值
        val radius = dp2Px(context,120f).toDouble()  //半径

        val x = radian * radius
        val y = sqrt(radius.pow(2.0) - x.pow(2.0))

        volumeDragBmpX = abs(screenWidth - x).toFloat()
        volumeDragBmpY = abs(screenHeight - y).toFloat()

        Log.d(TAG, "测试cos角度：$x-----$y")
    }


    /**
     * 条件：手指触摸的x,y轴的坐标，即三角形的a、b两条直角边长
     * 结果：求cos夹角的角度数
     */
    private fun getAngle(x: Float,y: Float,screenWidth: Float,screenHeight: Float): Float{
        //获取相对的x,y值(即a和b两条边的值)
        val a = abs(screenWidth - x)
        val b = abs(screenHeight - y)

        //获取区域2的cos角度
//        val volumeRectF2Angle = (mx / sqrt(mx.pow(2) + my.pow(2))).toDouble()
//        var volumeRectF2cos = acos(volumeRectF2Angle) //获取反余弦函数值
//        volumeRectF2cos = toDegrees(volumeRectF2cos)  //将圆弧度转化为角度数

        val angleThan = x / sqrt(a.pow(2) + b.pow(2))  //获取对角与斜边的比例
        var angleAcos = acos(angleThan)  //获取反余弦函数值 (这句容易出现问题 -> 错了角度求不到)

        return toDegrees(angleAcos.toDouble()).toFloat()   //将圆弧度转成角度值
    }


    /**
     * 条件：三角形的反余弦角、斜边（已知角度，半径值），求x和y轴的坐标
     */
    private fun getAngleX(angle: Float? = 0f,radiusMax: Float): Float{
        //1.角度转弧度
        var radian = Math.toRadians(angle!!.toDouble())
        //2.弧度转正余弦函数值(即对边/斜边)
        radian = cos(radian)

        //3.根据公式获取对应角度的 X轴值
        val angleX = radian * radiusMax
        return angleX.toFloat()
    }

    /**
     * 根据上面获取x轴位置，求y轴的位置
     */
    private fun getAngleY(angle: Float,radiusMax: Float): Float{
        return sqrt(radiusMax.pow(2) - getAngleX(angle,radiusMax).pow(2))
    }
}

data class PlayParameter(var isPressed: Boolean)