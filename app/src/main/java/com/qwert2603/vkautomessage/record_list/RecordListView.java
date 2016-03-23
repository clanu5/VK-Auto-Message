package com.qwert2603.vkautomessage.record_list;

import com.qwert2603.vkautomessage.base.ListView;
import com.qwert2603.vkautomessage.model.entity.Record;

public interface RecordListView extends ListView<Record> {
    void moveToRecordDetails(int recordId);
    void showChooseUser(int currentUserId);
}
