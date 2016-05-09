package com.qwert2603.vkautomessage.delete_user;

import com.qwert2603.vkautomessage.base.BaseView;

public interface DeleteUserView extends BaseView {
    void showLoading();
    void showUserName(String userName);
    void showRecordsCount(int recordsCount);
    void showEnabledRecordsCount(int enabledRecordsCount);
    void submitDone(int userId);
}
