package com.qwert2603.vkautomessage.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.util.StringUtils;

import java.util.Calendar;

public class Record implements Identifiable, Parcelable {

    /**
     * Повтор каждый день через каждые {@link #mRepeatInfo} часов.
     */
    public static final int REPEAT_TYPE_HOURS_IN_DAY = 0;

    /**
     * Повтор в те дни недели, для которых установлен в 1 соответствующий бит {@link #mRepeatInfo}.
     * Начиная с Вс.
     * {@link Calendar#SUNDAY} == 1, поэтому первый бит пропускается.
     * 01001110 = Вс, Пн, Вт, Пт.
     */
    public static final int REPEAT_TYPE_DAYS_IN_WEEK = 1;

    /**
     * Повтор в каждый год в день {@link #mRepeatInfo}. (mmdd)
     * 0326 = 26 марта.
     * 1231 = 31 декабря.
     * 0229 = 29 февраля.
     */
    public static final int REPEAT_TYPE_DAY_IN_YEAR = 2;

    /**
     * Периоды для отправки при {@link #mRepeatType} == {@link #REPEAT_TYPE_HOURS_IN_DAY}.
     */
    public static final int[] PERIODS = {1, 2, 3, 4, 6, 8, 12, 24};

    /**
     * Период по умолчанию при {@link #mRepeatType} == {@link #REPEAT_TYPE_HOURS_IN_DAY}.
     */
    private static final int DEFAULT_PERIOD = PERIODS[PERIODS.length - 1];

    /**
     * Множитель для номера месяца при {@link #mRepeatType} == {@link #REPEAT_TYPE_DAY_IN_YEAR}.
     */
    private static final int MONTH_MULTIPLIER = 100;

    private static final String ERROR_WRONG_REPEAT_TYPE = "Wrong repeat type!";

    private int mId;
    private int mUserId;

    @NonNull
    private String mMessage;
    private boolean mEnabled;

    private int mRepeatType;
    private int mRepeatInfo;
    private int mHour;
    private int mMinute;

    public Record(int userId) {
        mUserId = userId;
        mMessage = StringUtils.getNewRecordMessage();
        mRepeatType = REPEAT_TYPE_HOURS_IN_DAY;
        mRepeatInfo = DEFAULT_PERIOD;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
    }

    public Record(int id, int userId, @NonNull String message, boolean enabled, int repeatType, int repeatInfo, int hour, int minute) {
        mId = id;
        mUserId = userId;
        mMessage = message;
        mEnabled = enabled;
        mRepeatType = repeatType;
        mRepeatInfo = repeatInfo;
        mHour = hour;
        mMinute = minute;
    }

