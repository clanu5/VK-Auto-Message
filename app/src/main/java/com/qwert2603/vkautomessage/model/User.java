package com.qwert2603.vkautomessage.model;

import com.qwert2603.vkautomessage.util.StringUtils;
import com.vk.sdk.api.model.VKApiUser;

import java.util.Objects;

public class User implements Identifiable {

    public static final int NO_INFO = -1;

    /**
     * @return пользователь с пустыми полями.
     */
    public static User createEmptyUser() {
        return new User(NO_INFO, "", "", "");
    }

    private int mId;
    private String mFirstName;
    private String mLastName;
    private String mPhoto;
    private int mEnabledRecordsCount;
    private int mRecordsCount;

    public User(VKApiUser vkApiUser) {
        mId = vkApiUser.id;
        mFirstName = vkApiUser.first_name;
        mLastName = vkApiUser.last_name;
        mPhoto = vkApiUser.photo_100;
        mEnabledRecordsCount = NO_INFO;
        mRecordsCount = NO_INFO;
    }

    public User(int id, String firstName, String lastName, String photo) {
        mId = id;
        mFirstName = firstName;
        mLastName = lastName;
        mPhoto = photo;
        mEnabledRecordsCount = NO_INFO;
        mRecordsCount = NO_INFO;
    }

    @Override
    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public int getEnabledRecordsCount() {
        return mEnabledRecordsCount;
    }

    public void setEnabledRecordsCount(int enabledRecordsCount) {
        mEnabledRecordsCount = enabledRecordsCount;
    }

    public int getRecordsCount() {
        return mRecordsCount;
    }

    public void setRecordsCount(int recordsCount) {
        mRecordsCount = recordsCount;
    }

    @Override
    public String toString() {
        return mId + " " + StringUtils.getUserName(User.this) + " " + mPhoto;
    }

    public boolean equalsVkData(User user) {
        return user.mId == mId
                && Objects.equals(user.mFirstName, mFirstName)
                && Objects.equals(user.mLastName, mLastName)
                && Objects.equals(user.mPhoto, mPhoto);
    }

    public void setVkDataFrom(User user) {
        mId = user.mId;
        mFirstName = user.mFirstName;
        mLastName = user.mLastName;
        mPhoto = user.mPhoto;
    }
}
