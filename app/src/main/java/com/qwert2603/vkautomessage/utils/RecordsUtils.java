package com.qwert2603.vkautomessage.utils;

import com.qwert2603.vkautomessage.entities.Record;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecordsUtils {

    private static volatile ArrayList<Record> records;

    public static void loadRecords() {
        //todo
        records = new ArrayList<>();
        records.add(new Record(1918, "Test message", true, Calendar.getInstance(), 10000));
        records.add(new Record(2016, "Another", false, Calendar.getInstance(), 50000));
        records.add(new Record(6742, "Wonderful", true, Calendar.getInstance(), 42));
    }

    public static List<Record> getRecords() {
        if (records == null) {
            loadRecords();
        }
        return records;
    }

    public static void saveRecords() {
        //todo
    }

}
