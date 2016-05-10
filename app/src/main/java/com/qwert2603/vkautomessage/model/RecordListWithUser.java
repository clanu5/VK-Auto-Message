package com.qwert2603.vkautomessage.model;

import android.support.annotation.NonNull;

import java.util.List;

public class RecordListWithUser {
    @NonNull
    public final List<Record> mRecordList;

    @NonNull
    public final User mUser;

    public RecordListWithUser(@NonNull List<Record> recordList, @NonNull User user) {
        mRecordList = recordList;
        mUser = user;
    }
}
