package com.qwert2603.vkautomessage.delete_user;

import com.qwert2603.vkautomessage.base.delete_item.DeleteItemView;

public interface DeleteUserView extends DeleteItemView {
    void showLoading();
    void showUserName(String userName);
    void showRecordsCount(int recordsCount);
    void showEnabledRecordsCount(int enabledRecordsCount);
}
