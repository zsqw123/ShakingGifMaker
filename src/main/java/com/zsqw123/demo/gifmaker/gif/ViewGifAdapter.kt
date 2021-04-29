package com.zsqw123.demo.gifmaker.gif

import android.graphics.Bitmap
import android.view.View
import androidx.core.view.drawToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ViewGifAdapter(private val output: File, private val fps: Int, private val view: View) {
    /** @param count Int second */
    suspend fun generate(count: Int, progressCallback: (Int) -> Unit) {
        withContext(Dispatchers.IO) {
            val encoder = GifEncoder(output)
            encoder.setFrameRate(fps)
            for (i in 1..count) {
                encoder.addFrame(view.drawToBitmap(Bitmap.Config.RGB_565))
                progressCallback(((i / count.toFloat()) * 100).toInt())
            }
            encoder.finish()
        }
    }
}