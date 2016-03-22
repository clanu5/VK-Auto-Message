package com.qwert2603.vkautomessage.user_details;

import android.graphics.Bitmap;

import com.qwert2603.vkautomessage.base.BaseView;

public interface UserView extends BaseView {
    void showSelected(boolean selected);
    void showName(String name);
    void showPhoto(Bitmap bitmap);
}
