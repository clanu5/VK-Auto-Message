package com.qwert2603.vkautomessage.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.qwert2603.vkautomessage.model.entity.Record;
import com.qwert2603.vkautomessage.model.helper.DatabaseHelper;
import com.qwert2603.vkautomessage.model.helper.PhotoHelper;
import com.qwert2603.vkautomessage.model.helper.VkApiHelper;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class DataManager {
    private static DataManager sDataManager;

    private DataManager(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
        mVkApiHelper = new VkApiHelper();
        mPhotoHelper = new PhotoHelper();
    }

    public static void initWithContext(Context context) {
        sDataManager = new DataManager(context.getApplicationContext());
    }

    public static DataManager getInstance() {
        return sDataManager;
    }

    private DatabaseHelper mDatabaseHelper;
    private VkApiHelper mVkApiHelper;
    private PhotoHelper mPhotoHelper;

    private volatile List<Record> mRecordList;
    private final Map<Integer, Record> mRecordMap = new HashMap<>();

    @SuppressWarnings("SynchronizeOnNonFinalField")
    public Observable<List<Record>> getAllRecords() {
        return Observable.just(mRecordList)
                .flatMap(records -> records != null ? Observable.just(records) : mDatabaseHelper.getAllRecords())
                .flatMap(records1 -> {
                    // Чтобы существовало только по 1 объекту записи с каждым id.
                    // И чтобы в mRecordList и mRecordMap были одни и те же объекты.
                    if (mRecordList == null) {
                        synchronized (mRecordMap) {
                            if (mRecordList == null) {
                                mRecordList = records1;
                                synchronized (mRecordList) {
                                    for (int i = 0, size = mRecordList.size(); i < size; i++) {
                                        int id = mRecordList.get(i).getId();
                                        if (mRecordMap.containsKey(id)) {
                                            mRecordList.set(i, mRecordMap.get(id));
                                        } else {
                                            mRecordMap.put(id, mRecordList.get(i));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return Observable.just(mRecordList);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Record> getRecordById(int recordId) {
        return Observable.just(mRecordMap.get(recordId))
                .flatMap(record -> record != null ? Observable.just(record) : mDatabaseHelper.getRecordById(recordId))
                .flatMap(record1 -> {
                    // Чтобы существовало только по 1 объекту записи с каждым id.
                    if (!mRecordMap.containsKey(recordId)) {
                        synchronized (mRecordMap) {
                            if (!mRecordMap.containsKey(recordId)) {
                                mRecordMap.put(recordId, record1);
                            }
                        }
                    }
                    return Observable.just(mRecordMap.get(recordId));
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    public Observable<Long> addRecord(Record record) {
        return mDatabaseHelper
                .insertRecord(record)
                .map(aLong -> {
                    synchronized (mRecordMap) {
                        mRecordMap.put(record.getId(), record);
                    }
                    if (mRecordList != null) {
                        synchronized (mRecordList) {
                            mRecordList.add(record);
                        }
                    }
                    return aLong;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    public Observable<Integer> removeRecord(int recordId) {
        return mDatabaseHelper
                .deleteRecord(recordId)
                .map(aLong -> {
                    Record deletingRecord = mRecordMap.get(recordId);
                    if (deletingRecord == null) {
                        LogUtils.e("removeRecord ## nothing deleted ## smth is wrong!!!");
                    }
                    synchronized (mRecordMap) {
                        mRecordMap.remove(recordId);
                    }
                    if (mRecordList != null) {
                        synchronized (mRecordList) {
                            mRecordList.remove(deletingRecord);
                        }
                    }
                    return aLong;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Integer> updateRecord(Record record) {
        LogUtils.d("DataManager ## updateRecord " + record.getUser().first_name);
        return mDatabaseHelper
                .updateRecord(record)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void justUpdateRecord(Record record) {
        updateRecord(record).subscribe(i -> {}, LogUtils::e);
    }

    private final Map<Integer, VKApiUserFull> mUserMap = new HashMap<>();

    public Observable<List<VKApiUserFull>> getAllFriends() {
        return mVkApiHelper
                .getFriends()
                .flatMap(Observable::from)
                .doOnNext(user -> {
                    synchronized (mUserMap) {
                        mUserMap.put(user.id, user);
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<VKApiUserFull> getVkUserById(int userId) {
        return Observable.just(mUserMap.get(userId))
                .flatMap(user -> user != null ? Observable.just(user) : mVkApiHelper.getUserById(userId))
                .flatMap(user1 -> {
                    // Чтобы существовало только по 1 объекту юзера с каждым id.
                    if (!mUserMap.containsKey(userId)) {
                        synchronized (mUserMap) {
                            if (!mUserMap.containsKey(userId)) {
                                mUserMap.put(userId, user1);
                            }
                        }
                    }
                    return Observable.just(mUserMap.get(userId));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void logOutVk() {
        mVkApiHelper.logOut();
    }

    public Observable<Bitmap> getPhotoByUrl(String url) {
        return mPhotoHelper
                .downloadBitmap(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
