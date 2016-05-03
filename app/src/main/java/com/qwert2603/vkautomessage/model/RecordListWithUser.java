package com.qwert2603.vkautomessage.model;

import java.util.List;

public class RecordListWithUser {
    public final List<Record> mRecordList;
    public final User mUser;

    public RecordListWithUser(List<Record> recordList, User user) {
        mRecordList = recordList;
        mUser = user;
    }
}
