package com.zsqw123.demo.gifmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.zsqw123.demo.gifmaker.databinding.ActivityMainBinding
import com.zsqw123.demo.gifmaker.utils.gone
import com.zsqw123.demo.gifmaker.utils.visable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            edit.doAfterTextChanged { tv.text = it.toString() }
            switchIv.setOnCheckedChangeListener { _, isChecked -> if (isChecked) iv.visable() else iv.gone() }
            switchTv.setOnCheckedChangeListener { _, isChecked -> if (isChecked) tv.visable() else tv.gone() }
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    iv.offset = progress / 100f
                    tv.offset = progress / 100f
                }
            })
        }
    }
}