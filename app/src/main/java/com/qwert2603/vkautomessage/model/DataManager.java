package com.qwert2603.vkautomessage.model;

import android.content.Context;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.helper.DatabaseHelper;
import com.qwert2603.vkautomessage.helper.InMemoryCacheHelper;
import com.qwert2603.vkautomessage.helper.PreferenceHelper;
import com.qwert2603.vkautomessage.helper.SendMessageHelper;
import com.qwert2603.vkautomessage.helper.VkApiHelper;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;

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
    InMemoryCacheHelper mInMemoryCacheHelper;

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
        // убираем из списка пользователей пользователя приложения.
        return getUserMyself()
                .flatMap(
                        userMyself -> mDatabaseHelper.getAllUsers()
                                .flatMap(Observable::from)
                                .doOnNext(user -> mInMemoryCacheHelper.putUser(user))
                                .filter(user -> user.getId() != userMyself.getId())
                                .toList()
                )
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<RecordListWithUser> getRecordsForUser(int userId) {
        Observable<List<Record>> recordsObservable = mDatabaseHelper.getRecordsForUser(userId)
                .doOnNext(records -> {
                    for (Record record : records) {
                        mInMemoryCacheHelper.putRecord(record);
                    }
                })
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
        return Observable.zip(recordsObservable, getUserById(userId), RecordListWithUser::new)
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<User> getUserById(int userId) {
        User user = mInMemoryCacheHelper.getUser(userId);
        if (user != null) {
            return Observable.just(user);
        }
        return mDatabaseHelper.getUserById(userId)
                .doOnNext(user1 -> mInMemoryCacheHelper.putUser(user1))
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<VkUser> getVkUserById(int userId, boolean allowFromCache) {
        return mVkApiHelper.getUserById(userId, allowFromCache)
                .map(VkUser::new)
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<RecordWithUser> getRecordById(int recordId) {
        Observable<Record> recordObservable;
        Record record = mInMemoryCacheHelper.getRecord(recordId);
        if (record != null) {
            recordObservable = Observable.just(record);
        } else {
            recordObservable = mDatabaseHelper.getRecordById(recordId)
                    .doOnNext(record1 -> mInMemoryCacheHelper.putRecord(record1))
                    .subscribeOn(mIoScheduler)
                    .observeOn(mUiScheduler)
                    .cache();
        }
        Observable<User> userObservable = recordObservable
                .map(Record::getUserId)
                .flatMap(this::getUserById);
        return Observable.zip(recordObservable, userObservable, RecordWithUser::new);
    }

    public Observable<List<Record>> getAllRecords() {
        return getAllUsers()
                .flatMap(Observable::from)
                .flatMap(user -> getRecordsForUser(user.getId()))
                .map(recordListWithUser -> recordListWithUser.mRecordList)
                .flatMap(Observable::from)
                .toList();
    }

    public Observable<User> addUser(User user) {
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
                .doOnNext(record -> mDatabaseHelper.doDeleteRecord(record.getId()))
                .toList()
                .map(l -> null);
        Observable<Void> deleteUserObservable = mDatabaseHelper.deleteUser(userId)
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
        return Observable.zip(deleteRecordsObservable, deleteUserObservable, (v1, v2) -> null);
    }

    private Observable<Void> updateUser(User user) {
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

    private Observable<Void> updateRecord(Record record) {
        putRecordToSendMessageService(record);
        return mDatabaseHelper.updateRecord(record)
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    private Map<Integer, Subscription> mUpdateRecordSubscriptionMap = new HashMap<>();

    public void onRecordUpdated(Record record) {
        Subscription removed = mUpdateRecordSubscriptionMap.remove(record.getId());
        if (removed != null) {
            removed.unsubscribe();
        }
        Subscription subscription = updateRecord(record)
                .subscribe(
                        i -> {
                        },
                        LogUtils::e,
                        () -> mUpdateRecordSubscriptionMap.remove(record.getId())
                );
        mUpdateRecordSubscriptionMap.put(record.getId(), subscription);
    }

    private Map<Integer, Subscription> mUpdateUserSubscriptionMap = new HashMap<>();

    public void onUserUpdated(User user) {
        Subscription removed = mUpdateUserSubscriptionMap.remove(user.getId());
        if (removed != null) {
            removed.unsubscribe();
        }
        Subscription subscription = updateUser(user)
                .subscribe(
                        i -> {
                        },
                        LogUtils::e,
                        () -> mUpdateUserSubscriptionMap.remove(user.getId())
                );
        mUpdateUserSubscriptionMap.put(user.getId(), subscription);
    }

    public Observable<List<VkUser>> getAllVkFriends() {
        Observable<List<VkUser>> friends = mVkApiHelper.getFriends()
                .flatMap(Observable::from)
                .map(VkUser::new)
                .toList()
                .cache();
        updateUsersInDatabase(friends);
        Observable<Map<Integer, DatabaseHelper.RecordsCountInfo>> recordsCountForUsers = mDatabaseHelper.getRecordsCountForUsers();
        return Observable.zip(friends, recordsCountForUsers,
                (users, recordsCountMap) -> {
                    for (VkUser user : users) {
                        if (recordsCountMap.containsKey(user.getId())) {
                            DatabaseHelper.RecordsCountInfo recordsCountInfo = recordsCountMap.get(user.getId());
                            user.setEnabledRecordsCount(recordsCountInfo.mEnabledRecordsCount);
                            user.setRecordsCount(recordsCountInfo.mRecordsCount);
                        }
                    }
                    return users;
                })
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public Observable<User> getUserMyself() {
        int myselfId = mPreferenceHelper.getMyselfId();
        Observable<User> observable;
        if (myselfId == PreferenceHelper.NO_MYSELF_ID) {
            // для myself загружается photo_200.
            /* // TODO: 23.12.2016 не загружается. try this:
            .map(vkApiUserFull -> {
                        vkApiUserFull.photo_100 = vkApiUserFull.photo_200;
                        return vkApiUserFull;
                    })
                    */
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
        mVkApiHelper.logOut();
        getAllRecords()
                .flatMap(Observable::from)
                .doOnNext(record -> {
                    if (record.isEnabled()) {
                        mSendMessageHelper.onRecordRemoved(record.getId());
                    }
                })
                .doOnCompleted(() -> {
                    mPreferenceHelper.clear();
                    mDatabaseHelper.doDeleteAllRecordsAndUsers();
                    mInMemoryCacheHelper.clear();
                })
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler)
                .subscribe(aVoid -> {
                }, LogUtils::e);
    }

    /**
     * Настроить отправку сообщений для записи record.
     * Этот метод стоит вызывать, когда запись создается/меняется и после включения устройства.
     * А также после каждой отправки сообщения, чтобы назначить следующую отправку.
     */
    public void putRecordToSendMessageService(Record record) {
        mSendMessageHelper.onRecordChanged(record);
    }

    private void updateUsersInDatabase() {
        Observable<List<User>> observable = mDatabaseHelper.getAllUsers()
                .flatMap(Observable::from)
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
    private <U extends User> void updateUsersInDatabase(Observable<List<U>> userObservable) {
        mDatabaseHelper.getAllUsers()
                .flatMap(Observable::from)
                .toMap(User::getId, user -> user, HashMap::new)
                .flatMap(dbUsers -> userObservable
                        .flatMap(Observable::from)
                        .filter(vkUser -> dbUsers.containsKey(vkUser.getId()))
                        .filter(vkUser -> !dbUsers.get(vkUser.getId()).equalsVkData(vkUser))
                        .doOnNext(vkUser -> mDatabaseHelper.doUpdateUser(vkUser))
                        .toMap(User::getId, user -> user, HashMap::new))
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler)
                .subscribe(
                        updatedUsers -> mRxBus.send(new RxBus.Event(RxBus.Event.EVENT_USERS_VK_DATA_UPDATED, updatedUsers)),
                        LogUtils::e
                );
    }

}
