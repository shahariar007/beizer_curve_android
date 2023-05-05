package com.mortuza.drawproject

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs


class CustomMapView: View,View.OnTouchListener {
    private var paint: Paint? = null
    private var paint1: Paint? = null
    private var path: Path? = null

    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f
    private var control1X = 0f
    private var control1Y = 0f
    private var control2X = 0f
    private var control2Y = 0f
    lateinit var zoomLevels:CustomTouchListener
    var leftBottom: Point = Point();
    var leftTop: Point = Point();
    var rightBottom: Point = Point();
    var rightTop: Point = Point();

    var screenHeight = 0;
    var screenWidth = 0;
    var oldDownPressX: Float = -1000f;
    var oldDownPressY: Float = 0f;

    private var isDrawing = false

    private lateinit var gestureDetector: GestureDetector

    private val SWIPE_THRESHOLD = 50
    private val SWIPE_VELOCITY_THRESHOLD = 100

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        init()
    }
//    init {
//        init()
//    }

    private fun init() {
        setWillNotDraw(false)
        val colorValue1 = Color.parseColor("#5E000000")
        paint = Paint()
        paint!!.color = colorValue1
        paint!!.strokeWidth = 3f
        paint!!.style = Paint.Style.STROKE
        val colorValue = Color.parseColor("#B25EDAF6")
        paint1 = Paint()
        paint1!!.color = colorValue
        // paint1!!.strokeWidth = 25f
        paint1!!.style = Paint.Style.FILL

        path = Path()
        isFocusable = true
        isFocusableInTouchMode = true
        isClickable = true
        update();
        setOnTouchListener(this)
    }

    private fun update() {
        leftBottom = Point(0, screenHeight);
        leftTop = Point(0, 0);
        rightBottom = Point(screenWidth, screenHeight);
        rightTop = Point(screenWidth, 0);
    }

    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        val x = event.x
//        val y = event.y
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                var accessDownPoint = screenWidth / 3
//                if (accessDownPoint > event.x || (screenWidth - accessDownPoint) < event.x) {
//
//                    oldDownPressX = event.x
//                    oldDownPressY = event.y
//                }else
//                {
//                    oldDownPressX = -1000f
//                    oldDownPressY = event.y
//                }
//                drawInit()
//            }
//            MotionEvent.ACTION_MOVE -> {
//
//                var result = false
//                if (oldDownPressX == -1000f) {
//                    return true
//                }
//                try {
//                    val diffX: Float = oldDownPressX - x
//                    if (abs(diffX) > SWIPE_THRESHOLD) {
//                        if (diffX > 0) {
//                            drawOn(false, x, y)
//                        } else {
//                            drawOn(true, x, y)
//                        }
//                        result = true
//                    }
//                } catch (exception: Exception) {
//                    exception.printStackTrace()
//                }
//                return result
//            }
//            MotionEvent.ACTION_UP -> drawOff();
//        }
//        return true
//    }

    private fun drawInit() {
        startX = leftTop.x.toFloat()
        startY = leftTop.y.toFloat()
        endX = leftBottom.x.toFloat()
        endY = leftBottom.y.toFloat()
        control1X = x
        control1Y = y
        control2X = x
        control2Y = y
        path!!.moveTo(x, y)
        isDrawing = true
    }

    private fun drawOn(isLeft: Boolean, x: Float, y: Float) {
        if (isDrawing && isLeft) {
            // Update the control points and end points
            startX = leftTop.x.toFloat()
            startY = leftTop.y.toFloat()
            control1X = (x + startX + 200) / 2
            control1Y = y// startY
            control2X = (x + startX+ 200) / 2
            control2Y = y
            endX = leftBottom.x.toFloat()
            endY = leftBottom.y.toFloat()

            // Redraw the curve
            path!!.reset()
            path!!.moveTo(startX, startY)
            path!!.cubicTo(control1X, control1Y, control2X, control2Y, endX, endY)
            invalidate()
        } else if (isDrawing) {
            startX = rightTop.x.toFloat()
            startY = rightTop.y.toFloat()
            control1X = (x + startX+ 200) / 3f
            control1Y = y
            control2X = (x + startX+ 200) / 3f
            control2Y = y
            endX = rightBottom.x.toFloat()
            endY = rightBottom.y.toFloat()

            // Redraw the curve
            path!!.reset()
            path!!.moveTo(startX, startY)
            path!!.cubicTo(control1X, control1Y, control2X, control2Y, endX, endY)
            invalidate()
        }
    }

    private fun drawOff() {
        if (isDrawing) {
            // Reset the path
            path!!.reset()
            isDrawing = false
            oldDownPressX = -1000f;
            invalidate()
        }
    }

//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        // Draw the path on the canvas
//        path?.let {
//            canvas.drawPath(it, paint!!)
//            canvas.drawPath(it, paint1!!)
//
//        }
//    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        path?.let {
            canvas?.drawPath(it, paint!!)
            canvas?.drawPath(it, paint1!!)

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenHeight = h
        screenWidth = w
        update()
    }
    fun setZoomListener(zoomLevel: CustomTouchListener)
    {
        zoomLevels =zoomLevel;
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                var accessDownPoint = screenWidth / 4
                if (accessDownPoint > event.x || (screenWidth - accessDownPoint) < event.x) {

                    oldDownPressX = event.x
                    oldDownPressY = event.y
                }else
                {
                    oldDownPressX = -1000f
                    oldDownPressY = event.y
                }
                drawInit()
            }
            MotionEvent.ACTION_MOVE -> {

                var result = false
                if (oldDownPressX == -1000f) {
                    return false
                }
                try {
                    val diffX: Float = oldDownPressX - x
                    if (abs(diffX) > SWIPE_THRESHOLD) {
                        if (diffX > 0) {
                            drawOn(false, x, y)
                            zoomLevels.onZoomLevel(y,"DrawLeft",screenHeight.toFloat())
                        } else {
                            drawOn(true, x, y)
                            zoomLevels.onZoomLevel(y,"DrawLeft",screenHeight.toFloat())
                        }
                        result = true
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
                return result
            }
            MotionEvent.ACTION_UP -> drawOff();
        }
        return false
    }
}
