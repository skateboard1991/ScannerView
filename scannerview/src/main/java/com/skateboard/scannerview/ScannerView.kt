package com.skateboard.scannerview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View

class ScannerView(context: Context, attrs: AttributeSet?) : View(context, attrs)
{

    private lateinit var animator: ValueAnimator

    private var duration = 2000

    private var linePos = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var downDrawable: Drawable? = null

    private var upDrawable: Drawable? = null

    private val STATE_DOWN = 10

    private val STATE_UP = 11

    private var state = STATE_DOWN

    private var centerColor:Int=Color.parseColor("#50FF0000")

    private var endColor:Int=Color.TRANSPARENT

    constructor(context: Context) : this(context, null)

    init
    {
        if (attrs != null)
        {
            parseAttrs(attrs)
        }
        initAnimator()
        paint.style = Paint.Style.FILL_AND_STROKE
    }


    private fun parseAttrs(attrs: AttributeSet)
    {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScannerView)
        duration = typedArray.getInt(R.styleable.ScannerView_duration, 5000)
        upDrawable = typedArray.getDrawable(R.styleable.ScannerView_upDrawable)
        downDrawable = typedArray.getDrawable(R.styleable.ScannerView_downDrawable)
        typedArray.recycle()
    }

    private fun initAnimator()
    {
        animator = ValueAnimator.ofInt(0, duration)
        animator.duration = duration.toLong()
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener {

            val fraction = it.animatedFraction
            setDrawableAlpha(fraction)
            linePos = height * fraction
            postInvalidate()
        }

        animator.addListener(object : AnimatorListenerAdapter()
        {
            override fun onAnimationRepeat(animation: Animator?)
            {
                super.onAnimationRepeat(animation)
                resetState()
            }
        })
    }

    private fun setDrawableAlpha(fraction: Float)
    {
        if (state == STATE_DOWN)
        {
            if (fraction <= 0.5)
            {
                downDrawable?.alpha = (fraction *2* 255).toInt()
            } else
            {
                downDrawable?.alpha = 255 - (fraction *2* 255).toInt()
            }
        } else
        {
            if (fraction <= 0.5)
            {
                upDrawable?.alpha = (fraction *2* 255).toInt()

            } else
            {
                upDrawable?.alpha = 255 - (fraction *2* 255).toInt()
            }
        }


    }


    private fun resetState()
    {
        if (linePos <= height / 2)
        {
            state = STATE_DOWN
        } else if (linePos >= height.toFloat() / 2)
        {
            state = STATE_UP
        }
    }


    override fun onAttachedToWindow()
    {
        super.onAttachedToWindow()
        animator.start()
    }

    override fun onDetachedFromWindow()
    {
        super.onDetachedFromWindow()
        animator.cancel()
    }

    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)
        canvas?.let {

            drawDrawable(it)
            drawRect(it)
        }
    }

    private fun drawDrawable(canvas: Canvas)
    {
        if (state == STATE_DOWN)
        {
            drawDownDrawable(canvas)
        } else
        {
            drawUpDrawable(canvas)
        }
    }

    private fun drawDownDrawable(canvas: Canvas)
    {
        val downerDrawable = downDrawable
        if (downerDrawable != null)
        {
            val left = (width - downerDrawable.intrinsicWidth) / 2

            val top = (height - downerDrawable.intrinsicHeight) / 2

            downerDrawable.setBounds(left, top, left + downerDrawable.intrinsicWidth, top + downerDrawable.intrinsicHeight)

            downerDrawable.draw(canvas)
        }

    }

    private fun drawUpDrawable(canvas: Canvas)
    {
        val upperDrawable = upDrawable
        if (upperDrawable != null)
        {
            val left = (width - upperDrawable.intrinsicWidth) / 2

            val top = (height - upperDrawable.intrinsicHeight) / 2

            upperDrawable.setBounds(left, top, left + upperDrawable.intrinsicWidth, top + upperDrawable.intrinsicHeight)

            upperDrawable.draw(canvas)
        }

    }

    private fun drawRect(canvas: Canvas)
    {
        val centerX = width.toFloat() / 2
        val centerY = height.toFloat() / 2
        val shader = LinearGradient(centerX, centerY, centerX, linePos, centerColor, endColor, Shader.TileMode.CLAMP)
        paint.shader = shader
        if (linePos <= centerY)
        {
            canvas.drawRect(0f, linePos, width.toFloat(), centerY, paint)
        } else
        {
            canvas.drawRect(0f, centerY, width.toFloat(), linePos, paint)
        }
    }
}