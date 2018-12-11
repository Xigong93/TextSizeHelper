package pokercc.android.testsizehelper;

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

    public static void init(Application app) {
        init(app, null);
    }

    /**
     * 初始化
     *
     * @param app
     * @param viewMatcher 自定义的view匹配器
     */
    public static void init(Application app, ActivityTextSizeHelper.ViewMatcher viewMatcher) {
        activityTextHelperManager = new ActivityTextHelperManager(viewMatcher);
        app.registerActivityLifecycleCallbacks(activityTextHelperManager);
        defaultScaledDensity = app.getResources().getDisplayMetrics().scaledDensity;
        fontScaled = PreferenceUtil.getAppFontScale(app);
        if (Math.abs(fontScaled - defaultScaledDensity) > 0.01) {
            changeFontScale(app, fontScaled);
        }
    }


    /**
     * 改变字体大小
     *
     * @param context
     * @param fontScaled
     */
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
        AppTextSizeHelper.fontScaled = fontScaled;
        更新全局设置:
        {
            changeFontScale(context, fontScaled);
        }
        动态修改activity字体大小:
        {
            activityTextHelperManager.onFontScaled(fontScaled);
        }

        PreferenceUtil.saveAppFontScale(context, fontScaled);

    }

    private static void changeFontScale(Context context, float fontScaled) {
        final Resources resources = context.getResources();
        final Configuration configuration = resources.getConfiguration();
        configuration.fontScale = fontScaled;
        final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        displayMetrics.scaledDensity = defaultScaledDensity * fontScaled;
        resources.updateConfiguration(configuration, displayMetrics);
    }

    /**
     * activity字体监听器
     */
    private static class ActivityTextHelperManager implements Application.ActivityLifecycleCallbacks {


        private final Map<Activity, ActivityTextSizeHelper> textSizeHelperMap = new HashMap<>();

        private final ActivityTextSizeHelper.ViewMatcher viewMatcher;

        private ActivityTextHelperManager(ActivityTextSizeHelper.ViewMatcher viewMatcher) {
            this.viewMatcher = viewMatcher;

        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (!textSizeHelperMap.containsKey(activity)) {
                textSizeHelperMap.put(activity, new ActivityTextSizeHelper(activity, viewMatcher));
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

            ActivityTextSizeHelper textSizeHelper = textSizeHelperMap.get(activity);
            if (textSizeHelper != null) {
                textSizeHelper.onResume();
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
