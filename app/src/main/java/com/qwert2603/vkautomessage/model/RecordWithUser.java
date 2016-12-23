package com.qwert2603.vkautomessage.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class RecordWithUser implements Parcelable {
    @NonNull
    public final Record mRecord;

    @NonNull
    public final User mUser;

    public RecordWithUser(@NonNull Record record, @NonNull User user) {
        mRecord = record;
        mUser = user;
    }

    protected RecordWithUser(Parcel in) {
        mRecord = in.readParcelable(Record.class.getClassLoader());
        mUser = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mRecord, flags);
        dest.writeParcelable(mUser, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecordWithUser> CREATOR = new Creator<RecordWithUser>() {
        @Override
        public RecordWithUser createFromParcel(Parcel in) {
            return new RecordWithUser(in);
        }

        @Override
        public RecordWithUser[] newArray(int size) {
            return new RecordWithUser[size];
        }
    };

    @Override
    public String toString() {
        return "RecordWithUser{" +
                "mRecord=" + mRecord +
                ", mUser=" + mUser +
                '}';
    }
}
