package com.pokercc.changetextviewscaledemo

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView

class FontScaleView(context: Context, attrs: AttributeSet? = null) :
    TextView(context, attrs) {


    override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)
        text = "fontScaled:${resources.displayMetrics.scaledDensity}"
        Log.d(this::class.java.simpleName, "resource=$resources")
    }

}