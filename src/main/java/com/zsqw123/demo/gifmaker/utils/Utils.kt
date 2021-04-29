package com.zsqw123.demo.gifmaker.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import com.zsqw123.demo.gifmaker.R
import com.zsqw123.demo.gifmaker.app

private fun getBitmap(resources: Resources, size: Int, @DrawableRes id: Int): Bitmap {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeResource(resources, id, options)
    options.apply {
        inJustDecodeBounds = false
        inDensity = minOf(outWidth, outHeight)
        inTargetDensity = size
    }
    return BitmapFactory.decodeResource(resources, id, options)
}

fun getTestBitmap(size: Int = app.resources.displayMetrics.widthPixels) = getBitmap(app.resources, size, R.drawable.test)
