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


/**
 * 修改文字大小的帮助类
 */
class TextSizeHelper(private val rootView: ViewGroup) {


    /**
     * 扩展属性，原始字体大小，单位sp
     */
    private var TextView.originSize: Float?
        get() = getTag(R.id.TEXT_SIZE_HELPER_ORIGIN_SIZE) as Float?
        set(value) {
            setTag(R.id.TEXT_SIZE_HELPER_ORIGIN_SIZE, value)
        }

    // 默认缩放比例
    private val defaultScaledDensity: Float by lazy { rootView.resources.displayMetrics.scaledDensity }
    // 字体缩放的比例
    public var fontScaled: Float = 1.0f
    // 新的缩放比例
    public val newScaledDensity: Float
        get() = defaultScaledDensity * fontScaled

    init {
        // 保存textview的默认尺寸
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            allTextViews()
                .forEach {
                    if (it.originSize == null) {
                        it.originSize = it.textSize / it.resources.displayMetrics.scaledDensity
                    }
                }
        }
    }


    /**
     * 改变字体大小，调用次方法
     */
    fun onFontScaled(fontScaled: Float) {
        Log.d("TextSizeHelper", "fontScaled=$fontScaled")
        this.fontScaled = fontScaled
        allTextViews()
            .forEach {
                it.setTextSize(TypedValue.COMPLEX_UNIT_SP, it.originSize!!)
            }
    }


    private fun allTextViews(): List<TextView> {
        return rootView.allViews()
            .filter { it is TextView }
            .map { it as TextView }
            .filter { it.id != View.NO_ID }

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


}

class MainActivity : AppCompatActivity() {

    var textSizeHelper: TextSizeHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "onCreate")
        val rootView = findViewById<ViewGroup>(android.R.id.content)
        textSizeHelper = TextSizeHelper(rootView)
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
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            it.id = View.generateViewId()
        })

    }

    override fun getResources(): Resources {

        return super.getResources().apply {
            textSizeHelper?.apply {
                displayMetrics.scaledDensity = textSizeHelper!!.newScaledDensity
            }
        }
    }
}
