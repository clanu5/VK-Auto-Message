package com.qwert2603.vkautomessage.view;

public interface RecordView extends BaseView {
    void showUserName(String userName);
    void showMessage(String message);
    void showEnabled(boolean enabled);
    void showTime(String time);
    void showChooseUser();
    void showChooseTime();
}
