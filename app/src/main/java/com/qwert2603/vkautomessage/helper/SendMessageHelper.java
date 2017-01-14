package com.qwert2603.vkautomessage.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.service.SendMessageService;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

public class SendMessageHelper {

    @Inject
    Context mContext;

    AlarmManager mAlarmManager;

    public SendMessageHelper() {
        VkAutoMessageApplication.getAppComponent().inject(SendMessageHelper.this);
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    public void onRecordChanged(Record record) {
        Intent intent = new Intent(mContext, SendMessageService.class);
        intent.putExtra(SendMessageService.EXTRA_RECORD_ID, record.getId());
        PendingIntent pendingIntent = PendingIntent.getService(mContext, record.getId(), intent, 0);
        if (record.isEnabled()) {
            long nextSendingInMillis = getNextSendingInMillis(record, System.currentTimeMillis());
            LogUtils.d("onRecordChanged nextSendingInMillis == " + String.valueOf(new Date(nextSendingInMillis)));
            // TODO: 14.01.2017 use correct method on api >= 23
            mAlarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    nextSendingInMillis,
                    pendingIntent);
        } else {
            mAlarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    /**
     * @param record объект записи
     * @return время следующей отправки для записи в миллисекундах.
     */
    public static long getNextSendingInMillis(Record record, long currentTimeMillis) {
        int delta = Const.MILLIS_PER_MINUTE;  // используется, чтобы избежать повторной отправки в одно время.
        int repeatType = record.getRepeatType();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, record.getHour());
        calendar.set(Calendar.MINUTE, record.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (repeatType == Record.REPEAT_TYPE_HOURS_IN_DAY || repeatType == Record.REPEAT_TYPE_DAYS_IN_WEEK) {
            // 29 февраля == 1 марта.
            long timeInMillis = calendar.getTimeInMillis();
            // интервал отправки сообщений.
            int interval = (repeatType == Record.REPEAT_TYPE_HOURS_IN_DAY) ?
                    (record.getPeriod() * Const.MILLIS_PER_HOUR) : Const.MILLIS_PER_DAY;

            timeInMillis -= delta;
            while (timeInMillis < currentTimeMillis) {
                timeInMillis += interval;
            }
            while (timeInMillis - interval > currentTimeMillis) {
                timeInMillis -= interval;
            }
            calendar.setTimeInMillis(timeInMillis + delta);
            if (repeatType == Record.REPEAT_TYPE_DAYS_IN_WEEK) {
                while (!record.isDayOfWeekEnabled(calendar.get(Calendar.DAY_OF_WEEK))) {
                    calendar.setTimeInMillis(calendar.getTimeInMillis() + Const.MILLIS_PER_DAY);
                }
            }
        } else if (repeatType == Record.REPEAT_TYPE_DAY_IN_YEAR) {
            calendar.set(Calendar.MONTH, record.getMonth());
            calendar.set(Calendar.DAY_OF_MONTH, record.getDayOfMonth());

            calendar.setTimeInMillis(calendar.getTimeInMillis() - delta);
            while (calendar.getTimeInMillis() < currentTimeMillis) {
                calendar.roll(Calendar.YEAR, true);
            }
            Calendar prevYearCalendar = Calendar.getInstance();
            prevYearCalendar.setTimeInMillis(calendar.getTimeInMillis());
            prevYearCalendar.roll(Calendar.YEAR, false);
            while (prevYearCalendar.getTimeInMillis() > currentTimeMillis) {
                calendar.roll(Calendar.YEAR, false);
                prevYearCalendar.roll(Calendar.YEAR, false);
            }
            calendar.setTimeInMillis(calendar.getTimeInMillis() + delta);
        }

        return calendar.getTimeInMillis();
    }

    public void onRecordRemoved(int recordId) {
        Intent intent = new Intent(mContext, SendMessageService.class);
        intent.putExtra(SendMessageService.EXTRA_RECORD_ID, recordId);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, recordId, intent, 0);
        mAlarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

}
