package com.qwert2603.vkautomessage.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class DatabaseHelper extends SQLiteOpenHelper {

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

    private static final String QUERY_COLUMN_RECORD_ID = "R_ID";
    private static final String QUERY_COLUMN_USER_ID = "U_ID";
    private static final String SELECT_RECORDS_QUERY =
            "SELECT R." + COLUMN_RECORD_ID + " AS " + QUERY_COLUMN_RECORD_ID + ", R." + COLUMN_RECORD_USER_ID
                    + ", R." + COLUMN_RECORD_MESSAGE + ", R." + COLUMN_RECORD_ENABLED + ", R." + COLUMN_RECORD_TIME
                    + ", U." + COLUMN_USER_ID + " AS " + QUERY_COLUMN_USER_ID + ", U." + COLUMN_USER_FIRST_NAME
                    + ", U." + COLUMN_USER_LAST_NAME + ", U." + COLUMN_USER_PHOTO_100
                    + " FROM " + TABLE_RECORD + " AS R , " + TABLE_USER + " AS U "
                    + " WHERE (R." + COLUMN_RECORD_USER_ID + " = U." + COLUMN_USER_ID + ") ";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_RECORD + " ("
                        + COLUMN_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_RECORD_USER_ID + " INTEGER, "
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
        return Observable.defer(() -> Observable.just(doGetAllRecords()));
    }

    public Observable<Record> getRecordById(int recordId) {
        return Observable.defer(() -> Observable.just(doGetRecordById(recordId)));
    }

    /**
     * @return список всех имеющихся в БД пользователей.
     */
    public Observable<List<VKApiUserFull>> getAllUsers() {
        return Observable.defer(() -> Observable.just(doGetAllUsers()));
    }

    /**
     * Обновить фотографию пользователя.
     *  @param userId   id пользователя.
     * @param newPhoto url новой фотографии.
     */
    public Observable<Boolean> updateUserPhoto(int userId, String newPhoto) {
        return Observable.defer(() -> Observable.just(doUpdateUserPhoto(userId, newPhoto)));
    }

    /**
     * Добавить запись record в БД, переданному объекту record будет назначен id.
     *
     * @param record запись для добавления.
     * @return id добавленной записи
     */
    public Observable<Long> insertRecord(Record record) {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                insertUserIfAbsent(record.getUser());
                long id = doInsertRecord(record);
                record.setId((int) id);
                subscriber.onNext(id);
                subscriber.onCompleted();
            }
        });
    }

    /**
     * Удалить запись по id.
     *
     * @return Observable для кол-ва удаленных записей. (не должно быть больше 1).
     */
    public Observable<Integer> deleteRecord(int recordId) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Record record = doGetRecordById(recordId);
                subscriber.onNext(doDeleteRecord(recordId));
                deleteUserIfNoRecordsForHim(record.getUser().id);
                subscriber.onCompleted();
            }
        });
    }

    /**
     * Обновить запись.
     *
     * @return Observable для кол-ва обновленных записей. (не должно быть больше 1).
     */
    public Observable<Integer> updateRecord(Record record) {
        return Observable.defer(() -> Observable.just(doUpdateRecord(record)));
    }

    public Observable<Void> deleteAllRecordsAndUsers() {
        return Observable.defer(() -> Observable.just(doDeleteAllRecordsAndUsers()));
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
            user.id = getInt(getColumnIndex(QUERY_COLUMN_USER_ID));
            user.first_name = getString(getColumnIndex(COLUMN_USER_FIRST_NAME));
            user.last_name = getString(getColumnIndex(COLUMN_USER_LAST_NAME));
            user.photo_100 = getString(getColumnIndex(COLUMN_USER_PHOTO_100));
            int recordId = getInt(getColumnIndex(QUERY_COLUMN_RECORD_ID));
            String message = getString(getColumnIndex(COLUMN_RECORD_MESSAGE));
            boolean enabled = getInt(getColumnIndex(COLUMN_RECORD_ENABLED)) > 0;
            Date time = new Date(getLong(getColumnIndex(COLUMN_RECORD_TIME)));
            return new Record(recordId, user, message, time, enabled);
        }
    }

    private static class UserCursor extends CursorWrapper {
        public UserCursor(Cursor cursor) {
            super(cursor);
        }

        public VKApiUserFull getUser() {
            if (isClosed() || isBeforeFirst() || isAfterLast()) {
                return null;
            }
            VKApiUserFull user = new VKApiUserFull();
            user.id = getInt(getColumnIndex(COLUMN_USER_ID));
            user.first_name = getString(getColumnIndex(COLUMN_USER_FIRST_NAME));
            user.last_name = getString(getColumnIndex(COLUMN_USER_LAST_NAME));
            user.photo_100 = getString(getColumnIndex(COLUMN_USER_PHOTO_100));
            return user;
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
        return getWritableDatabase().delete(TABLE_RECORD, COLUMN_RECORD_ID + " = ?", new String[]{String.valueOf(recordId)});
    }

    private int doDeleteUser(int userId) {
        return getWritableDatabase().delete(TABLE_USER, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    private int doUpdateRecord(Record record) {
        Record oldRecord = doGetRecordById(record.getId());
        insertUserIfAbsent(record.getUser());

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RECORD_ID, record.getId());
        contentValues.put(COLUMN_RECORD_USER_ID, record.getUser().id);
        contentValues.put(COLUMN_RECORD_MESSAGE, record.getMessage());
        contentValues.put(COLUMN_RECORD_ENABLED, record.isEnabled() ? 1 : 0);
        contentValues.put(COLUMN_RECORD_TIME, record.getTime().getTime());
        int result = getWritableDatabase()
                .update(TABLE_RECORD, contentValues, COLUMN_RECORD_ID + " = ?", new String[]{String.valueOf(record.getId())});
        if (oldRecord != null && oldRecord.getUser().id != record.getUser().id) {
            deleteUserIfNoRecordsForHim(oldRecord.getUser().id);
        }
        return result;
    }

    private List<Record> doGetAllRecords() {
        RecordCursor recordCursor = new RecordCursor(getReadableDatabase()
                .rawQuery(SELECT_RECORDS_QUERY, null));
        recordCursor.moveToFirst();
        List<Record> recordList = new ArrayList<>();
        while (!recordCursor.isAfterLast()) {
            recordList.add(recordCursor.getRecord());
            recordCursor.moveToNext();
        }
        recordCursor.close();
        return recordList;
    }

    private Record doGetRecordById(int recordId) {
        RecordCursor recordCursor = new RecordCursor(getReadableDatabase()
                .rawQuery(SELECT_RECORDS_QUERY
                        + " AND (" + QUERY_COLUMN_RECORD_ID + " = " + recordId + ")"
                        + " LIMIT 1", null));
        recordCursor.moveToFirst();
        Record record = null;
        if (!recordCursor.isAfterLast()) {
            record = recordCursor.getRecord();
            recordCursor.moveToNext();
        }
        recordCursor.close();
        return record;
    }

    private List<VKApiUserFull> doGetAllUsers() {
        UserCursor userCursor = new UserCursor(getReadableDatabase()
                .query(TABLE_USER, null, null, null, null, null, null));
        userCursor.moveToFirst();
        List<VKApiUserFull> userList = new ArrayList<>();
        while (!userCursor.isAfterLast()) {
            userList.add(userCursor.getUser());
            userCursor.moveToNext();
        }
        userCursor.close();
        return userList;
    }

    private boolean doUpdateUserPhoto(int userId, String newPhoto) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_PHOTO_100, newPhoto);
        int update = getWritableDatabase()
                .update(TABLE_USER, contentValues, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        return update == 1;
    }

    private Void doDeleteAllRecordsAndUsers() {
        getWritableDatabase().delete(TABLE_RECORD, null, null);
        getWritableDatabase().delete(TABLE_USER, null, null);
        return null;
    }

    /**
     * Объект на котором происходит синхронизация при добавлении и удалении зписей из {@link #TABLE_USER}.
     */
    private final Object mObject = new Object();

    /**
     * Удалить пользователя из {@link #TABLE_USER}, если в {@link #TABLE_RECORD} нет записей для этого пользователя.
     */
    private void deleteUserIfNoRecordsForHim(int userId) {
        if (getRecordCountForUser(userId) == 0 && isUserExist(userId)) {
            synchronized (mObject) {
                if (getRecordCountForUser(userId) == 0 && isUserExist(userId)) {
                    doDeleteUser(userId);
                }
            }
        }
    }

    /**
     * Добавить пользователя, если его еще нет в {@link #TABLE_USER}.
     */
    private void insertUserIfAbsent(VKApiUser user) {
        if (!isUserExist(user.id)) {
            synchronized (mObject) {
                if (!isUserExist(user.id)) {
                    doInsertUser(user);
                }
            }
        }
    }

    private int getRecordCountForUser(int userId) {
        Cursor cursor = getReadableDatabase()
                .rawQuery("SElECT COUNT(*) FROM " + TABLE_RECORD + " WHERE " + COLUMN_RECORD_USER_ID + " = " + userId, null);
        int result;
        cursor.moveToFirst();
        result = cursor.getInt(0);
        cursor.close();
        return result;
    }

    private boolean isUserExist(int userId) {
        Cursor cursor = getReadableDatabase()
                .rawQuery("SElECT COUNT(*) FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + " = " + userId, null);
        boolean result;
        cursor.moveToFirst();
        result = cursor.getInt(0) > 0;
        cursor.close();
        return result;
    }

}
