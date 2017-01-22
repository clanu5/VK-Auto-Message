package com.qwert2603.vkautomessage.helper;

import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.LogUtils;

public class InMemoryCacheHelper {

    private static final boolean WORK = true;

    private LruCache<Integer, User> mUsersCache = new LruCache<>(500);
    private LruCache<Integer, Record> mRecordsCache = new LruCache<>(5000);

    public InMemoryCacheHelper() {
    }

    public void putUser(User user) {
        if (WORK) {
            LogUtils.d("InMemoryCacheHelper putUser " + user);
            mUsersCache.put(user.getId(), user);
        }
    }

    @Nullable
    public User getUser(int userId) {
        if (WORK) {
            LogUtils.d("InMemoryCacheHelper getUser " + userId);
            return mUsersCache.get(userId);
        }
        return null;
    }

    public void putRecord(Record record) {
        if (WORK) {
            LogUtils.d("InMemoryCacheHelper putRecord " + record);
            mRecordsCache.put(record.getId(), record);
        }
    }

    @Nullable
    public Record getRecord(int recordId) {
        if (WORK) {
            LogUtils.d("InMemoryCacheHelper getRecord " + recordId);
            return mRecordsCache.get(recordId);
        }
        return null;
    }

    public void clear() {
        if (WORK) {
            mUsersCache.evictAll();
            mRecordsCache.evictAll();
        }
    }
}
