package com.qwert2603.vkautomessage.model;

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

    private volatile List<Record> mRecordList;

    public Observable<List<Record>> getAllRecords() {
        return Observable.just(mRecordList)
                .flatMap(records -> records != null ? Observable.just(records) : mDatabaseHelper.getAllRecords())
                .flatMap(records1 -> {
                    if (mRecordList == null) {
                        mRecordList = records1;
                    }
                    return Observable.just(mRecordList);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Record> getRecordById(int recordId) {
        return getAllRecords()
                .flatMap(Observable::from)
                .filter(record -> record.getId() == recordId)
                .take(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Long> addRecord(Record record) {
        return mDatabaseHelper
                .insertRecord(record)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Integer> removeRecord(int recordId) {
        return mDatabaseHelper
                .deleteRecord(recordId)
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
