package com.qwert2603.vkautomessage.util;

import android.util.Log;

public class LogUtils {

    public static void d(String s) {
        d("AASSDD", s);
    }

    public static void d(String tag, String s) {
        Log.d(tag, s);
    }

    public static void e(String s) {
        e("AASSDD", s);
    }

    public static void e(String tag, String s) {
        Log.e(tag, s);
    }

}
