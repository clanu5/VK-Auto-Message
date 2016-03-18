package com.qwert2603.vkautomessage.view;

import android.graphics.Bitmap;

public interface UserView extends BaseView {
    void showName(String name);
    void showPhoto(Bitmap bitmap);
}
