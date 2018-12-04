package com.pokercc.testsizehelper

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

/**
 * 动态修改整个app文字大小的帮助类
 *
 */
object AppTextSizeHelper {


    private lateinit var activityTextHelperManager: ActivityTextHelperManager


    // 默认缩放比例
    private var defaultScaledDensity: Float? = null

    // 字体缩放的比例
    var fontScaled: Float = 1.0f
//    // 新的缩放比例
//    val newScaledDensity: Float
//        get() = defaultScaledDensity * fontScaled

    fun init(
        app: Application,
        viewPredicate: (View) -> Boolean = { true }//自定义是否允许字体缩放的校验器
    ) {
        activityTextHelperManager = ActivityTextHelperManager(viewPredicate)
        app.registerActivityLifecycleCallbacks(activityTextHelperManager)
        defaultScaledDensity = app.resources.displayMetrics.scaledDensity

    }

    /**
     * 改变字体大小
     * @param fontScaled 字体缩放比例[0.1,8]
     * @param globalApp 是否是整个app生效
     */
    fun onFontScaled(context: Context, fontScaled: Float) {
        if (fontScaled < 0.1 || fontScaled > 8) {
            throw IllegalArgumentException("fontScale to large or too small,is $fontScaled")
        }
        if (defaultScaledDensity == null) {
            throw IllegalStateException("please init on application onCreate at first")
        }
        if (fontScaled == this.fontScaled) {
            return
        }

        Log.d("AppTextSizeHelper", "fontScaled=$fontScaled")
        this.fontScaled = fontScaled

        // 改变设置

        val resources = context.resources
        resources.updateConfiguration(
            resources.configuration.also { it.fontScale = this.fontScaled },
            resources.displayMetrics.also { it.scaledDensity = defaultScaledDensity!! * fontScaled })

        // 动态修改当前application每个activity里面的textView
        activityTextHelperManager.onFontScaled(fontScaled)
    }

}

private class ActivityTextHelperManager(private val viewPredicate: (View) -> Boolean) :
    Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

    private val textSizeHelperMap: MutableMap<Activity, ActivityTextSizeHelper> = mutableMapOf()
    override fun onActivityResumed(activity: Activity) {
        if (!textSizeHelperMap.containsKey(activity)) {
            textSizeHelperMap[activity] = ActivityTextSizeHelper(
                activity.findViewById(android.R.id.content), viewPredicate
            )
        }
    }

    override fun onActivityDestroyed(activity: Activity?) {
        textSizeHelperMap.remove(activity)
    }

    fun onFontScaled(fontScaled: Float) {
        textSizeHelperMap.values.forEach {
            it.onFontScaled(fontScaled)
        }

    }
}
