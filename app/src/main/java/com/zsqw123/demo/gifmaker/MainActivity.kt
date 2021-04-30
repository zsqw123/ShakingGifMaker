package com.zsqw123.demo.gifmaker

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.zsqw123.demo.gifmaker.databinding.ActivityMainBinding
import com.zsqw123.demo.gifmaker.gif.GifAdapter
import com.zsqw123.demo.gifmaker.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            edit.doAfterTextChanged { tv.text = it.toString() }
            var picFlag = true
            switchIv.setOnCheckedChangeListener { _, isChecked ->
                picFlag = if (isChecked) {
                    iv.visable()
                    true
                } else {
                    iv.invisable()
                    false
                }
            }
            switchTv.setOnCheckedChangeListener { _, isChecked -> if (isChecked) tv.visable() else tv.gone() }
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    iv.offset = progress / 100f
                    tv.offset = progress / 100f
                }
            })
            btGen.setOnClickListener {
                val outFile = File(cacheDir, System.currentTimeMillis().toString())
                if (outFile.exists()) outFile.delete()
                lifecycleScope.launch {
                    runOnUiThread { groupLoading.visable() }
                    GifAdapter(outFile, 48).viewToGif(if (picFlag) shown else tv, 8) {
                        runOnUiThread { progressText.text = String.format("❤ 导出中 %d ❤", it) }
                    }
                    delay(200)
                    runOnUiThread {
                        progressText.text = "❤ 导出完成 ❤"
                        progress.invisable()
                    }
                    shareGif(outFile)
                }
            }
            btImport.setOnClickListener {
                startActivityForResult(Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }, SELECT_PICTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data?.data == null) return
        if (requestCode == SELECT_PICTURE) {
            lifecycleScope.launch(Dispatchers.IO) {
                val input = contentResolver.openInputStream(data.data!!)
                BitmapFactory.decodeStream(input)?.let {
                    it.save(this@MainActivity)
                    runOnUiThread {
                        binding.iv.fromImport = true
                        binding.iv.requestLayout()
                    }
                }
                input?.close()
            }
        }
    }

    companion object {
        const val SELECT_PICTURE = 1
    }
}