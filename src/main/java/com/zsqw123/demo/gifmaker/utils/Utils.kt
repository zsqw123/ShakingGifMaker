package com.zsqw123.demo.gifmaker.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.annotation.DrawableRes
import com.zsqw123.demo.gifmaker.R
import com.zsqw123.demo.gifmaker.app

fun getBitmap(resources: Resources, size: Int, @DrawableRes id: Int): Bitmap {
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

fun Context.getTestBitmap(size: Int = app.resources.displayMetrics.widthPixels) = getBitmap(resources, size, R.drawable.test)

fun View.gone() {
    visibility = View.GONE
}

fun View.visable() {
    visibility = View.VISIBLE
}
