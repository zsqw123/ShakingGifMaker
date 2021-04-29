package com.zsqw123.demo.gifmaker.gif

import android.graphics.Bitmap
import android.view.View
import androidx.core.graphics.scale
import androidx.core.view.drawToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ViewGifAdapter(private val output: File, private val fps: Int, private val view: View) {
    /** @param count Int second */
    suspend fun generate(count: Int, outputSize: Int = 200, progressCallback: (Int) -> Unit) {
        withContext(Dispatchers.IO) {
            val encoder = GifEncoder(output)
            encoder.setFrameRate(fps)
            for (i in 1..count) {
                val origin = view.drawToBitmap(Bitmap.Config.RGB_565)
                encoder.addFrame(origin.scale(width = outputSize, (outputSize.toFloat() / origin.width * origin.height).toInt()))
                progressCallback(((i / count.toFloat()) * 100).toInt())
            }
            encoder.finish()
        }
    }
}