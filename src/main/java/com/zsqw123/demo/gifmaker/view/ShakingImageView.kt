package com.zsqw123.demo.gifmaker.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import com.zsqw123.demo.gifmaker.utils.getTestBitmap

class ShakingImageView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private lateinit var bitmap: Bitmap
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bitmap= getTestBitmap(w)
    }
}