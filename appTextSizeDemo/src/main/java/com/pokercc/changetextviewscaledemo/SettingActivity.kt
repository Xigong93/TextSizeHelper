package com.pokercc.changetextviewscaledemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_setting.*
import pokercc.android.testsizehelper.AppTextSizeHelper


class SettingActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        seekBar.max = 100
        seekBar.progress = (AppTextSizeHelper.fontScaled * 100).toInt() - 100
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                AppTextSizeHelper.onFontScaled(seekBar.context, progress * 0.01f + 1)
                fontScale.text = "fontScale:${resources.displayMetrics.scaledDensity}"

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        fontScale.text = "fontScale:${resources.displayMetrics.scaledDensity}"
    }

}
