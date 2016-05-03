package com.qwert2603.vkautomessage.record_details;

import android.widget.ImageView;

import com.qwert2603.vkautomessage.base.BaseView;

public interface RecordView extends BaseView {
    ImageView getPhotoImageView();
    void showUserName(String userName);
    void showMessage(String message);
    void showEnabled(boolean enabled);
    void showTime(String time);
    void showLoading();
    void showEditMessage(String message);
    void showEditTime(int minuteAtDay);
    void showEditDay(/*todo*/);
    void showToast(int stringRes);
}
