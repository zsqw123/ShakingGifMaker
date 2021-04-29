package com.zsqw123.demo.gifmaker.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import com.zsqw123.demo.gifmaker.R
import com.zsqw123.demo.gifmaker.app
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

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

fun View.invisable() {
    visibility = View.INVISIBLE
}

private var cacheInput: String? = null
fun Context.readCacheBitmap(size: Int): Bitmap {
    if (cacheInput == null) cacheInput = File(cacheDir, "tmp").path.apply { println(this) }
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeFile(cacheInput, options)
    options.apply {
        inJustDecodeBounds = false
        inDensity = outWidth
        inTargetDensity = size
    }
    return BitmapFactory.decodeFile(cacheInput, options)!!
}

suspend fun Bitmap.save(context: Context) {
    val file = File(context.cacheDir, "tmp")
    val l: Int
    val t: Int
    val size: Int
    if (width > height) {
        l = (width - height) shr 1
        t = 0
        size = height
    } else {
        l = 0
        t = (height - width) shr 1
        size = width
    }
    withContext(Dispatchers.IO) {
        Bitmap.createBitmap(this@save, l, t, size, size).compress(Bitmap.CompressFormat.JPEG, 100, file.outputStream())
    }
}