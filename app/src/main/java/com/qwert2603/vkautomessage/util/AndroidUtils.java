package com.qwert2603.vkautomessage.util;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

public final class AndroidUtils {

    /**
     * Post runnable to execute on main thread with given delay.
     */
    public static void runOnUI(Runnable runnable, long delay) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delay);
    }

    /**
     * @return is given activity in portrait orientation.
     */
    public static boolean isPortraitOrientation(Activity activity) {
        return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * Resolve value of given attribute.
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
    public static void setViewEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setViewEnabled(((ViewGroup) view).getChildAt(i), enabled);
            }
        }
    }

}
