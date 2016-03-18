package com.qwert2603.vkautomessage.view;

import android.graphics.Bitmap;

public interface RecordView extends BaseView {
    void showPhoto(Bitmap photo);
    void showUserName(String userName);
    void showMessage(String message);
    void showEnabled(boolean enabled);
    void showTime(String time);
    void showChooseUser();
    void showChooseTime();
}
