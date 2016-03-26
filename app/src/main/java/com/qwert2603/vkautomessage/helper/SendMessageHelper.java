package com.qwert2603.vkautomessage.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.service.SendMessageService;

import java.util.Calendar;

public final class SendMessageHelper {

    private Context mContext;

    public SendMessageHelper(Context context) {
        mContext = context.getApplicationContext();
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
                alarmCalendar.roll(Calendar.DATE, true);
            }
            // TODO: 26.03.2016 перейти на minSdkVersion = 19, и использовать alarmManager.setExact();
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
