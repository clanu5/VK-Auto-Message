package com.qwert2603.vkautomessage.base.navigation;

import android.widget.ImageView;

import com.qwert2603.vkautomessage.base.BaseView;

public interface NavigationView extends BaseView {
    void performLogOut();
    void showMyselfName(String userName);
    ImageView getMyselfPhotoImageView();
    void showLoadingMyself();
}
