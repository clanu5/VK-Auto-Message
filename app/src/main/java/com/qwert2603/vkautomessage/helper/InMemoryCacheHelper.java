package com.qwert2603.vkautomessage.helper;

import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.LogUtils;

public class InMemoryCacheHelper {

    private LruCache<Integer, User> mUsersCache = new LruCache<>(300);
    private LruCache<Integer, Record> mRecordsCache = new LruCache<>(1000);

    public InMemoryCacheHelper() {
    }

    public void putUser(User user) {
        LogUtils.d("InMemoryCacheHelper putUser " + user);
        mUsersCache.put(user.getId(), user);
    }

    @Nullable
    public User getUser(int userId) {
        LogUtils.d("InMemoryCacheHelper getUser " + userId);
        return mUsersCache.get(userId);
    }

    public void putRecord(Record record) {
        LogUtils.d("InMemoryCacheHelper putRecord " + record);
        mRecordsCache.put(record.getId(), record);
    }

    @Nullable
    public Record getRecord(int recordId) {
        LogUtils.d("InMemoryCacheHelper getRecord " + recordId);
        return mRecordsCache.get(recordId);
    }

    public void clear() {
        mUsersCache.evictAll();
        mRecordsCache.evictAll();
    }
}
