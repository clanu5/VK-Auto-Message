package com.qwert2603.vkautomessage.model;

import android.support.annotation.NonNull;

public class RecordWithUser {
    @NonNull
    public final Record mRecord;

    @NonNull
    public final User mUser;

    public RecordWithUser(@NonNull Record record, @NonNull User user) {
        mRecord = record;
        mUser = user;
    }
}
