package com.qwert2603.vkautomessage.base.navigation;

import android.widget.ImageView;

import com.qwert2603.vkautomessage.base.BaseView;

public interface NavigationView extends BaseView {
    void showLogOut();
    void showUserName(String userName);
    ImageView getUserPhotoImageView();
    void showLoading();
    void setContentTranslationX(float translationX);
    void performBackPressed();
}
