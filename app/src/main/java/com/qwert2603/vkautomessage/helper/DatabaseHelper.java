package com.qwert2603.vkautomessage.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "records.sqlite";
    private static final int VERSION = 3;

    private static final String TABLE_RECORD = "record";
    private static final String COLUMN_RECORD_ID = "_id";
    private static final String COLUMN_RECORD_USER_ID = "user_id";
    private static final String COLUMN_RECORD_MESSAGE = "message";
    private static final String COLUMN_RECORD_ENABLED = "enabled";
    private static final String COLUMN_RECORD_REPEAT_TYPE = "repeat_type";
    private static final String COLUMN_RECORD_REPEAT_INFO = "repeat_info";
    private static final String COLUMN_RECORD_HOUR = "hour";
    private static final String COLUMN_RECORD_MINUTE = "minute";

    private static final String TABLE_USER = "user";
    private static final String COLUMN_USER_ID = "_id";
    private static final String COLUMN_USER_FIRST_NAME = "first_name";
    private static final String COLUMN_USER_LAST_NAME = "last_name";
    private static final String COLUMN_USER_PHOTO = "photo";
    private static final String COLUMN_USER_ADDING_TIME = "adding_time";    // время добавление в БД

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
                        + COLUMN_RECORD_REPEAT_TYPE + " INTEGER, "
                        + COLUMN_RECORD_REPEAT_INFO + " INTEGER, "
                        + COLUMN_RECORD_HOUR + " INTEGER, "
                        + COLUMN_RECORD_MINUTE + " INTEGER)"
        );
        db.execSQL(
                "CREATE TABLE " + TABLE_USER + " ("
                        + COLUMN_USER_ID + " INTEGER PRIMARY KEY, "
                        + COLUMN_USER_FIRST_NAME + " TEXT, "
                        + COLUMN_USER_LAST_NAME + " TEXT, "
                        + COLUMN_USER_PHOTO + " TEXT, "
                        + COLUMN_USER_ADDING_TIME + " INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_RECORD);
        db.execSQL("DROP TABLE " + TABLE_USER);
        onCreate(db);
    }

    public Observable<List<User>> getAllUsers() {
        return Observable.defer(() -> Observable.just(doGetAllUsers()));
    }

    public Observable<Map<Integer, Integer>> getRecordsCountForUsers() {
        // TODO: 06.05.2016 передавать инфо о кол-ве активных записей и общем кол-ве записей.
        return Observable.defer(() -> Observable.just(doGetRecordsCountForUsers()));
    }

    public Observable<List<Record>> getRecordsForUser(int userId) {
        return Observable.defer(() -> Observable.just(doGetRecordsForUser(userId)));
    }

    public Observable<User> getUserById(int userId) {
        return Observable.defer(() -> Observable.just(doGetUserById(userId)));
    }

    public Observable<Record> getRecordById(int recordId) {
        return Observable.defer(() -> Observable.just(doGetRecordById(recordId)));
    }

    public Observable<User> insertUser(User user) {
        return Observable.defer(() -> Observable.just(doInsertUser(user)));
    }

    public Observable<Void> deleteUser(int userId) {
        return Observable.defer(() -> Observable.just(doDeleteUser(userId)));
    }

    public Observable<Void> updateUser(User user) {
        return Observable.defer(() -> Observable.just(doUpdateUser(user)));
    }

    public Observable<Void> insertRecord(Record record) {
        return Observable.defer(() -> Observable.just(doInsertRecord(record)));
    }

    public Observable<Void> deleteRecord(int recordId) {
        return Observable.defer(() -> Observable.just(doDeleteRecord(recordId)));
    }

    public Observable<Void> updateRecord(Record record) {
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
            int id = getInt(getColumnIndex(COLUMN_RECORD_ID));
            int userId = getInt(getColumnIndex(COLUMN_RECORD_USER_ID));
            String message = getString(getColumnIndex(COLUMN_RECORD_MESSAGE));
            boolean enabled = getInt(getColumnIndex(COLUMN_RECORD_ENABLED)) > 0;
            int repeatType = getInt(getColumnIndex(COLUMN_RECORD_REPEAT_TYPE));
            int repeatInfo = getInt(getColumnIndex(COLUMN_RECORD_REPEAT_INFO));
            int hour = getInt(getColumnIndex(COLUMN_RECORD_HOUR));
            int minute = getInt(getColumnIndex(COLUMN_RECORD_MINUTE));
            return new Record(id, userId, message, enabled, repeatType, repeatInfo, hour, minute);
        }
    }

    private static class UserCursor extends CursorWrapper {
        public UserCursor(Cursor cursor) {
            super(cursor);
        }

        public User getUser() {
            if (isClosed() || isBeforeFirst() || isAfterLast()) {
                return null;
            }
            int id = getInt(getColumnIndex(COLUMN_USER_ID));
            String firstName = getString(getColumnIndex(COLUMN_USER_FIRST_NAME));
            String lastName = getString(getColumnIndex(COLUMN_USER_LAST_NAME));
            String photo = getString(getColumnIndex(COLUMN_USER_PHOTO));
            return new User(id, firstName, lastName, photo);
        }
    }

    private List<User> doGetAllUsers() {
        UserCursor userCursor = new UserCursor(getReadableDatabase()
                .query(TABLE_USER, null, null, null, null, null, COLUMN_USER_ADDING_TIME));
        userCursor.moveToFirst();
        List<User> userList = new ArrayList<>();
        while (!userCursor.isAfterLast()) {
            userList.add(userCursor.getUser());
            userCursor.moveToNext();
        }
        userCursor.close();
        Map<Integer, Integer> recordsCountForUsers = doGetRecordsCountForUsers();
        for (User user : userList) {
            if (recordsCountForUsers.containsKey(user.getId())) {
                user.setRecordsCount(recordsCountForUsers.get(user.getId()));
            }
        }
        return userList;
    }

    private Map<Integer, Integer> doGetRecordsCountForUsers() {
        Map<Integer, Integer> map = new HashMap<>();
        String query = "SELECT " + TABLE_USER + "." + COLUMN_USER_ID + ", COUNT(" + TABLE_RECORD + "." + COLUMN_RECORD_ID + ")" +
                " FROM " + TABLE_USER +
                " LEFT JOIN " + TABLE_RECORD +
                " ON " + TABLE_USER + "." + COLUMN_USER_ID + " = " + TABLE_RECORD + "." + COLUMN_RECORD_USER_ID +
                " GROUP BY " + TABLE_USER + "." + COLUMN_USER_ID;
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            map.put(cursor.getInt(0), cursor.getInt(1));
            cursor.moveToNext();
        }
        cursor.close();
        return map;
    }

    private List<Record> doGetRecordsForUser(int userId) {
        RecordCursor recordCursor = new RecordCursor(getReadableDatabase().query(
                TABLE_RECORD,
                null,
                COLUMN_RECORD_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null
        ));
        recordCursor.moveToFirst();
        List<Record> recordList = new ArrayList<>();
        while (!recordCursor.isAfterLast()) {
            recordList.add(recordCursor.getRecord());
            recordCursor.moveToNext();
        }
        recordCursor.close();
        return recordList;
    }

    private User doGetUserById(int userId) {
        UserCursor userCursor = new UserCursor(getReadableDatabase().query(
                TABLE_USER,
                null,
                COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null,
                "1"
        ));
        userCursor.moveToFirst();
        User user = null;
        if (!userCursor.isAfterLast()) {
            user = userCursor.getUser();
            user.setRecordsCount(getRecordsCountForUser(userId));
        }
        userCursor.close();
        return user;
    }

    private Record doGetRecordById(int recordId) {
        RecordCursor recordCursor = new RecordCursor(getReadableDatabase().query(
                TABLE_RECORD,
                null,
                COLUMN_RECORD_ID + " = ?",
                new String[]{String.valueOf(recordId)},
                null,
                null,
                null,
                "1"
        ));
        recordCursor.moveToFirst();
        Record record = null;
        if (!recordCursor.isAfterLast()) {
            record = recordCursor.getRecord();
        }
        recordCursor.close();
        return record;
    }

    public User doInsertUser(User user) {
        ContentValues contentValues = getContentValuesForUser(user);
        contentValues.put(COLUMN_USER_ADDING_TIME, System.currentTimeMillis());
        getWritableDatabase().insert(TABLE_USER, null, contentValues);
        return user;
    }

    private Void doDeleteUser(int userId) {
        getWritableDatabase().delete(TABLE_USER, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        return null;
    }

    private Void doUpdateUser(User user) {
        getWritableDatabase().update(
                TABLE_USER,
                getContentValuesForUser(user),
                COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())}
        );
        return null;
    }

    private Void doInsertRecord(Record record) {
        ContentValues contentValues = getContentValuesForRecord(record);
        contentValues.remove(COLUMN_RECORD_ID); // БД сама назначит id.
        int id = (int) getWritableDatabase().insert(TABLE_RECORD, null, contentValues);
        record.setId(id);
        return null;
    }

    public Void doDeleteRecord(int recordId) {
        getWritableDatabase().delete(TABLE_RECORD, COLUMN_RECORD_ID + " = ?", new String[]{String.valueOf(recordId)});
        return null;
    }

    private Void doUpdateRecord(Record record) {
        getWritableDatabase().update(
                TABLE_RECORD,
                getContentValuesForRecord(record),
                COLUMN_RECORD_ID + " = ?",
                new String[]{String.valueOf(record.getId())}
        );
        return null;
    }

    public Void doDeleteAllRecordsAndUsers() {
        getWritableDatabase().delete(TABLE_RECORD, null, null);
        getWritableDatabase().delete(TABLE_USER, null, null);
        return null;
    }

    private int getRecordsCountForUser(int userId) {
        Cursor cursor = getReadableDatabase().query(
                TABLE_RECORD,
                new String[]{"COUNT(" + TABLE_RECORD + "." + COLUMN_RECORD_ID + ")"},
                COLUMN_RECORD_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null
        );
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    private static ContentValues getContentValuesForRecord(Record record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RECORD_ID, record.getId());
        contentValues.put(COLUMN_RECORD_USER_ID, record.getUserId());
        contentValues.put(COLUMN_RECORD_MESSAGE, record.getMessage());
        contentValues.put(COLUMN_RECORD_ENABLED, record.isEnabled() ? 1 : 0);
        contentValues.put(COLUMN_RECORD_REPEAT_TYPE, record.getRepeatType());
        contentValues.put(COLUMN_RECORD_REPEAT_INFO, record.getRepeatInfo());
        contentValues.put(COLUMN_RECORD_HOUR, record.getHour());
        contentValues.put(COLUMN_RECORD_MINUTE, record.getMinute());
        return contentValues;
    }

    private static ContentValues getContentValuesForUser(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_ID, user.getId());
        contentValues.put(COLUMN_USER_FIRST_NAME, user.getFirstName());
        contentValues.put(COLUMN_USER_LAST_NAME, user.getLastName());
        contentValues.put(COLUMN_USER_PHOTO, user.getPhoto());
        return contentValues;
    }

}
