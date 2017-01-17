package com.qwert2603.vkautomessage.user_details;

import com.qwert2603.vkautomessage.base.BaseView;

public interface UserView extends BaseView {
    void showName(String name);
    void showPhoto(String url, String initials);
    void hideRecordsCount();
    void showRecordsCount(int recordsCount, int enabledRecordsCount);
}
