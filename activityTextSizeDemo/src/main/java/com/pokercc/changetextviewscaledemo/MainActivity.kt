package com.pokercc.changetextviewscaledemo

import android.annotation.TargetApi
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import com.pokercc.testsizehelper.ActivityTextSizeHelper
import kotlinx.android.synthetic.main.activity_main.*

const val TAG = "测试改变字体大小"


class MainActivity : AppCompatActivity() {

    var activityTextSizeHelper: ActivityTextSizeHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "onCreate")
        val rootView = findViewById<ViewGroup>(android.R.id.content)
        activityTextSizeHelper = ActivityTextSizeHelper(rootView)
        seekBar.max = 100
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                activityTextSizeHelper!!.onFontScaled(progress * 0.01f + 1)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun onAddNewTextViewClick(view: View) {
        ll_content.addView(TextView(this).also {
            it.text = "新添加的textview"
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            it.id = View.generateViewId()
        })

    }

    fun onAddNewUnableTextViewClick(view: View) {
        ll_content.addView(TextView(this).also {
            it.text = "新添加的textview,不能缩放字体"
            it.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            it.setTag(R.id.TEXT_SIZE_HELPER_USER_DP, 1)
        })

    }

    fun onStartNewActivityClick(view: View) {
        startActivity(Intent(this, OtherActivity::class.java))
    }

    fun onRecreateActivityClick(view: View) {
        recreate()
    }

    override fun getResources(): Resources {
        if (activityTextSizeHelper != null) {
            return activityTextSizeHelper!!.getProxyResource(super.getResources())
        }
        return super.getResources()
    }
}
