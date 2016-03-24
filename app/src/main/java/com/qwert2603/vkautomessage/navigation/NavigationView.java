package com.qwert2603.vkautomessage.navigation;

import android.graphics.Bitmap;

import com.qwert2603.vkautomessage.base.BaseView;

public interface NavigationView extends BaseView {
    void showSettings();
    void showLogOut();
    void showUserName(String userName);
    void showUserPhoto(Bitmap photo);
}
