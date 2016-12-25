package com.qwert2603.vkautomessage.user_details;

import android.widget.ImageView;

import com.qwert2603.vkautomessage.base.BaseView;

public interface UserView extends BaseView {
    void showName(String name);
    ImageView getPhotoImageView();
    void hideRecordsCount();
    void showRecordsCount(int recordsCount, int enabledRecordsCount);
}
