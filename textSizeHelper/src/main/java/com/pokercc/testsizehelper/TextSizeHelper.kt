package com.pokercc.testsizehelper

import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView

/**
 * 修改文字大小的帮助类
 * 如果某个textView 不需要缩放字体，请设置tag={user_dp...}
 * 或者是代码设置tag,TextView.setTag(R.id.TEXT_SIZE_HELPER_USER_DP,1)
 */
class TextSizeHelper(private val rootView: ViewGroup) {

    companion object {
        const val USE_DP = "use_dp"
    }

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
    var fontScaled: Float = 1.0f
    // 新的缩放比例
    val newScaledDensity: Float
        get() = defaultScaledDensity * fontScaled

    init {
        // 保存textview的默认尺寸
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            enableAllTextViews()
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
        if (fontScaled != this.fontScaled) {
            Log.d("TextSizeHelper", "fontScaled=$fontScaled")
            this.fontScaled = fontScaled
            enableAllTextViews()
                .forEach {
                    it.originSize?.apply {
                        it.setTextSize(TypedValue.COMPLEX_UNIT_SP, this)
                    }
                }
        }
    }


    /**
     * 递归获取全部的textView
     */
    private fun enableAllTextViews(): List<TextView> {

        return rootView.allTextViews()
            // 过滤代码里设置的tag,不支持缩放字体的TextView
            .filter { it.getTag(R.id.TEXT_SIZE_HELPER_USER_DP) == null }
            // 过滤xml或代码里设置的tag，不支持缩放字体的TextView
            .filter { it.tag !is String || USE_DP !in it.tag as String }

    }
//
//    /**
//     * 递归查找全部的子view
//     */
//    private fun ViewGroup.allViews(): List<View> {
//        val views = mutableListOf<View>()
//        for (i in 0 until childCount) {
//            val child = getChildAt(i)
//            views.add(child)
//            if (child is ViewGroup) {
//                views.addAll(child.allViews())
//            }
//        }
//        return views.toList()
//    }


    /**
     * 递归获取全部的textView
     */
    private fun ViewGroup.allTextViews(): List<TextView> {
        val views = mutableListOf<TextView>()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ViewGroup) {
                views.addAll(child.allTextViews())
                continue
            }
            if (child is TextView) {
                views.add(child)
            }

        }
        return views.toList()
    }

}