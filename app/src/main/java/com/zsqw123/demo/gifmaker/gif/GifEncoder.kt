package com.zsqw123.demo.gifmaker.gif

import android.graphics.Bitmap
import java.io.*
import java.nio.ByteBuffer
import kotlin.math.roundToInt

class GifEncoder(file: File) {
    private var width = 0
    private var height = 0
    private var delay = 10 // frame delay (hundredths)
    private var started = false // ready to output frames
    private var out: OutputStream = file.outputStream()
    private lateinit var curBitmap: Bitmap // current frame
    private lateinit var pixels: ByteArray // BGR byte array from frame
    private lateinit var indexedPixels: ByteArray// converted frame indexed to palette
    private var colorDepth = 0 // number of bit planes
    private lateinit var colorTab: ByteArray // RGB palette
    private var usedEntry = BooleanArray(256) // active palette entries
    private var palSize = 7 // color table size (bits-1)
    private var firstFrame = true
    private var sizeSet = false // if false, get size from first frame
    private var sample = 10 // default sample interval for quantizer

    init {
        writeString("GIF89a") // gif header
    }

    fun addFrame(im: Bitmap) {
        if (!sizeSet) setSize(im.width, im.height) // use first frame's size
        curBitmap = im
        getImagePixels() // convert to correct format if necessary
        analyzePixels() // build color table & map pixels
        if (firstFrame) {
            writeLSD() // logical screen descriptior
            writePalette() // global color table
            writeNetscapeExt() // use NS app extension to indicate reps
        }
        writeGraphicCtrlExt() // write graphic control extension
        writeImageDesc() // image descriptor
        if (!firstFrame) writePalette() // local color table
        writePixels() // encode and write pixel data
        firstFrame = false
    }

    fun finish() = out.apply {
        write(0x3b) // gif trailer
        flush()
        close()
    }

    fun setFrameRate(fps: Int) {
        if (fps > 0) delay = 100 / fps
    }

    /**
     * Sets quality of color quantization (conversion of images
     * to the maximum 256 colors allowed by the GIF specification).
     * Lower values (minimum = 1) produce better colors, but slow
     * processing significantly.  10 is the default, and produces
     * good color mapping at reasonable speeds.  Values greater
     * than 20 do not yield significant improvements in speed.
     *
     * @param quality int greater than 0.
     */
    fun setQuality(quality: Int) {
        sample = if (quality < 1) 1 else quality
    }

    private fun setSize(w: Int, h: Int) {
        if (started && !firstFrame) return
        width = w
        height = h
        if (width < 1) width = 320
        if (height < 1) height = 240
        sizeSet = true
    }

    /**
     * Analyzes image colors and creates color map.
     */
    private fun analyzePixels() {
        val len = pixels.size
        val nPix = len / 3
        indexedPixels = ByteArray(nPix)
        val nq = NeuQuant(pixels, len, sample)
        // initialize quantizer
        colorTab = nq.process() // create reduced palette
        // convert map from BGR to RGB
        run {
            var i = 0
            while (i < colorTab.size) {
                val temp = colorTab[i]
                colorTab[i] = colorTab[i + 2]
                colorTab[i + 2] = temp
                usedEntry[i / 3] = false
                i += 3
            }
        }
        // map image pixels to new palette
        var k = 0
        for (i in 0 until nPix) {
            val index = nq.map(
                pixels[k++].toInt() and 0xff,
                pixels[k++].toInt() and 0xff,
                pixels[k++].toInt() and 0xff
            )
            usedEntry[index] = true
            indexedPixels[i] = index.toByte()
        }
        colorDepth = 8
        palSize = 7
    }

    /**
     * Convert img to bytes, and remove the alpha channel.
     *
     * @param img array of packed ints from an android bitmap, with channels (alpha,red,green,blue)
     * @return array of raw bytes, with channels (blue,green,red)
     */
    private fun getPixelBytes(img: IntArray): ByteArray {
        val bytes = ByteArray(img.size * 3)
        var byteIdx = 0
        for (thisPixel in img) {
            val theseBytes = ByteBuffer.allocate(4).putInt(thisPixel).array()
            // RGB --> BGR
            bytes[byteIdx++] = theseBytes[3]
            bytes[byteIdx++] = theseBytes[2]
            bytes[byteIdx++] = theseBytes[1]
        }
        return bytes
    }

    /**
     * Extracts image pixels into byte array "pixels"
     */
    private fun getImagePixels() {
        val w = curBitmap.width
        val h = curBitmap.height
        val pixelInts = IntArray(w * h * 3)
        curBitmap.getPixels(pixelInts, 0, width, 0, 0, w, h)
        pixels = getPixelBytes(pixelInts)
    }

    /**
     * Writes Graphic Control Extension
     */
    private fun writeGraphicCtrlExt() {
        out.write(0x21) // extension introducer
        out.write(0xf9) // GCE label
        out.write(4) // data block size
        out.write(0)
        writeShort(delay) // delay x 1/100 sec
        out.write(0) // transparent color index
        out.write(0) // block terminator
    }

    /**
     * Writes Image Descriptor
     */
    private fun writeImageDesc() {
        out.write(0x2c) // image separator
        writeShort(0) // image position x,y = 0,0
        writeShort(0)
        writeShort(width) // image size
        writeShort(height)
        // packed fields
        if (firstFrame) {
            // no LCT  - GCT is used for first (or only) frame
            out.write(0)
        } else {
            // specify normal LCT
            out.write(0x80 or palSize) // 6-8 size of color table
        }
    }

    /**
     * Writes Logical Screen Descriptor
     */
    private fun writeLSD() {
        // logical screen size
        writeShort(width)
        writeShort(height)
        // packed fields
        out.write(0x80 or 0x70 or palSize)
        out.write(0) // background color index
        out.write(0) // pixel aspect ratio - assume 1:1
    }

    /**
     * Writes Netscape application extension to define
     */
    private fun writeNetscapeExt() = out.apply {
        write(0x21) // extension introducer
        write(0xff) // app extension label
        write(11) // block size
        writeString("NETSCAPE" + "2.0") // app id + auth code
        write(3) // sub-block size
        write(1) // loop sub-block id
        writeShort(0) // loop count (extra iterations, 0=repeat forever)
        write(0) // block terminator
    }

    private fun writePalette() {
        out.write(colorTab, 0, colorTab.size)
        val n = 3 * 256 - colorTab.size
        for (i in 0 until n) {
            out.write(0)
        }
    }

    /**
     * Encodes and writes pixel data
     */
    private fun writePixels() = LzwEncoder(width, height, indexedPixels, colorDepth).encode(out)

    private fun writeShort(value: Int) {
        out.write(value and 0xff)
        out.write(value shr 8 and 0xff)
    }

    private fun writeString(s: String) {
        for (element in s) out.write(element.toInt())
    }
}