package com.qwert2603.vkautomessage.base.navigation;

import com.qwert2603.vkautomessage.base.BaseView;

public interface NavigationView extends BaseView {
    void performLogOut();
    void showMyselfName(String userName);
    void showMyselfPhoto(String url);
    void showLoadingMyself();
}
