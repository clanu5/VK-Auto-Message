package com.qwert2603.vkautomessage.record_details;

import android.widget.ImageView;

import com.qwert2603.vkautomessage.base.BaseView;

public interface RecordView extends BaseView {
    ImageView getPhotoImageView();
    void showUserName(String userName);
    void showMessage(String message);
    void showEnabled(boolean enabled);
    void showTime(String time);
    void showRepeatType(String repeatType);
    void showRepeatInfo(String repeatInfo);
    void showLoading();
    void showEditMessage(String message);
    void showEditTime(int hour, int minute);
    void showEditRepeatType(int repeatType);
    void showEditPeriod(int period);
    void showEditDaysInWeek(int daysInWeek);
    void showEditDayInYear(int month, int dayOfMonth);
    void showToast(int stringRes);
}