    protected Record(Parcel in) {
        mId = in.readInt();
        mUserId = in.readInt();
        mMessage = in.readString();
        mEnabled = in.readByte() != 0;
        mRepeatType = in.readInt();
        mRepeatInfo = in.readInt();
        mHour = in.readInt();
        mMinute = in.readInt();
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

    @Override
    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    @NonNull
    public String getMessage() {
        return mMessage;
    }

    public void setMessage(@NonNull String message) {
        mMessage = message;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public int getRepeatType() {
        return mRepeatType;
    }

    public void setRepeatType(int repeatType) {
        if (mRepeatType == repeatType) {
            return;
        }
        mRepeatType = repeatType;
        Calendar calendar = Calendar.getInstance();
        switch (repeatType) {
            case Record.REPEAT_TYPE_HOURS_IN_DAY:
                mRepeatInfo = DEFAULT_PERIOD;
                break;
            case Record.REPEAT_TYPE_DAYS_IN_WEEK:
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                mRepeatInfo = 1 << dayOfWeek;
                break;
            case Record.REPEAT_TYPE_DAY_IN_YEAR:
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                mRepeatInfo = month * MONTH_MULTIPLIER + day;
                break;
        }
    }

    public int getRepeatInfo() {
        return mRepeatInfo;
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        mHour = hour;
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute = minute;
    }

    /**
     * Установить период отправки при {@link #mRepeatType} == {@link #REPEAT_TYPE_HOURS_IN_DAY}.
     *
     * @param period период отправки
     */
    public void setPeriod(int period) {
        checkRepeatType(REPEAT_TYPE_HOURS_IN_DAY);
        mRepeatInfo = period;
    }

    /**
     * @return период отправки при {@link #mRepeatType} == {@link #REPEAT_TYPE_HOURS_IN_DAY}.
     */
    public int getPeriod() {
        checkRepeatType(REPEAT_TYPE_HOURS_IN_DAY);
        return mRepeatInfo;
    }

    /**
     * Установить "включенность отправки" в день недели.
     * при {@link #mRepeatType} == {@link #REPEAT_TYPE_DAYS_IN_WEEK}.
     *
     * @param dayOfWeek день недели.
     * @param enabled   "включенность отправки"
     */
    public void setDayOfWeek(int dayOfWeek, boolean enabled) {
        checkRepeatType(REPEAT_TYPE_DAYS_IN_WEEK);
        if (enabled) {
            mRepeatInfo |= 1 << dayOfWeek;
        } else {
            mRepeatInfo &= ~(1 << dayOfWeek);
        }
    }

    /**
     * Установить "включенность отправки" на все дни недели.
     *
     * @param daysOfWeek число, определяющее в какие дни неледи включена отправка
     */
    public void setDaysOfWeek(int daysOfWeek) {
        checkRepeatType(REPEAT_TYPE_DAYS_IN_WEEK);
        mRepeatInfo = daysOfWeek;
    }

    /**
     * @param dayOfWeek день недели для которого требуется информация.
     * @return включена ли отправка в конкретный день недели
     * при {@link #mRepeatType} == {@link #REPEAT_TYPE_DAYS_IN_WEEK}.
     */
    public boolean isDayOfWeekEnabled(int dayOfWeek) {
        checkRepeatType(REPEAT_TYPE_DAYS_IN_WEEK);
        return (mRepeatInfo & (1 << dayOfWeek)) != 0;
    }

    /**
     * @return число, определяющее в какие дни неледи включена отправка
     * при {@link #mRepeatType} == {@link #REPEAT_TYPE_DAYS_IN_WEEK}.
     */
    public int getDaysInWeek() {
        checkRepeatType(REPEAT_TYPE_DAYS_IN_WEEK);
        return mRepeatInfo;
    }

    /**
     * @return число дней в неделю, в которые включена отправка
     * при {@link #mRepeatType} == {@link #REPEAT_TYPE_DAYS_IN_WEEK}.
     */
    public int getDaysInWeekCount() {
        checkRepeatType(REPEAT_TYPE_DAYS_IN_WEEK);
        int res = 0;
        for (int i = 1; i < Const.DAYS_PER_WEEK + 1; i++) {
            if (isDayOfWeekEnabled(i)) {
                ++res;
            }
        }
        return res;
    }

    /**
     * Установить номер месяц при {@link #mRepeatType} == {@link #REPEAT_TYPE_DAY_IN_YEAR}.
     *
     * @param month номер месяца.
     */
    public void setMonth(int month) {
        checkRepeatType(REPEAT_TYPE_DAY_IN_YEAR);
        mRepeatInfo = month * MONTH_MULTIPLIER + getDayOfMonth();
    }

    /**
     * Установить номер дня в месяце при {@link #mRepeatType} == {@link #REPEAT_TYPE_DAY_IN_YEAR}.
     *
     * @param dayOfMonth номер дня в месяце.
     */
    public void setDayOfMonth(int dayOfMonth) {
        checkRepeatType(REPEAT_TYPE_DAY_IN_YEAR);
        mRepeatInfo = getMonth() * MONTH_MULTIPLIER + dayOfMonth;
    }

    /**
     * @return номер месяца при {@link #mRepeatType} == {@link #REPEAT_TYPE_DAY_IN_YEAR}.
     */
    public int getMonth() {
        checkRepeatType(REPEAT_TYPE_DAY_IN_YEAR);
        return mRepeatInfo / MONTH_MULTIPLIER;
    }

    /**
     * @return номер дня в месяце при {@link #mRepeatType} == {@link #REPEAT_TYPE_DAY_IN_YEAR}.
     */
    public int getDayOfMonth() {
        checkRepeatType(REPEAT_TYPE_DAY_IN_YEAR);
        return mRepeatInfo % MONTH_MULTIPLIER;
    }

    /**
     * Проверить что {@link #mRepeatType} == repeatType.
     *
     * @throws RuntimeException
     */
    private void checkRepeatType(int repeatType) throws RuntimeException {
        if (mRepeatType != repeatType) {
            throw new RuntimeException(ERROR_WRONG_REPEAT_TYPE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (mId != record.mId) return false;
        if (mUserId != record.mUserId) return false;
        if (mEnabled != record.mEnabled) return false;
        if (mRepeatType != record.mRepeatType) return false;
        if (mRepeatInfo != record.mRepeatInfo) return false;
        if (mHour != record.mHour) return false;
        if (mMinute != record.mMinute) return false;
        return mMessage.equals(record.mMessage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeInt(mUserId);
        parcel.writeString(mMessage);
        parcel.writeByte((byte) (mEnabled ? 1 : 0));
        parcel.writeInt(mRepeatType);
        parcel.writeInt(mRepeatInfo);
        parcel.writeInt(mHour);
        parcel.writeInt(mMinute);
    }
}
