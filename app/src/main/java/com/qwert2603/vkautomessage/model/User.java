package com.qwert2603.vkautomessage.model;

import com.vk.sdk.api.model.VKApiUser;

public class User {

    private int mId;
    private String mFirstName;
    private String mLastName;
    private String mPhoto;

    public User(VKApiUser vkApiUser) {
        mId = vkApiUser.id;
        mFirstName = vkApiUser.first_name;
        mLastName = vkApiUser.last_name;
        mPhoto = vkApiUser.photo_100;
    }

    public User(int id, String firstName, String lastName, String photo) {
        mId = id;
        mFirstName = firstName;
        mLastName = lastName;
        mPhoto = photo;
    }

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
}
