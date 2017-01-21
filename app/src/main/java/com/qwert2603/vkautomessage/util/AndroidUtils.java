package com.qwert2603.vkautomessage.util;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

public final class AndroidUtils {

    private static final Handler sMainLooperHandler;

    static {
        sMainLooperHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Post runnable to execute on main thread with given delay.
     */
    public static void runOnUI(Runnable runnable, long delay) {
        sMainLooperHandler.postDelayed(runnable, delay);
    }

    /**
     * @return is given activity in portrait orientation.
     */
    public static boolean isPortraitOrientation(Activity activity) {
        return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static boolean isMarshmallowOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * Resolve value of given attribute.
     *
     * @return value of attribute in pixels
     */
    @SuppressWarnings("unused")
    public static int resolveAttributeToPixel(Activity activity, int resId) {
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(resId, typedValue, true);
        return TypedValue.complexToDimensionPixelSize(typedValue.data, activity.getResources().getDisplayMetrics());
    }


    /**
     * Set enabled state to view and all its descendants.
     */
    @SuppressWarnings("unused")
    public static void setViewEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setViewEnabled(((ViewGroup) view).getChildAt(i), enabled);
            }
        }
    }

    public static void setActionOnPreDraw(View view, Runnable action) {
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                action.run();
                return true;
            }
        });
    }

}
