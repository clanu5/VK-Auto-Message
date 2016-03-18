package com.qwert2603.vkautomessage.view;

import com.qwert2603.vkautomessage.model.entity.Record;

public interface RecordListView extends ListView<Record> {
    void moveToRecordDetails(int recordId);
}
