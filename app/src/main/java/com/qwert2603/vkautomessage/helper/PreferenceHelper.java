package com.qwert2603.vkautomessage.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.qwert2603.vkautomessage.VkAutoMessageApplication;

import javax.inject.Inject;

public class PreferenceHelper {

    public static final int NO_MYSELF_ID = -1;

    private static final String myselfIdKey = "myselfId";
    private static final String lastNotificationIdKey = "lastNotificationId";

    private SharedPreferences mSharedPreferences;

    @Inject
    Context mAppContext;

    public PreferenceHelper() {
        VkAutoMessageApplication.getAppComponent().inject(PreferenceHelper.this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
    }

    public int getMyselfId() {
        return mSharedPreferences.getInt(myselfIdKey, NO_MYSELF_ID);
    }

    public void setMyselfId(int myselfId) {
        mSharedPreferences.edit().putInt(myselfIdKey, myselfId).apply();
    }

    public int getLastNotificationId() {
        return mSharedPreferences.getInt(lastNotificationIdKey, 0);
    }

    public void setLastNotificationId(int lastNotificationId) {
        mSharedPreferences.edit().putInt(lastNotificationIdKey, lastNotificationId).apply();
    }

    public void clear() {
        mSharedPreferences.edit()
                .clear()
                .apply();
    }
}
