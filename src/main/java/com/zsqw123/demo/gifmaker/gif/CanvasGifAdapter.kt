package com.zsqw123.demo.gifmaker.gif

import android.graphics.Bitmap
import android.graphics.Canvas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CanvasGifAdapter(
    private val output: File, private val fps: Int,
    private val width: Int, private val height: Int,
    private val canvasCall: (Canvas) -> Unit
) {
    /** @param duration Int second */
    suspend fun generate(duration: Int, progressCallback: (Int) -> Unit) {
        val canvas = Canvas()
        withContext(Dispatchers.IO) {
            val encoder = GifEncoder(output)
            encoder.setFrameRate(fps)
            val total = fps * duration
            for (i in 0 until total) {
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                canvas.setBitmap(bitmap)
                canvasCall(canvas)
                encoder.addFrame(bitmap)
                progressCallback(i / total)
            }
            encoder.finish()
        }
    }
}