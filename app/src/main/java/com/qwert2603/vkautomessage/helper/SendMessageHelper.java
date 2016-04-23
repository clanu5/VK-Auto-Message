package com.qwert2603.vkautomessage.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.service.SendMessageService;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

public class SendMessageHelper {

    /**
     * Кол-во миллисекунд в сутках.
     */
    private static final int MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

    @Inject
    Context mContext;

    public SendMessageHelper() {
        // TODO: 23.04.2016 inject
    }

    public void onRecordChanged(Record record) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(mContext, SendMessageService.class);
        intent.putExtra(SendMessageService.EXTRA_RECORD_ID, record.getId());
        PendingIntent pendingIntent = PendingIntent.getService(mContext, record.getId(), intent, 0);
        if (record.isEnabled()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(record.getTime());

            Calendar alarmCalendar = Calendar.getInstance();
            alarmCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            alarmCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            alarmCalendar.set(Calendar.SECOND, 0);
            if (alarmCalendar.getTimeInMillis() < System.currentTimeMillis()) {
                alarmCalendar.setTime(new Date(alarmCalendar.getTimeInMillis() + MILLIS_PER_DAY));
            }
            // TODO: 26.03.2016 использовать alarmManager.setExact(); (или нет)
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public void onRecordRemoved(int recordId) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(mContext, SendMessageService.class);
        intent.putExtra(SendMessageService.EXTRA_RECORD_ID, recordId);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, recordId, intent, 0);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

}
