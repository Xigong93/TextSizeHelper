package com.pokercc.testsizehelper;

import android.app.Activity;
import android.content.Context;

public class PreferenceUtil {

    private static final String PREFERENCE_FILE_NAME = "pokercc.android.textsizehelper";
    private static final String FONT_SCALE_SUFFIX = ".font_scale";
    private static final String APP_FONT_SCALE = "app" + FONT_SCALE_SUFFIX;

    public static void saveAppFontScale(Context context, float fontScale) {
        context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE).edit()
                .putFloat(APP_FONT_SCALE, fontScale)
                .apply();
    }

    public static float getAppFontScale(Context context) {
        return context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
                .getFloat(APP_FONT_SCALE, 1.0f);
    }

    public static void saveActivityFontScale(Activity activity, float fontScale) {
        activity.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE).edit()
                .putFloat(activity.getClass().getName() + FONT_SCALE_SUFFIX, fontScale)
                .apply();
    }

    public static float getActivityFontScale(Activity activity) {
        return activity.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
                .getFloat(activity.getClass().getName() + FONT_SCALE_SUFFIX, 1.0f);
    }
}
