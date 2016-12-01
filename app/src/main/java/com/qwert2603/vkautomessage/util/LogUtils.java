package com.qwert2603.vkautomessage.util;

import android.util.Log;

import com.qwert2603.vkautomessage.errors_show.ErrorsHolder;

public final class LogUtils {

    public static final String APP_TAG = "AASSDD";
    public static final String ERROR_MSG = "ERROR!!!";

    private static final ErrorsHolder sErrorsHolder = new ErrorsHolder();

    public static void d(String s) {
        d(APP_TAG, s);
    }

    public static void d(String tag, String s) {
        Log.d(tag, s);
    }

    public static void e(Throwable t) {
        sErrorsHolder.addError(t);
        Log.e(APP_TAG, ERROR_MSG, t);
    }

    public static void e(String s) {
        Log.e(APP_TAG, s);
    }
}
