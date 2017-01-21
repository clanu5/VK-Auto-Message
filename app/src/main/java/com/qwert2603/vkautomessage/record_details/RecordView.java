package com.qwert2603.vkautomessage.record_details;

import com.qwert2603.vkautomessage.base.BaseView;
import com.qwert2603.vkautomessage.model.Record;

public interface RecordView extends BaseView {
    void showPhoto(String url, String initials);
    void showUserName(String userName);
    void showMessage(String message);
    void showEnabled(boolean enabled);
    void showTime(String time);
    void showRepeatType(String repeatType);
    void showRepeatInfo(String repeatInfo);
    void showLoading();
    void showEditMessage(String message);
    void showEditTime(int hour, int minute);
    void showEditRepeatType(@Record.RepeatType int repeatType);
    void showEditPeriod(int period);
    void showEditDaysInWeek(int daysInWeek);
    void showEditDayInYear(int month, int dayOfMonth);
    void showToast(int stringRes);
}
