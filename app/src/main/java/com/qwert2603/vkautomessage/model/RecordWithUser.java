package com.qwert2603.vkautomessage.model;

public class RecordWithUser {
    public final Record mRecord;
    public final User mUser;

    public RecordWithUser(Record record, User user) {
        if (record != null && user != null && record.getUserId() != user.getId()) {
            throw new RuntimeException("RecordWithUser#constructor ERROR!!! record.getUserId() != user.getId()");
        }
        mRecord = record;
        mUser = user;
    }
}
