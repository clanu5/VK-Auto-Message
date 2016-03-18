package com.qwert2603.vkautomessage.model.helper;

import com.qwert2603.vkautomessage.model.entity.Record;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;

public final class DatabaseHelper {

    private static DatabaseHelper sDatabaseHelper = new DatabaseHelper();

    private DatabaseHelper() {
    }

    public static DatabaseHelper getInstance() {
        return sDatabaseHelper;
    }

    private Map<Integer, Record> mRecordMap = new HashMap<>();

    {
        VKApiUserFull userFull = new VKApiUserFull();
        userFull.first_name = "Alex";
        userFull.last_name = "Zhdanov";
        mRecordMap.put(0, new Record(0, userFull, "Test message", new Date(), true));

        userFull = new VKApiUserFull();
        userFull.first_name = "Fernando";
        userFull.last_name = "Alonso";
        mRecordMap.put(1, new Record(1, userFull, "Another", new Date(System.currentTimeMillis() + 19181918), false));

        userFull = new VKApiUserFull();
        userFull.first_name = "Another";
        userFull.last_name = "Man";
        mRecordMap.put(2, new Record(2, userFull, "Wonderful", new Date(System.currentTimeMillis() + 20000000), true));
    }

    public Observable<List<Record>> loadRecords() {
        // TODO: 18.03.2016
        return Observable.interval(3, TimeUnit.SECONDS)
                .take(1)
                .map(aLong -> new ArrayList<>(mRecordMap.values()));
    }

    public Observable<Record> getRecordById(int recordId) {
        // TODO: 18.03.2016
        return Observable.interval(300, TimeUnit.MILLISECONDS)
                .take(1)
                .map(aLong -> mRecordMap.get(recordId));
    }

    public void addRecord(Record record) {
        // TODO: 18.03.2016
    }

    public void removeRecord(int recordId) {
        // TODO: 18.03.2016
    }

    public void updateRecord(Record record) {
        // TODO: 18.03.2016
    }

}
