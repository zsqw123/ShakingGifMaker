package com.zsqw123.demo.gifmaker.gif

import android.graphics.Bitmap
import android.view.View
import androidx.core.graphics.scale
import androidx.core.view.drawToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class GifAdapter(private var output: File, private var fps: Int, private var outputWidth: Int = 200) {
    /** @param count Int frame count */
    suspend fun viewToGif(view: View, count: Int, progressCallback: (Int) -> Unit) {
        withContext(Dispatchers.IO) {
            val encoder = GifEncoder(output)
            encoder.setFrameRate(fps)
            for (i in 1..count) {
                val origin = view.drawToBitmap(Bitmap.Config.RGB_565)
                encoder.addFrame(origin.scale(width = outputWidth, (outputWidth.toFloat() / origin.width * origin.height).toInt()))
                progressCallback(((i / count.toFloat()) * 100).toInt())
            }
            encoder.finish()
        }
    }
}