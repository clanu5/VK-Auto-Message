package com.qwert2603.vkautomessage.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.qwert2603.vkautomessage.VkAutoMessageApplication;
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
        VkAutoMessageApplication.getAppComponent().inject(SendMessageHelper.this);
    }

    public void onRecordChanged(Record record) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

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
            if (alarmCalendar.getTimeInMillis() < System.currentTimeMillis()) {
                alarmCalendar.setTime(new Date(alarmCalendar.getTimeInMillis() + MILLIS_PER_DAY));
            }
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
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
