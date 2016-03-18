package com.qwert2603.vkautomessage.model.entity;

import com.vk.sdk.api.model.VKApiUserFull;

import java.util.Date;

public class Record {

    private int mId;
    private VKApiUserFull mUser;
    private String mMessage;
    private boolean mIsEnabled;
    private Date mTime;

    public Record(int id) {
        this(id, new VKApiUserFull(), "", new Date(), false);
    }

    public Record(int id, VKApiUserFull userName, String message, Date time, boolean isEnabled) {
        mId = id;
        mUser = userName;
        mMessage = message;
        mTime = time;
        mIsEnabled = isEnabled;
    }

    public VKApiUserFull getUser() {
        return mUser;
    }

    public void setUser(VKApiUserFull userName) {
        mUser = userName;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public Date getTime() {
        return mTime;
    }

    public void setTime(Date time) {
        mTime = time;
    }

    public boolean isEnabled() {
        return mIsEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        mIsEnabled = isEnabled;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}
