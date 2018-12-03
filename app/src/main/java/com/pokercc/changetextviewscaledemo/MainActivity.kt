package com.pokercc.changetextviewscaledemo

import android.annotation.TargetApi
import android.content.Context
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
import kotlinx.android.synthetic.main.activity_main.*

const val TAG = "测试改变字体大小"


class ChangeTextSizeHelper(private val rootView: ViewGroup) {
    private val textSizeMap: MutableMap<Int, Float> = mutableMapOf()

    var fontScaled: Float? = null
    fun onFontScaled(fontScaled: Float) {
        Log.d("ChangeTextSizeHelper", "fontScaled=$fontScaled")
        this.fontScaled = fontScaled
        rootView.allViews()
            .filter { it is TextView }
            .map { it as TextView }
            .filter { it.id != View.NO_ID }
            .onEach {
                if (!textSizeMap.containsKey(it.id)) {
                    textSizeMap[it.id] = it.textSize

                }
            }
            .forEach {
                it.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeMap[it.id]!! * fontScaled)
            }
    }

    /**
     * 递归查找全部的子view
     */
    fun ViewGroup.allViews(): List<View> {
        val views = mutableListOf<View>()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            views.add(child)
            if (child is ViewGroup) {
                views.addAll(child.allViews())
            }
        }
        return views.toList()
    }


    fun getScaledDensity(context: Context): Float {
        return (this.fontScaled ?: 1.0f)
    }

}

class MainActivity : AppCompatActivity() {

    var textSizeHelper: ChangeTextSizeHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "onCreate")
        val rootView = findViewById<ViewGroup>(android.R.id.content)
        textSizeHelper = ChangeTextSizeHelper(rootView)
        seekBar.max = 100
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textSizeHelper!!.onFontScaled(progress * 0.01f + 1)
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
            it.text = "AAAAAAA"
            it.id = View.generateViewId()
        })

    }

    var defaultScaledDensity: Float? = null

    override fun getResources(): Resources {
        val resources = super.getResources()
        if (defaultScaledDensity == null) {
            defaultScaledDensity = resources.displayMetrics.scaledDensity
        }
        if (textSizeHelper != null) {
            resources.displayMetrics.scaledDensity = defaultScaledDensity!! * textSizeHelper!!.getScaledDensity(this)
        }

        return resources
    }
}
