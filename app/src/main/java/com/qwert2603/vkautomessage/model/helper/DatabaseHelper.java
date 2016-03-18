package com.qwert2603.vkautomessage.model.helper;

import com.qwert2603.vkautomessage.model.entity.Record;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;

public final class DatabaseHelper {

    private static DatabaseHelper sDatabaseHelper = new DatabaseHelper();

    private DatabaseHelper() {
    }

    public static DatabaseHelper getInstance() {
        return sDatabaseHelper;
    }

    public Observable<List<Record>> loadRecords() {
        // TODO: 18.03.2016
        List<Record> records = new ArrayList<>();

        VKApiUserFull userFull = new VKApiUserFull();
        userFull.first_name = "Alex";
        userFull.last_name = "Zhdanov";
        records.add(new Record(userFull, "Test message", new Date(), true));

        userFull = new VKApiUserFull();
        userFull.first_name = "Fernando";
        userFull.last_name = "Alonso";
        records.add(new Record(userFull, "Another", new Date(System.currentTimeMillis() + 19181918), false));

        userFull = new VKApiUserFull();
        userFull.first_name = "Another";
        userFull.last_name = "Man";
        records.add(new Record(userFull, "Wonderful", new Date(System.currentTimeMillis() + 20000000), true));

        return Observable.interval(3, TimeUnit.SECONDS)
                .take(1)
                .map(aLong -> records);
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
