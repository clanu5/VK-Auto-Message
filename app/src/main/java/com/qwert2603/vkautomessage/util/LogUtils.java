package com.qwert2603.vkautomessage.util;

import android.util.Log;

public class LogUtils {

    public static final String APP_TAG = "AASSDD";

    public static void d(String s) {
        d(APP_TAG, s);
    }

    public static void d(String tag, String s) {
        Log.d(tag, s);
    }

    public static void e(String s) {
        e(APP_TAG, s);
    }

    public static void e(String tag, String s) {
        Log.e(tag, s);
    }

    public static void e(Throwable t) {
        Log.e(APP_TAG, "ERROR!!!", t);
    }

}
