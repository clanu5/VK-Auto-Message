package com.qwert2603.vkautomessage.model;

import android.support.annotation.NonNull;

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
    @NonNull
    private String mFirstName;
    @NonNull
    private String mLastName;
    @NonNull
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

    public User(int id, @NonNull String firstName, @NonNull String lastName, @NonNull String photo) {
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

    @NonNull
    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(@NonNull String photo) {
        mPhoto = photo;
    }

    @NonNull
    public String getLastName() {
        return mLastName;
    }

    public void setLastName(@NonNull String lastName) {
        mLastName = lastName;
    }

    @NonNull
    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(@NonNull String firstName) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (mId != user.mId) return false;
        if (mEnabledRecordsCount != user.mEnabledRecordsCount) return false;
        if (mRecordsCount != user.mRecordsCount) return false;
        if (!mFirstName.equals(user.mFirstName)) return false;
        if (!mLastName.equals(user.mLastName)) return false;
        return mPhoto.equals(user.mPhoto);
    }

}
