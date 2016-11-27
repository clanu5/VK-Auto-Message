package com.qwert2603.vkautomessage.record_list;

import com.qwert2603.vkautomessage.base.list.ListView;
import com.qwert2603.vkautomessage.model.Record;

public interface RecordListView extends ListView<Record> {
    void showUserName(String userName);
    void showDontWriteToDeveloper();
}
