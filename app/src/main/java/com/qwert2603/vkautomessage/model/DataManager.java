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
                .observeOn(mIoScheduler)
                .flatMap(
                        userMyself -> mDatabaseHelper.getAllUsers()
                                .flatMap(Observable::from)
                                .doOnNext(user -> mInMemoryCacheHelper.putUser(user))
                                .filter(user -> user.getId() != userMyself.getId())
                                .toList()
                )
                .compose(applySchedulers());
    }

    public Observable<RecordListWithUser> getRecordsForUser(int userId) {
        Observable<List<Record>> recordsObservable = mDatabaseHelper.getRecordsForUser(userId)
                .doOnNext(records -> {
                    for (Record record : records) {
                        mInMemoryCacheHelper.putRecord(record);
                    }
                })
                .compose(applySchedulers());
        return Observable.zip(recordsObservable, getUserById(userId), RecordListWithUser::new)
                .compose(applySchedulers());
    }

    public Observable<User> getUserById(int userId) {
        User user = mInMemoryCacheHelper.getUser(userId);
        if (user != null) {
            return Observable.just(user);
        }
        return mDatabaseHelper.getUserById(userId)
                .doOnNext(user1 -> mInMemoryCacheHelper.putUser(user1))
                .compose(applySchedulers());
    }

    public Observable<VkUser> getVkUserById(int userId, boolean allowFromCache) {
        return mVkApiHelper.getUserById(userId, allowFromCache)
                .map(VkUser::new)
                .compose(applySchedulers());
    }

    public Observable<RecordWithUser> getRecordById(int recordId) {
        Observable<Record> recordObservable;
        Record record = mInMemoryCacheHelper.getRecord(recordId);
        if (record != null) {
            recordObservable = Observable.just(record);
            User user = mInMemoryCacheHelper.getUser(record.getUserId());
            if (user != null) {
                LogUtils.d("return Observable.just(new RecordWithUser(record, user));");
                return Observable.just(new RecordWithUser(record, user));
            }
        } else {
            recordObservable = mDatabaseHelper.getRecordById(recordId)
                    .doOnNext(record1 -> mInMemoryCacheHelper.putRecord(record1))
                    .cache()
                    .compose(applySchedulers());
        }
        Observable<User> userObservable = recordObservable
                .observeOn(mIoScheduler)
                .map(Record::getUserId)
                .flatMap(this::getUserById);
        return Observable.zip(recordObservable, userObservable, RecordWithUser::new)
                .compose(applySchedulers());
    }

    public Observable<List<Record>> getAllRecords() {
        return getAllUsers()
                .observeOn(mIoScheduler)
                .flatMap(Observable::from)
                .flatMap(user -> getRecordsForUser(user.getId()))
                .observeOn(mIoScheduler)
                .map(recordListWithUser -> recordListWithUser.mRecordList)
                .flatMap(Observable::from)
                .toList()
                .compose(applySchedulers());
    }

    public Observable<User> addUser(User user) {
        return mDatabaseHelper.insertUser(user)
                .doOnNext(user1 -> mInMemoryCacheHelper.putUser(user1))
                .compose(applySchedulers());
    }

    /**
     * Удалить пользователя и все записи для него.
     * Запланированные отправки будут отменены.
     */
    public Observable<Void> removeUser(int userId) {
        Observable<Void> deleteRecordsObservable = getRecordsForUser(userId)
                .observeOn(mIoScheduler)
                .map(recordListWithUser -> recordListWithUser.mRecordList)
                .flatMap(Observable::from)
                .doOnNext(record -> mSendMessageHelper.onRecordRemoved(record.getId()))
                .doOnCompleted(() -> mDatabaseHelper.doDeleteRecordsForUser(userId))
                .toList()
                .map(l -> (Void) null)
                .compose(applySchedulers());
        Observable<Void> deleteUserObservable = mDatabaseHelper.deleteUser(userId)
                .compose(applySchedulers());
        return Observable.zip(deleteRecordsObservable, deleteUserObservable, (v1, v2) -> (Void) null)
                .compose(applySchedulers());
    }

    private Observable<Void> updateUser(User user) {
        return mDatabaseHelper.updateUser(user)
                .doOnNext(user1 -> mInMemoryCacheHelper.putUser(user))
                .compose(applySchedulers());
    }

    public Observable<Void> addRecord(Record record) {
        putRecordToSendMessageService(record);
        return mDatabaseHelper.insertRecord(record)
                .doOnNext(aVoid -> mInMemoryCacheHelper.putRecord(record))
                .compose(applySchedulers());
    }

    public Observable<Void> removeRecord(int recordId) {
        mSendMessageHelper.onRecordRemoved(recordId);
        return mDatabaseHelper.deleteRecord(recordId)
                .compose(applySchedulers());
    }

    private Observable<Void> updateRecord(Record record) {
        putRecordToSendMessageService(record);
        return mDatabaseHelper.updateRecord(record)
                .doOnNext(aVoid -> mInMemoryCacheHelper.putRecord(record))
                .compose(applySchedulers());
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
                .cache()
                .compose(applySchedulers());
        updateUsersInDatabase(friends);
        Observable<Map<Integer, DatabaseHelper.RecordsCountInfo>> recordsCountForUsers = mDatabaseHelper.getRecordsCountForUsers();
        return Observable.zip(friends.observeOn(mIoScheduler), recordsCountForUsers.observeOn(mIoScheduler),
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
                .compose(applySchedulers());
    }

    public Observable<User> getUserMyself() {
        int myselfId = mPreferenceHelper.getMyselfId();
        Observable<User> observable;
        if (myselfId == PreferenceHelper.NO_MYSELF_ID) {
            observable = mVkApiHelper.getMyself()
                    .map(User::new)
                    .doOnNext(user -> mPreferenceHelper.setMyselfId(user.getId()))
                    .doOnNext(mDatabaseHelper::doInsertUser);
        } else {
            User user = mInMemoryCacheHelper.getUser(myselfId);
            if (user != null) {
                return Observable.just(user);
            } else {
                observable = mDatabaseHelper.getUserById(myselfId);
            }
        }
        return observable
                .doOnNext(user1 -> mInMemoryCacheHelper.putUser(user1))
                .compose(applySchedulers());
    }

    public Observable<Object> sendVkMessage(int userId, String message, Object token) {
        return mVkApiHelper.sendMessage(userId, message, token)
                .compose(applySchedulers());
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
                .observeOn(mIoScheduler)
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
                .compose(applySchedulers())
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
                .toList()
                .compose(applySchedulers());
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
                        .observeOn(mIoScheduler)
                        .flatMap(Observable::from)
                        .filter(vkUser -> dbUsers.containsKey(vkUser.getId()))
                        .filter(vkUser -> !dbUsers.get(vkUser.getId()).equalsVkData(vkUser))
                        .doOnNext(vkUser -> mDatabaseHelper.doUpdateUser(vkUser))
                        .toMap(User::getId, user -> user, HashMap::new))
                .compose(applySchedulers())
                .subscribe(
                        updatedUsers -> mRxBus.send(new RxBus.Event(RxBus.Event.EVENT_USERS_VK_DATA_UPDATED, updatedUsers)),
                        LogUtils::e
                );
    }

    private <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

}
