package com.qwert2603.vkautomessage.util;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;

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

}
