package com.qwert2603.vkautomessage.model;

import android.content.Context;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.helper.DatabaseHelper;
import com.qwert2603.vkautomessage.helper.PreferenceHelper;
import com.qwert2603.vkautomessage.helper.SendMessageHelper;
import com.qwert2603.vkautomessage.helper.VkApiHelper;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

public class DataManager {

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

    @Inject
    RxBus mRxBus;

    public DataManager() {
        VkAutoMessageApplication.getAppComponent().inject(DataManager.this);
        updateUsersInDatabase();
    }

    /**
     * @return список пользователей, которые есть в БД.
     */
    public Observable<List<User>> getAllUsers() {
        return mDatabaseHelper.getAllUsers()
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<RecordListWithUser> getRecordsForUser(int userId) {
        Observable<List<Record>> records = mDatabaseHelper.getRecordsForUser(userId)
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
        return Observable.zip(records, getUserById(userId), RecordListWithUser::new);
    }

    /**
     * @return пользователь с нужным id, если его нет в БД, он будет загружен с vk.com.
     */
    public Observable<User> getUserById(int userId) {
        return mDatabaseHelper.getUserById(userId)
                .flatMap(user -> user != null ? Observable.just(user) : mVkApiHelper.getUserById(userId).map(User::new))
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<RecordWithUser> getRecordById(int recordId) {
        Observable<Record> record = mDatabaseHelper.getRecordById(recordId)
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler).cache();
        Observable<User> user = record
                .map(Record::getUserId)
                .flatMap(this::getUserById);
        return Observable.zip(record, user, RecordWithUser::new);
    }

    public Observable<List<Record>> getAllRecords() {
        return getAllUsers()
                .flatMap(Observable::from)
                .flatMap(user -> getRecordsForUser(user.getId()))
                .map(recordListWithUser -> recordListWithUser.mRecordList)
                .flatMap(Observable::from)
                .toList();
    }

    public Observable<Void> addUser(User user) {
        return mDatabaseHelper.insertUser(user)
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    /**
     * Удалить пользователя и все записи для него.
     * Запланированные отправки будут отменены.
     */
    public Observable<Void> removeUser(int userId) {
        Observable<Void> deleteRecordsObservable = getRecordsForUser(userId)
                .map(recordListWithUser -> recordListWithUser.mRecordList)
                .flatMap(Observable::from)
                .doOnNext(record -> mSendMessageHelper.onRecordRemoved(record.getId()))
                .doOnNext(record -> mDatabaseHelper.deleteRecord(record.getId()))
                .toList()
                .map(l -> null);
        Observable<Void> deleteUserObservable = mDatabaseHelper.deleteUser(userId)
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
        return Observable.concat(deleteRecordsObservable, deleteUserObservable);
    }

    public Observable<Void> updateUser(User user) {
        return mDatabaseHelper.updateUser(user)
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<Void> addRecord(Record record) {
        putRecordToSendMessageService(record);
        return mDatabaseHelper.insertRecord(record)
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<Void> removeRecord(int recordId) {
        mSendMessageHelper.onRecordRemoved(recordId);
        return mDatabaseHelper.deleteRecord(recordId)
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<Void> updateRecord(Record record) {
        putRecordToSendMessageService(record);
        return mDatabaseHelper.updateRecord(record)
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public void onRecordUpdated(Record record) {
        updateRecord(record).subscribe(
                i -> {
                },
                LogUtils::e
        );
    }

    public Observable<List<VKApiUserFull>> getAllVkFriends() {
        Observable<List<VKApiUserFull>> friends = mVkApiHelper.getFriends().cache();
        updateUsersInDatabase(
                friends.flatMap(Observable::from)
                        .map(User::new)
                        .toList()
        );
        return friends
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<User> getUserMyself() {
        int myselfId = mPreferenceHelper.getMyselfId();
        LogUtils.d("myselfId == " + myselfId);
        Observable<User> observable;
        if (myselfId == PreferenceHelper.NO_MYSELF_ID) {
            // для myself загружается photo_200.
            observable = mVkApiHelper.getMyself()
                    .doOnNext(vkApiUserFull -> vkApiUserFull.photo_100 = vkApiUserFull.photo_200)
                    .map(User::new)
                    .doOnNext(user -> mPreferenceHelper.setMyselfId(user.getId()))
                    .doOnNext(mDatabaseHelper::doInsertUser);
        } else {
            observable = mDatabaseHelper.getUserById(myselfId);
        }
        return observable
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
                .doOnCompleted(() -> {
                    mVkApiHelper.logOut();
                    mPreferenceHelper.clear();
                    mDatabaseHelper.doDeleteAllRecordsAndUsers();
                })
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler)
                .subscribe(aVoid -> {
                }, LogUtils::e);
    }

    /**
     * Настроить отправку сообщений для записи record.
     * Этот метод стоит вызывать, когда запись меняется и после включения устройства.
     */
    public void putRecordToSendMessageService(Record record) {
        // TODO: 01.05.2016 добавить вызов этого метода везде куда надо.
        // и SendMessageHelper#onRecordRemoved
        mSendMessageHelper.onRecordChanged(record);
    }

    private void updateUsersInDatabase() {
        Observable<List<User>> observable = mDatabaseHelper.getAllUsers()
                .flatMap(Observable::from)
                .doOnNext(user -> LogUtils.d("updateUsersInDatabase" + user.toString()))
                .map(User::getId)
                .toList()
                .flatMap(mVkApiHelper::getUsersById)
                .flatMap(Observable::from)
                .map(User::new)
                .toList();
        updateUsersInDatabase(observable);
    }

    /**
     * Обновить пользователей, сохраненных в {@link #mDatabaseHelper}.
     *
     * @param userObservable пользователи, которые будут обновлены.
     */
    private void updateUsersInDatabase(Observable<List<User>> userObservable) {
        userObservable
                .flatMap(Observable::from)
                .flatMap(user -> mDatabaseHelper.updateUser(user))
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler)
                .subscribe(
                        b -> {
                        },
                        LogUtils::e,
                        () -> mRxBus.send(RxBus.EVENT_USERS_PHOTO_UPDATED)
                );
    }
}
