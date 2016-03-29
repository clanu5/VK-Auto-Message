package com.qwert2603.vkautomessage.delete_record;

import com.qwert2603.vkautomessage.base.BaseView;

public interface DeleteRecordView extends BaseView {
    void showUserName(String userName);
    void showMessage(String message);
    void showEmpty();
    void submitDone(int recordId);
}
