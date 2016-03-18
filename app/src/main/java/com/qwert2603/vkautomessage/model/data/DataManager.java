package com.qwert2603.vkautomessage.model.data;

import com.qwert2603.vkautomessage.model.entity.Record;
import com.qwert2603.vkautomessage.model.helper.DatabaseHelper;
import com.qwert2603.vkautomessage.model.helper.VkApiHelper;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class DataManager {
    private static DataManager sDataManager = new DataManager();

    private DataManager() {
    }

    public static DataManager getInstance() {
        return sDataManager;
    }

    public Observable<List<Record>> getAllRecords() {
        return DatabaseHelper.getInstance()
                .loadRecords()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<VKApiUserFull>> getAllFriends() {
        return VkApiHelper.getInstance()
                .getFriends()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
