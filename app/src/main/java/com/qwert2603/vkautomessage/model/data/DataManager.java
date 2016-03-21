package com.qwert2603.vkautomessage.model.data;

import android.content.Context;

import com.qwert2603.vkautomessage.model.entity.Record;
import com.qwert2603.vkautomessage.model.helper.DatabaseHelper;
import com.qwert2603.vkautomessage.model.helper.VkApiHelper;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class DataManager {
    private static DataManager sDataManager;

    private DataManager(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
        mVkApiHelper = new VkApiHelper();
    }

    public static void initWithContext(Context context) {
        sDataManager = new DataManager(context.getApplicationContext());
    }

    public static DataManager getInstance() {
        return sDataManager;
    }

    private DatabaseHelper mDatabaseHelper;
    private VkApiHelper mVkApiHelper;

    public Observable<List<Record>> getAllRecords() {
        return mDatabaseHelper
                .getAllRecords()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Record> getRecordById(int recordId) {
        return mDatabaseHelper
                .getRecordById(recordId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Long> addRecord(Record record) {
        return mDatabaseHelper
                .insertRecord(record)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Integer> removeRecord(Record record) {
        return mDatabaseHelper
                .deleteRecord(record)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Integer> updateRecord(Record record) {
        return mDatabaseHelper
                .updateRecord(record)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void justUpdateRecord(Record record) {
        updateRecord(record).subscribe();
    }

    public Observable<List<VKApiUserFull>> getAllFriends() {
        return mVkApiHelper
                .getFriends()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
