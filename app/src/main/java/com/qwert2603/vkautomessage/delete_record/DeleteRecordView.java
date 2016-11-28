package com.qwert2603.vkautomessage.delete_record;

import com.qwert2603.vkautomessage.base.delete_item.DeleteItemView;

public interface DeleteRecordView extends DeleteItemView {
    void showLoading();
    void showUserName(String userName);
    void showMessage(String message);
}
