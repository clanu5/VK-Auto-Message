package com.qwert2603.vkautomessage.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.qwert2603.vkautomessage.helper.DatabaseHelper;
import com.qwert2603.vkautomessage.helper.PhotoHelper;
import com.qwert2603.vkautomessage.helper.PreferenceHelper;
import com.qwert2603.vkautomessage.helper.SendMessageHelper;
import com.qwert2603.vkautomessage.helper.VkApiHelper;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.qwert2603.vkautomessage.util.StringUtils;
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
        mPreferenceHelper = new PreferenceHelper(context);
        mSendMessageHelper = new SendMessageHelper(context);
        // TODO: 25.03.2016 обновлять таблицу User (mDatabaseHelper). Так как аватарки и имена пользователей могли измениться.
    }

    public static void initWithContext(Context context) {
        if (sDataManager == null) {
            sDataManager = new DataManager(context.getApplicationContext());
        }
    }

    public static DataManager getInstance() {
        return sDataManager;
    }

    private DatabaseHelper mDatabaseHelper;
    private VkApiHelper mVkApiHelper;
    private PhotoHelper mPhotoHelper;
    private PreferenceHelper mPreferenceHelper;
    private SendMessageHelper mSendMessageHelper;

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
                })
                .subscribeOn(Schedulers.io())
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
        mSendMessageHelper.onRecordRemoved(recordId);
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
        putRecordToSendMessageService(record);
        return mDatabaseHelper
                .updateRecord(record)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<VKApiUserFull> getVkUserById(int userId) {
        return Observable.just(mVkUserMap.get(userId))
                .flatMap(user -> user != null ? Observable.just(user) : mVkApiHelper.getUserById(userId))
                .flatMap(user1 -> {
                    // Чтобы существовало только по 1 объекту юзера с каждым id.
                    if (!mVkUserMap.containsKey(userId)) {
                        synchronized (mVkUserMap) {
                            if (!mVkUserMap.containsKey(userId)) {
                                mVkUserMap.put(userId, user1);
                            }
                        }
                    }
                    return Observable.just(mVkUserMap.get(userId));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<VKApiUserFull> getVkUserMyself() {
        VKApiUserFull user = new VKApiUserFull();
        user.first_name = mPreferenceHelper.getUserName();
        user.photo_100 = mPreferenceHelper.getUserPhoto();
        return Observable.just(user)
                .flatMap(user1 -> "".equals(user1.photo_100) ? mVkApiHelper.getMyself() : Observable.just(user1))
                .flatMap(user2 -> {
                    if ("".equals(user.photo_100)) {
                        mPreferenceHelper.setUserName(StringUtils.getUserName(user2));
                        mPreferenceHelper.setUserPhoto(user2.photo_100);
                    }
                    return Observable.just(user2);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Object> sendVkMessage(int userId, String message, Object token) {
        return mVkApiHelper.sendMessage(userId, message, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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
                .map(record -> {
                    mSendMessageHelper.onRecordRemoved(record.getId());
                    return record;
                })
                .toList()
                .flatMap(records -> {
                    mRecordMap.clear();
                    if (mRecordList != null) {
                        mRecordList.clear();
                        mRecordList = null;
                    }
                    mVkUserMap.clear();
                    mPhotoCache.evictAll();
                    mVkApiHelper.logOut();
                    mPreferenceHelper.clear();
                    return mDatabaseHelper.deleteAllRecordsAndUsers();
                })
                .subscribe(aVoid -> {}, LogUtils::e);
    }

    private LruCache<String, Bitmap> mPhotoCache = new LruCache<>(256);

    public Observable<Bitmap> getPhotoByUrl(String url) {
        return Observable.just(mPhotoCache.get(url))
                .flatMap(bitmap -> bitmap != null ? Observable.just(bitmap) : mPhotoHelper.downloadBitmap(url))
                .flatMap(bitmap1 -> {
                    if (mPhotoCache.get(url) == null) {
                        mPhotoCache.put(url, bitmap1);
                    }
                    return Observable.just(mPhotoCache.get(url));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Настроить отправку сообщений для записи record.
     * Этот метод стоит вызывать, когда запись меняется и после включения устройства.
     */
    public void putRecordToSendMessageService(Record record) {
        mSendMessageHelper.onRecordChanged(record);
    }
}
