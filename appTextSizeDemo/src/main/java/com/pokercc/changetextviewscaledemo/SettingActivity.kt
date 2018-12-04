package com.pokercc.changetextviewscaledemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.SeekBar
import com.pokercc.testsizehelper.AppTextSizeHelper
import kotlinx.android.synthetic.main.activity_setting.*


class SettingActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        seekBar.max = 100
        seekBar.progress = (AppTextSizeHelper.fontScaled * 100).toInt() - 100
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                AppTextSizeHelper.onFontScaled(seekBar.context, progress * 0.01f + 1)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        fontScale.text = "fontScale:${resources.displayMetrics.scaledDensity}"
    }

}
