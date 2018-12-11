package com.pokercc.changetextviewscaledemo

import android.app.Application
import pokercc.android.testsizehelper.AppTextSizeHelper

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppTextSizeHelper.init(this)
    }
}