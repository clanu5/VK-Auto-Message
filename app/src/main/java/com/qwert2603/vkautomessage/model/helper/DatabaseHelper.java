package com.qwert2603.vkautomessage.model.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qwert2603.vkautomessage.model.entity.Record;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.vk.sdk.api.model.VKApiUser;

import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public final class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "records.sqlite";
    private static final int VERSION = 1;

    private static final String TABLE_RECORD = "record";
    private static final String COLUMN_RECORD_ID = "_id";
    private static final String COLUMN_RECORD_USER_ID = "user_id";
    private static final String COLUMN_RECORD_MESSAGE = "message";
    private static final String COLUMN_RECORD_ENABLED = "enabled";
    private static final String COLUMN_RECORD_TIME = "time";

    private static final String TABLE_USER = "user";
    private static final String COLUMN_USER_ID = "_id";
    private static final String COLUMN_USER_FIRST_NAME = "first_name";
    private static final String COLUMN_USER_LAST_NAME = "last_name";
    private static final String COLUMN_USER_PHOTO_100 = "photo_100";

    private static final String SELECT_RECORDS_QUERY =
            "SELECT R." + COLUMN_RECORD_ID + ", R." + COLUMN_RECORD_USER_ID
                    + ", R." + COLUMN_RECORD_MESSAGE + ", R." + COLUMN_RECORD_ENABLED + ", R." + COLUMN_RECORD_TIME
                    + ", U." + COLUMN_USER_ID + ", U." + COLUMN_USER_FIRST_NAME
                    + ", U." + COLUMN_USER_LAST_NAME + ", U." + COLUMN_USER_PHOTO_100
                    + " FROM " + TABLE_RECORD + " AS R , " + TABLE_USER + " AS U "
                    + " WHERE R." + COLUMN_RECORD_USER_ID + " = U." + COLUMN_USER_ID + " ";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_RECORD + " ("
                        + COLUMN_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_RECORD_USER_ID + " INTEGER REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ")" + ", "
                        + COLUMN_RECORD_MESSAGE + " TEXT, "
                        + COLUMN_RECORD_ENABLED + " INTEGER, "
                        + COLUMN_RECORD_TIME + " INTEGER" + ")"
        );
        db.execSQL(
                "CREATE TABLE " + TABLE_USER + " ("
                        + COLUMN_USER_ID + " INTEGER PRIMARY KEY, "
                        + COLUMN_USER_FIRST_NAME + " TEXT, "
                        + COLUMN_USER_LAST_NAME + " TEXT, "
                        + COLUMN_USER_PHOTO_100 + " TEXT" + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Observable<List<Record>> getAllRecords() {
        return Observable
                .create((Subscriber<? super Record> subscriber) -> {
                    RecordCursor recordCursor = new RecordCursor(getReadableDatabase()
                            .rawQuery(SELECT_RECORDS_QUERY, null));
                    recordCursor.moveToFirst();
                    while (! recordCursor.isAfterLast()) {
                        subscriber.onNext(recordCursor.getRecord());
                        recordCursor.moveToNext();
                    }
                    recordCursor.close();
                    subscriber.onCompleted();
                })
                .toList();
    }

    public Observable<Record> getRecordById(int recordId) {
        return Observable
                .create((Subscriber<? super Record> subscriber) -> {
                    RecordCursor recordCursor = new RecordCursor(getReadableDatabase()
                            .rawQuery(SELECT_RECORDS_QUERY + " AND R." + COLUMN_RECORD_ID + " = " + recordId, null));
                    recordCursor.moveToFirst();
                    for (int i = 0; i < recordCursor.getColumnCount(); i++) {
                        LogUtils.d(recordCursor.getColumnName(i));
                    }
                    if (!recordCursor.isAfterLast()) {
                        subscriber.onNext(recordCursor.getRecord());
                    }
                    recordCursor.close();
                    subscriber.onCompleted();
                });
    }

    public Observable<Long> insertRecord(Record record) {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                if (getRecordCountForUser(record.getUser().id) == 0) {
                    doInsertUser(record.getUser());
                }
                long id = doInsertRecord(record);
                record.setId((int) id);
                subscriber.onNext(id);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Integer> deleteRecord(Record record) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(doDeleteRecord(record.getId()));
                if (getRecordCountForUser(record.getUser().id) == 0) {
                    doDeleteUser(record.getUser().id);
                }
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Integer> updateRecord(Record record) {
        return Observable.just(doUpdateRecord(record));
    }

    private static class RecordCursor extends CursorWrapper {
        public RecordCursor(Cursor cursor) {
            super(cursor);
        }

        public Record getRecord() {
            if (isClosed() || isBeforeFirst() || isAfterLast()) {
                return null;
            }
            VKApiUser user = new VKApiUser();
            user.id = getInt(getColumnIndex(COLUMN_RECORD_USER_ID));
            user.first_name = getString(getColumnIndex(COLUMN_USER_FIRST_NAME));
            user.last_name = getString(getColumnIndex(COLUMN_USER_LAST_NAME));
            user.photo_100 = getString(getColumnIndex(COLUMN_USER_PHOTO_100));
            int recordId = getInt(getColumnIndex(COLUMN_RECORD_ID));
            String message = getString(getColumnIndex(COLUMN_RECORD_MESSAGE));
            boolean enabled = getInt(getColumnIndex(COLUMN_RECORD_ENABLED)) > 0;
            Date time = new Date(getLong(getColumnIndex(COLUMN_RECORD_TIME)));
            return new Record(recordId, user, message, time, enabled);
        }
    }

    private long doInsertRecord(Record record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RECORD_USER_ID, record.getUser().id);
        contentValues.put(COLUMN_RECORD_MESSAGE, record.getMessage());
        contentValues.put(COLUMN_RECORD_ENABLED, record.isEnabled() ? 1 : 0);
        contentValues.put(COLUMN_RECORD_TIME, record.getTime().getTime());
        return getWritableDatabase().insert(TABLE_RECORD, null, contentValues);
    }

    private long doInsertUser(VKApiUser user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_ID, user.id);
        contentValues.put(COLUMN_USER_FIRST_NAME, user.first_name);
        contentValues.put(COLUMN_USER_LAST_NAME, user.last_name);
        contentValues.put(COLUMN_USER_PHOTO_100, user.photo_100);
        return getWritableDatabase().insert(TABLE_USER, null, contentValues);
    }

    private int doDeleteRecord(int recordId) {
        return getWritableDatabase().delete(TABLE_RECORD, COLUMN_RECORD_ID, new String[]{String.valueOf(recordId)});
    }

    private int doDeleteUser(int userId) {
        return getWritableDatabase().delete(TABLE_USER, COLUMN_USER_ID, new String[]{String.valueOf(userId)});
    }

    private int doUpdateRecord(Record record) {
        LogUtils.d("doUpdateRecord " + record.isEnabled());
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RECORD_USER_ID, record.getUser().id);
        contentValues.put(COLUMN_RECORD_MESSAGE, record.getMessage());
        contentValues.put(COLUMN_RECORD_ENABLED, record.isEnabled() ? 1 : 0);
        contentValues.put(COLUMN_RECORD_TIME, record.getTime().getTime());
        return getWritableDatabase().update(TABLE_RECORD, contentValues, COLUMN_RECORD_ID + " = ? ", new String[]{String.valueOf(record.getId())});
    }

    private int getRecordCountForUser(int userId) {
        Cursor cursor = getReadableDatabase()
                .rawQuery("SElECT COUNT(*) FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + " = " + userId, null);
        int result;
        cursor.moveToFirst();
        result = cursor.getInt(0);
        cursor.close();
        return result;
    }

}
