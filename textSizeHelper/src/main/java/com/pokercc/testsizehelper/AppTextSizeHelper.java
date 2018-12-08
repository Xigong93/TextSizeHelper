package com.pokercc.testsizehelper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态修改整个app文字大小的帮助类
 */
public final class AppTextSizeHelper {


    private static ActivityTextHelperManager activityTextHelperManager;
    private static float defaultScaledDensity;

    public static float fontScaled;
    private static ActivityTextSizeHelper.ViewMatcher viewMatcher;

    public static void init(Application app) {
        activityTextHelperManager = new ActivityTextHelperManager();
        activityTextHelperManager.setViewMatcher(viewMatcher);
        app.registerActivityLifecycleCallbacks(activityTextHelperManager);
        defaultScaledDensity = app.getResources().getDisplayMetrics().scaledDensity;
    }

    public static void setViewMatcher(ActivityTextSizeHelper.ViewMatcher viewMatcher) {
        AppTextSizeHelper.viewMatcher = viewMatcher;
        activityTextHelperManager.setViewMatcher(viewMatcher);

    }

    public static void onFontScaled(Context context, float fontScaled) {
        if (fontScaled < 0.1 || fontScaled > 8) {
            throw new IllegalArgumentException("fontScale too large or too small,is " + fontScaled);
        }
        if (defaultScaledDensity == 0) {
            throw new IllegalStateException("please init on application oonCreate at first");
        }

        if (AppTextSizeHelper.fontScaled == fontScaled) {
            return;
        }
        Log.d("AppTextSizeHelper", "fontScaled=" + fontScaled);

        更新全局设置:
        {
            final Resources resources = context.getResources();
            final Configuration configuration = resources.getConfiguration();
            configuration.fontScale = fontScaled;
            final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            displayMetrics.scaledDensity = defaultScaledDensity * fontScaled;
            resources.updateConfiguration(configuration, displayMetrics);
        }
        动态修改activity字体大小:
        {
            activityTextHelperManager.onFontScaled(fontScaled);
        }

    }

    private static class ActivityTextHelperManager implements Application.ActivityLifecycleCallbacks {


        private final Map<Activity, ActivityTextSizeHelper> textSizeHelperMap = new HashMap<>();

        private ActivityTextSizeHelper.ViewMatcher viewMatcher;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (!textSizeHelperMap.containsKey(activity)) {
                final ActivityTextSizeHelper textSizeHelper = new ActivityTextSizeHelper(activity);
                textSizeHelper.setViewMatcher(viewMatcher);
                textSizeHelperMap.put(activity, textSizeHelper);
            }
        }

        public void setViewMatcher(ActivityTextSizeHelper.ViewMatcher viewMatcher) {
            this.viewMatcher = viewMatcher;
            for (ActivityTextSizeHelper textSizeHelper : textSizeHelperMap.values()) {
                textSizeHelper.setViewMatcher(viewMatcher);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }


        void onFontScaled(float fontScaled) {
            for (ActivityTextSizeHelper textSizeHelper : textSizeHelperMap.values()) {
                textSizeHelper.onFontScaled(fontScaled);
            }
        }


        @Override
        public void onActivityDestroyed(Activity activity) {
            textSizeHelperMap.remove(activity);
        }
    }
}
