package com.github.qqstepview

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.min

/**
 * 仿QQ运动步数
 */
class QQStepView : View {

    private var mOuterColor = Color.BLACK
    private var mInnerColor = Color.RED
    private var mTextColor = Color.BLACK
    private var mTextSize = 50
    private var mStrokeWidth = 20
    private var mDefaultSize = 200
    private var mStartAngle = 135f
    private var mMaxAngle = 270f

    private var mMaxStep = 0
    private var mCurrentStep = 0

    private lateinit var mTextPaint: Paint
    private lateinit var mOuterPaint: Paint
    private lateinit var mInnerPaint: Paint

    private val mRectF = RectF()
    private var mTextBounds = Rect()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttrs(context, attrs)
        initPaint()
    }

    @Synchronized
    fun setMaxStep(maxStep: Int) {
        this.mMaxStep = maxStep
    }

    @Synchronized
    fun setCurrentStep(currentStep: Int) {
        this.mCurrentStep = currentStep
        // 重绘
        invalidate()
    }

    @Synchronized
    fun setCurrentStepSmoothly(
        currentStep: Int,
        duration: Long = 1000L,
        interpolator: TimeInterpolator = DecelerateInterpolator()
    ) {
        ObjectAnimator.ofInt(0, currentStep).apply {
            this.duration = duration
            this.interpolator = interpolator
            this.addUpdateListener {
                setCurrentStep(it.animatedValue as Int)
            }
        }.start()
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.QQStepView)
        mOuterColor = typedArray.getColor(R.styleable.QQStepView_qsvOuterColor, mOuterColor)
        mInnerColor = typedArray.getColor(R.styleable.QQStepView_qsvInnerColor, mInnerColor)
        mTextColor = typedArray.getColor(R.styleable.QQStepView_qsvTextColor, mTextColor)
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.QQStepView_qsvTextSize, mTextSize)
        mStrokeWidth =
            typedArray.getDimensionPixelSize(R.styleable.QQStepView_qsvStrokeWidth, mStrokeWidth)
        typedArray.recycle()
    }

    private fun initPaint() {
        mTextPaint = Paint().apply {
            isAntiAlias = true
            textSize = mTextSize.toFloat()
            color = mTextColor
        }
        mOuterPaint = Paint().apply {
            isAntiAlias = true
            strokeWidth = mStrokeWidth.toFloat()
            color = mOuterColor
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }
        mInnerPaint = Paint().apply {
            isAntiAlias = true
            strokeWidth = mStrokeWidth.toFloat()
            color = mInnerColor
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = mDefaultSize
        }
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mDefaultSize
        }
        val measuredSize = min(widthSize, heightSize)
        val halfStroke = mStrokeWidth / 2f
        mRectF.set(halfStroke, halfStroke, measuredSize - halfStroke, measuredSize - halfStroke)
        setMeasuredDimension(measuredSize, measuredSize)
    }

    override fun onDraw(canvas: Canvas) {
        // 画外弧
        canvas.drawArc(mRectF, mStartAngle, mMaxAngle, false, mOuterPaint)

        // 画文字
        val text = "$mCurrentStep"
        mTextPaint.getTextBounds(text, 0, text.length, mTextBounds)
        val dx = width / 2f - mTextBounds.width() / 2f
        val fontMetricsInt = mTextPaint.fontMetricsInt
        val dy =
            height / 2f + ((fontMetricsInt.bottom - fontMetricsInt.top) / 2f - fontMetricsInt.bottom)
        canvas.drawText(text, dx, dy, mTextPaint)

        // 画内弧
        if (mMaxStep <= 0) return
        val progress = mCurrentStep.toFloat() / mMaxStep
        canvas.drawArc(mRectF, mStartAngle, mMaxAngle * progress, false, mInnerPaint)
    }
}