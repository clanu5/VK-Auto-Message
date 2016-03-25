package com.qwert2603.vkautomessage.model;

import com.vk.sdk.api.model.VKApiUser;

import java.util.Date;

public class Record {

    private int mId;
    private VKApiUser mUser;
    private String mMessage;
    private boolean mIsEnabled;
    private Date mTime;

    public Record() {
        this(-1, new VKApiUser(), "", new Date(), false);
    }

    public Record(int id, VKApiUser user, String message, Date time, boolean isEnabled) {
        mId = id;
        mUser = user;
        mMessage = message;
        mTime = time;
        mIsEnabled = isEnabled;
    }

    public VKApiUser getUser() {
        return mUser;
    }

    public void setUser(VKApiUser userName) {
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
