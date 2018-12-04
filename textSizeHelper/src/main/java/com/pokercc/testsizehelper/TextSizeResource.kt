package com.pokercc.testsizehelper

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics

class TextSizeResource(resources: Resources, scaledDensity: Float) : Resources(
    resources.assets,
    DisplayMetrics().also {
        it.setTo(resources.displayMetrics)
        it.scaledDensity = scaledDensity
    },
    resources.configuration
)