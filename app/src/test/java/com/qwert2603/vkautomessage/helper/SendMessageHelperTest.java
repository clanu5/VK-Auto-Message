package com.qwert2603.vkautomessage.helper;

import com.qwert2603.vkautomessage.model.Record;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class SendMessageHelperTest {

    private Record mRecord;
    private int mTodayOfWeek;
    private long mTodayTimeMillis;    // 12 мая 2016 года. 15:09:43.263.
    private long mFeb29TimeMillis;    // 29 февраля 2016 года. 15:09:43.263.
    private long mDec31TimeMillis;    // 31 декабря 2016 года. 15:09:43.263.

    @Before
    public void setUp() {
        mRecord = new Record(0);

        Calendar calendar = Calendar.getInstance();

        mTodayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 9);
        calendar.set(Calendar.SECOND, 54);
        calendar.set(Calendar.MILLISECOND, 263);
        mTodayTimeMillis = calendar.getTimeInMillis();

        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        mFeb29TimeMillis = calendar.getTimeInMillis();

        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        mDec31TimeMillis = calendar.getTimeInMillis();
    }

    @Test
    public void test0() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_HOURS_IN_DAY);
        mRecord.setPeriod(1);
        mRecord.setHour(19);
        mRecord.setMinute(18);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 18);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test1() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_HOURS_IN_DAY);
        mRecord.setPeriod(1);
        mRecord.setHour(6);
        mRecord.setMinute(13);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 13);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test2() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_HOURS_IN_DAY);
        mRecord.setPeriod(1);
        mRecord.setHour(6);
        mRecord.setMinute(7);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 7);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test3() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_HOURS_IN_DAY);
        mRecord.setPeriod(3);
        mRecord.setHour(6);
        mRecord.setMinute(7);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 7);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test4() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_HOURS_IN_DAY);
        mRecord.setPeriod(12);
        mRecord.setHour(6);
        mRecord.setMinute(7);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 7);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test5() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_HOURS_IN_DAY);
        mRecord.setPeriod(12);
        mRecord.setHour(14);
        mRecord.setMinute(17);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 13);
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 17);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test6() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_HOURS_IN_DAY);
        mRecord.setPeriod(12);
        mRecord.setHour(15);
        mRecord.setMinute(9);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 13);
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 9);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test7() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_HOURS_IN_DAY);
        mRecord.setPeriod(12);
        mRecord.setHour(15);
        mRecord.setMinute(10);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 13);
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 10);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test8() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_HOURS_IN_DAY);
        mRecord.setPeriod(12);
        mRecord.setHour(15);
        mRecord.setMinute(11);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 11);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test9() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_HOURS_IN_DAY);
        mRecord.setPeriod(24);
        mRecord.setHour(0);
        mRecord.setMinute(0);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 13);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test10() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_HOURS_IN_DAY);
        mRecord.setPeriod(24);
        mRecord.setHour(23);
        mRecord.setMinute(59);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test11() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAYS_IN_WEEK);
        mRecord.setDayOfWeek(mTodayOfWeek, false);
        mRecord.setDayOfWeek(Calendar.WEDNESDAY, true);
        mRecord.setHour(19);
        mRecord.setMinute(18);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 18);
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 18);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test12() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAYS_IN_WEEK);
        mRecord.setDayOfWeek(mTodayOfWeek, false);
        mRecord.setDayOfWeek(Calendar.WEDNESDAY, true);
        mRecord.setHour(12);
        mRecord.setMinute(15);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 18);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 15);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test13() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAYS_IN_WEEK);
        mRecord.setDayOfWeek(mTodayOfWeek, false);
        mRecord.setDayOfWeek(Calendar.SUNDAY, true);
        mRecord.setHour(12);
        mRecord.setMinute(15);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 15);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 15);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test14() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAYS_IN_WEEK);
        mRecord.setDayOfWeek(mTodayOfWeek, false);
        mRecord.setDayOfWeek(Calendar.MONDAY, true);
        mRecord.setHour(12);
        mRecord.setMinute(15);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 16);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 15);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test14_2() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAYS_IN_WEEK);
        mRecord.setDayOfWeek(mTodayOfWeek, false);
        mRecord.setDayOfWeek(Calendar.FRIDAY, true);
        mRecord.setHour(12);
        mRecord.setMinute(15);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 13);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 15);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test14_3() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAYS_IN_WEEK);
        mRecord.setDayOfWeek(mTodayOfWeek, false);
        mRecord.setDayOfWeek(Calendar.WEDNESDAY, true);
        mRecord.setHour(12);
        mRecord.setMinute(15);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 18);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 15);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test14_4() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAYS_IN_WEEK);
        mRecord.setDayOfWeek(mTodayOfWeek, false);
        mRecord.setDayOfWeek(Calendar.FRIDAY, true);
        mRecord.setHour(0);
        mRecord.setMinute(0);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 13);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test14_5() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAYS_IN_WEEK);
        mRecord.setDayOfWeek(mTodayOfWeek, false);
        mRecord.setDayOfWeek(Calendar.THURSDAY, true);
        mRecord.setHour(0);
        mRecord.setMinute(0);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 19);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test14_6() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAYS_IN_WEEK);
        mRecord.setDayOfWeek(mTodayOfWeek, false);
        mRecord.setDayOfWeek(Calendar.THURSDAY, true);
        mRecord.setHour(12);
        mRecord.setMinute(0);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 19);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test14_7() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAYS_IN_WEEK);
        mRecord.setDayOfWeek(mTodayOfWeek, false);
        mRecord.setDayOfWeek(Calendar.THURSDAY, true);
        mRecord.setHour(15);
        mRecord.setMinute(27);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE,27);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test15() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAY_IN_YEAR);
        mRecord.setMonth(Calendar.JULY);
        mRecord.setDayOfMonth(25);
        mRecord.setHour(12);
        mRecord.setMinute(15);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.JULY);
        calendar.set(Calendar.DAY_OF_MONTH, 25);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 15);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test16() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAY_IN_YEAR);
        mRecord.setMonth(Calendar.FEBRUARY);
        mRecord.setDayOfMonth(21);
        mRecord.setHour(12);
        mRecord.setMinute(15);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 15);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test17() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAY_IN_YEAR);
        mRecord.setMonth(Calendar.MARCH);
        mRecord.setDayOfMonth(1);
        mRecord.setHour(12);
        mRecord.setMinute(15);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 15);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test18() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAY_IN_YEAR);
        mRecord.setMonth(Calendar.FEBRUARY);
        mRecord.setDayOfMonth(29);
        mRecord.setHour(12);
        mRecord.setMinute(15);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 15);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test19() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAY_IN_YEAR);
        mRecord.setMonth(Calendar.MAY);
        mRecord.setDayOfMonth(12);
        mRecord.setHour(19);
        mRecord.setMinute(18);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 18);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test20() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAY_IN_YEAR);
        mRecord.setMonth(Calendar.MAY);
        mRecord.setDayOfMonth(12);
        mRecord.setHour(15);
        mRecord.setMinute(10);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 10);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test21() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAY_IN_YEAR);
        mRecord.setMonth(Calendar.DECEMBER);
        mRecord.setDayOfMonth(31);
        mRecord.setHour(23);
        mRecord.setMinute(59);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test22() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAY_IN_YEAR);
        mRecord.setMonth(Calendar.JANUARY);
        mRecord.setDayOfMonth(1);
        mRecord.setHour(0);
        mRecord.setMinute(0);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mTodayTimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test23() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_HOURS_IN_DAY);
        mRecord.setHour(18);
        mRecord.setMinute(14);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mFeb29TimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 14);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test24() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_HOURS_IN_DAY);
        mRecord.setHour(14);
        mRecord.setMinute(14);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mFeb29TimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 14);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test25() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAY_IN_YEAR);
        mRecord.setMonth(Calendar.MARCH);
        mRecord.setDayOfMonth(1);
        mRecord.setHour(2);
        mRecord.setMinute(19);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mFeb29TimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 19);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test26() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAY_IN_YEAR);
        mRecord.setMonth(Calendar.JANUARY);
        mRecord.setDayOfMonth(29);
        mRecord.setHour(2);
        mRecord.setMinute(19);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mFeb29TimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 19);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test27() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_HOURS_IN_DAY);
        mRecord.setHour(18);
        mRecord.setMinute(14);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mDec31TimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 14);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test28() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_HOURS_IN_DAY);
        mRecord.setHour(14);
        mRecord.setMinute(14);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mDec31TimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 14);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test29() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAY_IN_YEAR);
        mRecord.setMonth(Calendar.JANUARY);
        mRecord.setDayOfMonth(1);
        mRecord.setHour(2);
        mRecord.setMinute(19);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mDec31TimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 19);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }

    @Test
    public void test30() {
        mRecord.setRepeatType(Record.REPEAT_TYPE_DAY_IN_YEAR);
        mRecord.setMonth(Calendar.DECEMBER);
        mRecord.setDayOfMonth(31);
        mRecord.setHour(2);
        mRecord.setMinute(19);

        long sendingInMillis = SendMessageHelper.getNextSendingInMillis(mRecord, mDec31TimeMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 19);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Assert.assertEquals(new Date(calendar.getTimeInMillis()), new Date(sendingInMillis));
    }


}