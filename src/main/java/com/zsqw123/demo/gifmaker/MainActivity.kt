package com.zsqw123.demo.gifmaker

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.zsqw123.demo.gifmaker.databinding.ActivityMainBinding
import com.zsqw123.demo.gifmaker.gif.ViewGifAdapter
import com.zsqw123.demo.gifmaker.utils.gone
import com.zsqw123.demo.gifmaker.utils.invisable
import com.zsqw123.demo.gifmaker.utils.visable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
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
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    iv.offset = progress / 100f
                    tv.offset = progress / 100f
                }
            })
            btGen.setOnClickListener {
                val outFile = File(filesDir, "share.gif")
                if (outFile.exists()) outFile.delete()
                lifecycleScope.launch {
                    runOnUiThread { groupLoading.visable() }
                    ViewGifAdapter(outFile, 24, if (picFlag) shown else tv).generate(5) {
                        runOnUiThread { progressText.text = String.format("❤ 导出中 %d ❤", it) }
                    }
                    delay(200)
                    runOnUiThread { progressText.text = "❤ 导出完成 ❤" }
                }
            }
        }
    }
}