package com.star.theBigDipper.widget.navigator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.star.theBigDipper.R

class KeyWordView : AppCompatEditText, TextWatcher {
    private val mDefaultBgColor = Color.parseColor("#666666")
    private val mDefaultBgWith = 5
    private val mDefaultPasswordCount = 6
    private val mDefaultDivisionColor = Color.parseColor("#666666")
    private val mDefaultDivisionWith = 2
    private val mDefaultPasswordColor = Color.parseColor("#666666")
    private val mDefaultPasswrdSize = 18

    //背景框的圆角
    private var mBgRadius = 0

    //画笔
    private lateinit var mPaint: Paint

    //背景框颜色
    private var mBgColor = 0

    //背景框线的宽度
    private var mBgWidth = 0

    //总密码的个数
    private var mPasswordCount = 0

    //分割线的颜色
    private var mDivisionColor = 0

    //分割线的宽度
    private var mDivisionWith = 0

    //密码圆点的颜色
    private var mPasswordColor = 0

    //密码圆点的大小
    private var mPasswordSize = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttr(context, attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttr(context, attrs)
        init()
    }

    /**
     * 初始化xml 设置的属性
     *
     * @param context
     * @param attrs
     */
    private fun initAttr(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KeyWordView)
            mBgWidth = typedArray.getDimension(R.styleable.KeyWordView_bg_width, mDefaultBgWith.toFloat()).toInt()
            mBgColor = typedArray.getColor(R.styleable.KeyWordView_bg_color, mDefaultBgColor)
            mPasswordCount = typedArray.getInteger(R.styleable.KeyWordView_password_count, mDefaultPasswordCount)
            mDivisionColor = typedArray.getColor(R.styleable.KeyWordView_division_color, mDefaultDivisionColor)
            mDivisionWith = typedArray.getDimension(R.styleable.KeyWordView_division_width, mDefaultDivisionWith.toFloat()).toInt()
            mPasswordColor = typedArray.getColor(R.styleable.KeyWordView_password_color, mDefaultPasswordColor)
            mPasswordSize = typedArray.getDimension(R.styleable.KeyWordView_password_size, mDefaultPasswrdSize.toFloat()).toInt()
            mBgRadius = typedArray.getDimension(R.styleable.KeyWordView_bg_radius, 0f).toInt()
            typedArray.recycle()
        }
    }

    /**
     * 初始化画笔
     */
    private fun init() {
        mPaint = Paint()
        //抗锯齿
        mPaint.isAntiAlias = true
        //防抖动
        mPaint.isDither = true
        //设置输入最大长度
        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(mPasswordCount))
        //设置只能输入数字
        inputType = InputType.TYPE_CLASS_TEXT
        //监听文本变化，回调
        addTextChangedListener(this)
    }

    override fun onDraw(canvas: Canvas) {
        //绘制背景
        drawBg(canvas)
        //绘制分割线
        drawDivision(canvas)
        //绘制圆点密码
        drawPassword(canvas)
    }

    /**
     * 绘制圆点密码
     *
     * @param canvas
     */
    private fun drawPassword(canvas: Canvas) {
        //一个格子的长度
        val itemWith = (measuredWidth - 2 * mBgWidth) / mPasswordCount
        mPaint.color = mPasswordColor
        mPaint.strokeWidth = mPasswordSize.toFloat()
        mPaint.style = Paint.Style.FILL
        val text = text.toString().trim { it <= ' ' }
        if (!TextUtils.isEmpty(text)) {
            for (i in 0 until text.length) {
                val cx = mBgWidth + itemWith / 2 + (itemWith * i).toFloat()
                val cy = measuredHeight / 2.toFloat()
                canvas.drawCircle(cx, cy, mPasswordSize.toFloat(), mPaint)
            }
        }
    }

    /**
     * 绘制分割线
     *
     * @param canvas
     */
    private fun drawDivision(canvas: Canvas) {
        val itemWith = (measuredWidth - mBgWidth) / mPasswordCount
        mPaint.color = mDivisionColor
        mPaint.strokeWidth = mDivisionWith.toFloat()
        for (i in 0 until mPasswordCount - 1) {
            val startX = mBgWidth + (itemWith * (i + 1)).toFloat()
            val startY = 0f
            val endY = startY + measuredHeight
            canvas.drawLine(startX, startY, startX, endY, mPaint)
        }
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private fun drawBg(canvas: Canvas) {
        val rectF = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
        mPaint.color = mBgColor
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mBgWidth.toFloat()
        canvas.drawRoundRect(rectF, mBgRadius.toFloat(), mBgRadius.toFloat(), mPaint)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        //当输入密码为最长的时候 回调
        if (!TextUtils.isEmpty(s) && s.length == mPasswordCount && listener != null) {
            listener!!.full(s.toString().trim { it <= ' ' })
        }
    }

    private var listener: IPasswordFullListener? = null
    fun setPasswordFullListener(listener: IPasswordFullListener?) {
        this.listener = listener
    }

    interface IPasswordFullListener {
        fun full(password: String?)
    }
}