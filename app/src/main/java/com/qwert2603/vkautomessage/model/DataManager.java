package com.qwert2603.vkautomessage.model;

import android.content.Context;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.helper.DatabaseHelper;
import com.qwert2603.vkautomessage.helper.PreferenceHelper;
import com.qwert2603.vkautomessage.helper.SendMessageHelper;
import com.qwert2603.vkautomessage.helper.VkApiHelper;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

public class DataManager {

    public DataManager() {
        VkAutoMessageApplication.getAppComponent().inject(DataManager.this);
        // TODO: 25.03.2016 обновлять таблицу User (mDatabaseHelper). Так как аватарки и имена пользователей могли измениться.
    }

    @Inject
    Context mAppContext;

    @Inject
    DatabaseHelper mDatabaseHelper;

    @Inject
    VkApiHelper mVkApiHelper;

    @Inject
    PreferenceHelper mPreferenceHelper;

    @Inject
    SendMessageHelper mSendMessageHelper;

    @Inject
    @Named(Const.IO_THREAD)
    Scheduler mIoScheduler;

    @Inject
    @Named(Const.UI_THREAD)
    Scheduler mUiScheduler;

    private volatile List<Record> mRecordList;
    private final Map<Integer, Record> mRecordMap = new HashMap<>();

    @SuppressWarnings("SynchronizeOnNonFinalField")
    public Observable<List<Record>> getAllRecords() {
        return Observable.just(mRecordList)
                .flatMap(records -> records != null ? Observable.just(records) : mDatabaseHelper.getAllRecords())
                .doOnNext(records1 -> {
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
                })
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<Record> getRecordById(int recordId) {
        return Observable.just(mRecordMap.get(recordId))
                .flatMap(record -> record != null ? Observable.just(record) : mDatabaseHelper.getRecordById(recordId))
                .doOnNext(record1 -> {
                    // Чтобы существовало только по 1 объекту записи с каждым id.
                    if (!mRecordMap.containsKey(recordId)) {
                        synchronized (mRecordMap) {
                            if (!mRecordMap.containsKey(recordId)) {
                                mRecordMap.put(recordId, record1);
                            }
                        }
                    }
                })
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
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
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    public Observable<Integer> removeRecord(int recordId) {
        mSendMessageHelper.onRecordRemoved(recordId);
        return mDatabaseHelper
                .deleteRecord(recordId)
                .doOnNext(aLong -> {
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
                })
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<Integer> updateRecord(Record record) {
        putRecordToSendMessageService(record);
        return mDatabaseHelper
                .updateRecord(record)
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public void justUpdateRecord(Record record) {
        updateRecord(record).subscribe(i -> {
        }, LogUtils::e);
    }

    private final Map<Integer, VKApiUserFull> mVkUserMap = new HashMap<>();

    public Observable<List<VKApiUserFull>> getAllVkFriends() {
        return mVkApiHelper
                .getFriends()
                .flatMap(Observable::from)
                .doOnNext(user -> {
                    // Чтобы существовало только по 1 объекту юзера с каждым id.
                    if (!mVkUserMap.containsKey(user.id)) {
                        synchronized (mVkUserMap) {
                            if (!mVkUserMap.containsKey(user.id)) {
                                mVkUserMap.put(user.id, user);
                            }
                        }
                    }
                })
                .toList()
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<VKApiUserFull> getVkUserById(int userId) {
        return Observable.just(mVkUserMap.get(userId))
                .flatMap(user -> user != null ? Observable.just(user) : mVkApiHelper.getUserById(userId))
                .doOnNext(user1 -> {
                    // Чтобы существовало только по 1 объекту юзера с каждым id.
                    if (!mVkUserMap.containsKey(userId)) {
                        synchronized (mVkUserMap) {
                            if (!mVkUserMap.containsKey(userId)) {
                                mVkUserMap.put(userId, user1);
                            }
                        }
                    }
                })
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<VKApiUserFull> getVkUserMyself() {
        VKApiUserFull user = new VKApiUserFull();
        user.first_name = mPreferenceHelper.getUserFirstName();
        user.last_name = mPreferenceHelper.getUserLastName();
        user.photo_200 = mPreferenceHelper.getUserPhoto();
        return Observable.just(user)
                .flatMap(user1 -> "".equals(user1.photo_200) ? mVkApiHelper.getMyself() : Observable.just(user1))
                .doOnNext(user2 -> {
                    if ("".equals(user.photo_200)) {
                        mPreferenceHelper.setUserFirstName(user2.first_name);
                        mPreferenceHelper.setUserLastName(user2.last_name);
                        mPreferenceHelper.setUserPhoto(user2.photo_200);
                    }
                })
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<Object> sendVkMessage(int userId, String message, Object token) {
        return mVkApiHelper.sendMessage(userId, message, token)
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public int getLastNotificationId() {
        return mPreferenceHelper.getLastNotificationId();
    }

    public void setLastNotificationId(int lastNotificationId) {
        mPreferenceHelper.setLastNotificationId(lastNotificationId);
    }

    public void logOutVk() {
        getAllRecords()
                .flatMap(Observable::from)
                .doOnNext(record -> mSendMessageHelper.onRecordRemoved(record.getId()))
                .toList()
                .flatMap(records -> {
                    mRecordMap.clear();
                    if (mRecordList != null) {
                        mRecordList.clear();
                        mRecordList = null;
                    }
                    mVkUserMap.clear();
                    mVkApiHelper.logOut();
                    mPreferenceHelper.clear();
                    return mDatabaseHelper.deleteAllRecordsAndUsers();
                })
                .subscribe(aVoid -> {
                }, LogUtils::e);
    }

    /**
     * Настроить отправку сообщений для записи record.
     * Этот метод стоит вызывать, когда запись меняется и после включения устройства.
     */
    public void putRecordToSendMessageService(Record record) {
        mSendMessageHelper.onRecordChanged(record);
    }
}
