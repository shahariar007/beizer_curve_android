package com.mortuza.drawproject

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs


//class CustomMapView : View, View.OnTouchListener , GestureDetector.OnGestureListener {
//    var canvass: Canvas? = null;
//    var path: Path = Path()
//    var leftBottom:Point =Point();
//    var leftTop:Point =Point();
//    var rightBottom:Point =Point();
//    var rightTop:Point =Point();
//    var paint = Paint()
//    private var startPoint: Point = Point(0,0)
//    private var endPoint: Point = Point(0,0)
//    private var controlPoint1: Point = Point(0,0)
//    private var controlPoint2: Point = Point(0,0)
//
//    private var isSwipingLeft = false
//    private lateinit var gestureDetector: GestureDetector
//    constructor(context: Context) : super(context)
//    {
//        setOnTouchListener(this);
//        cc()
//    }
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
//    {
//        setOnTouchListener(this);
//        cc()
//    }
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
//    {
//        setOnTouchListener(this);
//        cc()
//    }
//
//    fun cc() {
//        setWillNotDraw(false);
//       leftBottom= Point(0,height);
//       leftTop= Point(0,0);
//       rightBottom= Point(width,height);
//       rightTop= Point(width,0);
//        isFocusable = true;
//        isFocusableInTouchMode = true;
//        gestureDetector = GestureDetector(context,this)
//        isClickable = true;
//        paint = Paint()
//        paint.color = Color.BLACK
//        paint.strokeWidth = 25f
//        paint.style = Paint.Style.STROKE
//
//        path = Path()
//
//        gestureDetector = GestureDetector(context, this)
//    }
//
//    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//        canvass=canvas
//
//      //  path.reset();
//
//        // Move to the starting point
//        path.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat());
//
//        // Draw the Bezier curve using the control points and end points
//        path.cubicTo(controlPoint1.x.toFloat(),
//            controlPoint1.y.toFloat(),
//            controlPoint2.x.toFloat(), controlPoint2.y.toFloat(), endPoint.x.toFloat(), endPoint.y.toFloat()
//        );
//
//        // Draw the path on the canvas
//        canvas?.drawPath(path, paint);
//    }
//
//    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//        return gestureDetector.onTouchEvent(event)
//    }
//
//
//    override fun onDown(e: MotionEvent?): Boolean {
//        return true
//    }
//
//    override fun onFling(
//        e1: MotionEvent,
//        e2: MotionEvent,
//        velocityX: Float,
//        velocityY: Float
//    ): Boolean {
//        // Calculate the horizontal distance and velocity between the two touch events
//        val deltaX = e2.x - e1.x
//        val absVelocityX = Math.abs(velocityX)
//
//        // Check if it is a left swipe
//        if (deltaX < 0 && absVelocityX > 200) {
//            isSwipingLeft = true
//
//            // Define the control points and end points of the curve
//            startPoint = Point(100, height / 2)
//            endPoint = Point(width - 100, height / 2)
//            controlPoint1 = Point(width / 3, height / 4)
//            controlPoint2 = Point(width * 2 / 3, height * 3 / 4)
//            Toast.makeText(context,"Click",Toast.LENGTH_LONG).show()
//            // Invalidate the view to trigger a redraw
//            invalidate()
//        }
//        return true
//    }
//
//    override fun onLongPress(e: MotionEvent?) {
//        // Do nothing
//    }
//
//    override fun onScroll(
//        e1: MotionEvent?,
//        e2: MotionEvent?,
//        distanceX: Float,
//        distanceY: Float
//    ): Boolean {
//        // Do nothing
//        return false
//    }
//
//    override fun onShowPress(e: MotionEvent?) {
//        // Do nothing
//    }
//
//    override fun onSingleTapUp(e: MotionEvent?): Boolean {
//        // Do nothing
//        return false
//    }
//}
class CustomMapView : View {

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

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setWillNotDraw(false)
        paint = Paint()
        paint!!.color = Color.BLACK
        paint!!.strokeWidth = 5f
        paint!!.style = Paint.Style.STROKE
        val colorValue = Color.parseColor("#1F5EDAF6")
        paint1 = Paint()
        paint1!!.color = colorValue
       // paint1!!.strokeWidth = 25f
        paint1!!.style = Paint.Style.FILL

        path = Path()
        isFocusable = true
        isFocusableInTouchMode = true
        isClickable = true
        update();
    }

    private fun update() {
        leftBottom = Point(0, screenHeight);
        leftTop = Point(0, 0);
        rightBottom = Point(screenWidth, screenHeight);
        rightTop = Point(screenWidth, 0);
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                var accessDownPoint = screenWidth / 3
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
                if (oldDownPressX == -1000f) return true
                try {
                    val diffX: Float = oldDownPressX - x
                    if (abs(diffX) > SWIPE_THRESHOLD) {
                        if (diffX > 0) {
                            drawOn(false, x, y)
                        } else {
                            drawOn(true, x, y)
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
        // gestureDetector.onTouchEvent(event);
        return true
    }

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
            control1X = (x + startX+ 200) / 2
            control1Y = y
            control2X = (x + startX+ 200) / 2
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the path on the canvas
        path?.let {
            canvas.drawPath(it, paint!!)
            canvas.drawPath(it, paint1!!)
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
}
