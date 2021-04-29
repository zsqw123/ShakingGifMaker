package com.zsqw123.demo.gifmaker.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.use
import com.zsqw123.demo.gifmaker.utils.getDimension
import com.zsqw123.demo.gifmaker.utils.sp
import kotlin.random.Random

class ShakingTextView(context: Context, attrs: AttributeSet) : View(context, attrs){
    private val random = Random(1)
    private val offset = 0.1f
    private var mTextSize = 18.sp

    var text = "❤ 何言nb ❤"
        set(value) {
            field = value
            invalidate()
        }

    private val textStartList = ArrayList<RectF>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        mTextSize = context.getDimension(attrs, android.R.attr.textSize, 18.sp)
        paint.textSize = mTextSize
        context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.text)).use {
            it.getString(0)?.let { s -> text = s }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val inputW = MeasureSpec.getSize(widthMeasureSpec)
        val inputH = MeasureSpec.getSize(heightMeasureSpec)
        val realW = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> inputW
            else -> (paint.measureText(text) * (1 + offset)).toInt().coerceAtMost(inputW)
        }
        caculateSize(text, realW)
        val realH = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> inputH
            else -> (textStartList.last().bottom + paint.fontMetrics.bottom - paint.fontMetrics.top)
                .toInt().coerceAtMost(inputH)
        }
        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(realW, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(realH, MeasureSpec.EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas) {
        textStartList.forEachIndexed { i, rectF ->
            canvas.drawText(text, i, i + 1, rectF.randomHori(), rectF.randomVerti(), paint)
        }
        invalidate()
    }

    private fun caculateSize(str: String, w: Int) {
        textStartList.clear()
        var totalLeft = 0f
        var totalHeight = -paint.ascent()
        val offsetY = totalHeight * offset
        val widths = FloatArray(str.length)
        paint.getTextWidths(str, widths)
        widths.forEachIndexed { i, it ->
            val offsetX = it * offset
            if (totalLeft + offsetX > w || str[i] == '\n') {
                totalLeft = 0f
                totalHeight += paint.fontMetrics.bottom - paint.fontMetrics.top
            }
            textStartList.add(RectF(totalLeft, totalHeight, totalLeft + offsetX, totalHeight + offsetY))
            totalLeft += it + offsetX
        }
    }

    private fun RectF.randomHori() = if (random.nextInt(2) == 0) left else right

    private fun RectF.randomVerti() = if (random.nextInt(2) == 0) top else bottom
}