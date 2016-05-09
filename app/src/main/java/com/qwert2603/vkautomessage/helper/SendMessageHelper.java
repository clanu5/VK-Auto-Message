package com.qwert2603.vkautomessage.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.service.SendMessageService;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

public class SendMessageHelper {

    private static final int MILLIS_PER_MINUTE = 60 * 1000;
    private static final int MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

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
            // TODO: 01.05.2016 учитывать разные типы отправки
            // (через 1,2,3,4,6,12,24 часа), в разные дни недели и разные дни года
            Calendar alarmCalendar = Calendar.getInstance();
            alarmCalendar.set(Calendar.HOUR_OF_DAY, record.getHour());
            alarmCalendar.set(Calendar.MINUTE, record.getMinute());
            alarmCalendar.set(Calendar.SECOND, 0);
            alarmCalendar.set(Calendar.MILLISECOND, 0);
            long timeInMillis = alarmCalendar.getTimeInMillis();
            int interval = record.getRepeatInfo() * MILLIS_PER_HOUR;    // интервал отправки сообщений.
            int delta = MILLIS_PER_MINUTE;  // используется, чтобы избежать повторной отправки в одно время.
            timeInMillis -= delta;
            while (timeInMillis < System.currentTimeMillis()) {
                timeInMillis += interval;
            }
            while (timeInMillis - interval > System.currentTimeMillis()) {
                timeInMillis -= interval;
            }
            timeInMillis += delta;
            LogUtils.d(new Date(timeInMillis).toString());
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        } else {
            mAlarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public void onRecordRemoved(int recordId) {
        Intent intent = new Intent(mContext, SendMessageService.class);
        intent.putExtra(SendMessageService.EXTRA_RECORD_ID, recordId);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, recordId, intent, 0);
        mAlarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

}
