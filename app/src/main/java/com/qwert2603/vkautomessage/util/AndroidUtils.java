package com.qwert2603.vkautomessage.util;

import android.os.Build;

public final class AndroidUtils {

    public static boolean isLollipopOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

}
