
package com.pokercc.testsizehelper;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态修改单个activity文字大小的帮助类
 * <p>
 * 对某个TextView 或者是ViewGroup 禁用字体缩放的方法
 * 1. 如果某个textView 不需要缩放字体，请设置tag={user_dp...}
 * 2. 或者是代码设置tag,TextView.setTag(R.id.TEXT_SIZE_HELPER_USER_DP,1)
 * 3. 实现自定义的ViewPredicate,排除不支持的view
 */
public final class ActivityTextSizeHelper2 {

    @SuppressWarnings("WeakerAccess")
    public static final String USE_DP = "use_dp";
    private ViewGroup rootView;
    public float fontScaled = 1.0f;
    private final float defaultScaledDensity;
    private final ViewMatcher viewMatcher;
    /**
     * 默认的view 匹配器
     */
    private static final ViewMatcher defaultViewMatcher = new ViewMatcher() {
        @Override
        public boolean match(View view) {

            // 未设置`TEXT_SIZE_HELPER_USER_DP`的tag
            return view.getTag(R.id.TEXT_SIZE_HELPER_USER_DP) == null &&
                    //默认tag，不包含`user_dp`字符串
                    (!(view.getTag() instanceof String) || !((String) view.getTag()).contains(USE_DP));
        }
    };

    public ActivityTextSizeHelper2(Activity activity) {
        this(activity, null);
    }


    public ActivityTextSizeHelper2(Activity activity, ViewMatcher viewMatcher) {
        this.defaultScaledDensity = activity.getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
        this.fontScaled = PreferenceUtil.getActivityFontScale(activity);
        this.viewMatcher = viewMatcher;
    }

    public void onCreate(Activity activity) {

        rootView = activity.findViewById(android.R.id.content);
        this.rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                for (TextView textView : allTextViews()) {
                    if (getTextViewOriginSize(textView) <= 0) {
                        setTextViewOriginSize(textView, textView.getTextSize() / textView.getResources().getDisplayMetrics().scaledDensity);
                    }
                }
            }
        });
        if (Math.abs(defaultScaledDensity - fontScaled) > 0.01) {
            changeTextSize();
        }
    }


    /**
     * 改变字体大小
     *
     * @param fontScaled 字符缩放比例[0.1,8]
     */
    public void onFontScaled(float fontScaled) {
        if (fontScaled < 0.1 || fontScaled > 8) {
            throw new IllegalArgumentException("fontScaled too large or too small,is " + fontScaled);
        }
        if (this.fontScaled == fontScaled) {
            return;
        }
        Log.d("ActivityTextSizeHelper", "fontScaled=" + fontScaled);
        this.fontScaled = fontScaled;
        changeTextSize();
        PreferenceUtil.saveActivityFontScale(getActivityFromView(rootView), fontScaled);

    }


    void changeTextSize() {
        for (TextView textView : allTextViews()) {
            if (getTextViewOriginSize(textView) > 0) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getTextViewOriginSize(textView));
            }
        }
    }

    private Resources proxyResources;

    public float getScaledDensity() {
        return defaultScaledDensity * fontScaled;
    }


    /**
     * 获取代理后的Resource对象
     *
     * @param resources
     * @return
     */
    public Resources getProxyResource(Resources resources) {
        if (proxyResources == null) {
            proxyResources = new TextSizeResource(resources, resources.getConfiguration());
        }
        proxyResources.getDisplayMetrics().scaledDensity = getScaledDensity();
        return proxyResources;

    }

    private static float getTextViewOriginSize(TextView textView) {
        Float originSize = (Float) textView.getTag(R.id.TEXT_SIZE_HELPER_ORIGIN_SIZE);
        if (originSize == null) {
            return 0;
        }
        return originSize;
    }

    private static void setTextViewOriginSize(TextView textView, float v) {
        textView.setTag(R.id.TEXT_SIZE_HELPER_ORIGIN_SIZE, v);
    }

    private static Activity getActivityFromView(View view) {
        Context context = view.getContext();
        while (!(context instanceof Activity)) {
            if (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        return (Activity) context;
    }

    private List<TextView> allTextViews() {
        if (viewMatcher != null) {
            return findAllTextViews(rootView, defaultViewMatcher, viewMatcher);
        } else {
            return findAllTextViews(rootView, defaultViewMatcher);
        }
    }


    private static List<TextView> findAllTextViews(ViewGroup viewGroup, ViewMatcher... viewMatchers) {
        final List<TextView> textViews = new ArrayList<>();
        A:
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            final View child = viewGroup.getChildAt(i);
            B:
            for (ViewMatcher matcher : viewMatchers) {
                if (!matcher.match(child)) {
                    continue A;
                }
            }
            if (child instanceof ViewGroup) {
                textViews.addAll(findAllTextViews((ViewGroup) child, viewMatchers));
                continue;
            }
            if (child instanceof TextView) {
                textViews.add((TextView) child);
            }
        }
        return textViews;
    }

    public interface ViewMatcher {
        /**
         * 是否需要对这个view或者是这个viewGroup的字体缩放进行处理
         *
         * @param view
         * @return true 表示需要处理
         */
        boolean match(View view);
    }


    private static class TextSizeResource extends Resources {

        public TextSizeResource(Resources resources, Configuration config) {
            super(resources.getAssets(), resources.getDisplayMetrics(), config);
        }
    }
}
