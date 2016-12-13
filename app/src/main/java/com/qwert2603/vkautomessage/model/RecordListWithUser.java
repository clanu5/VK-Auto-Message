package com.qwert2603.vkautomessage.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.List;

public class RecordListWithUser implements Parcelable {
    @NonNull
    public final List<Record> mRecordList;

    @NonNull
    public final User mUser;

    public RecordListWithUser(@NonNull List<Record> recordList, @NonNull User user) {
        mRecordList = recordList;
        mUser = user;
    }

    protected RecordListWithUser(Parcel in) {
        mRecordList = in.createTypedArrayList(Record.CREATOR);
        mUser = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mRecordList);
        dest.writeParcelable(mUser, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecordListWithUser> CREATOR = new Creator<RecordListWithUser>() {
        @Override
        public RecordListWithUser createFromParcel(Parcel in) {
            return new RecordListWithUser(in);
        }

        @Override
        public RecordListWithUser[] newArray(int size) {
            return new RecordListWithUser[size];
        }
    };
}
