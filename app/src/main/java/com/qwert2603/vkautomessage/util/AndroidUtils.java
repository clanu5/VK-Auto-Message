package com.qwert2603.vkautomessage.util;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

public final class AndroidUtils {

    public static boolean isLollipopOrHigher() {
//        return false;
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static void runOnUI(Runnable runnable, long delay) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delay);
    }

}
