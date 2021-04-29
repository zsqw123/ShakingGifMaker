package com.zsqw123.demo.gifmaker.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.zsqw123.demo.gifmaker.utils.getTestBitmap
import kotlin.random.Random

class ShakingImageView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private lateinit var bitmap: Bitmap
    private var offset = 0.15f
        set(value) {
            field = value.coerceAtMost(1f).coerceAtLeast(0f)
        }
    private val random = Random(114514)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bitmap = context.getTestBitmap(w.coerceAtMost(h))
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmapMesh(bitmap, 6, 6, genMesh(bitmap.width, bitmap.height, 6, 6), 0, null, 0, null)
        invalidate()
    }

    private fun genMesh(w: Int, h: Int, xCount: Int = 3, yCount: Int = 3) = genMesh(w.toFloat(), h.toFloat(), xCount, yCount)
    private fun genMesh(w: Float, h: Float, xCount: Int = 3, yCount: Int = 3): FloatArray {
        val singleW = w / xCount
        val singleH = h / yCount
        val res = FloatArray((xCount + 1) * (yCount + 1) * 2)
        var index = 0
        for (y in 0..yCount) for (x in 0..xCount) {
            val xOffset = (random.nextFloat() - 0.5f) * 2 * offset
            val yOffset = (random.nextFloat() - 0.5f) * 2 * offset
            res[index++] = (x * singleW + (singleW * xOffset))
            res[index++] = (y * singleH + (singleH * yOffset))
        }
        return res
    }
}