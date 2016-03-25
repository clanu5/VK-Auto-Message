package com.qwert2603.vkautomessage.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.helper.VkApiHelper;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.record_list.RecordListActivity;
import com.qwert2603.vkautomessage.util.InternetUtils;
import com.qwert2603.vkautomessage.util.LogUtils;

import rx.Observable;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;
import static com.qwert2603.vkautomessage.util.StringUtils.noMore;

public class SendMessageService extends IntentService {

    public static final String EXTRA_RECORD_ID = "com.qwert2603.vkautomessage.EXTRA_RECORD_TO_DELETE_ID";

    private static final int MESSAGE_LENGTH_LIMIT = 52;

    public SendMessageService() {
        super("SendMessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DataManager.initWithContext(getApplicationContext());
        DataManager.getInstance()
                .getRecordById(intent.getIntExtra(EXTRA_RECORD_ID, 0))
                .flatMap(record -> {
                    if (!InternetUtils.isInternetConnected(SendMessageService.this)) {
                        Throwable throwable = new VkApiHelper.SendMessageException
                                ("SendMessageService ## Internet not connected!", record);
                        return Observable.error(throwable);
                    }
                    return DataManager.getInstance().sendVkMessage(record.getUser().id, record.getMessage(), record);
                })
                .subscribe(
                        record -> {
                            showResultNotification((Record) record, true);
                        },
                        throwable -> {
                            LogUtils.e(throwable);
                            Record record = (Record) ((VkApiHelper.SendMessageException) throwable).mToken;
                            showResultNotification(record, false);
                        }
                );
    }

    @SuppressWarnings("deprecation")
    private void showResultNotification(Record record, boolean success) {
        String ticker = success ? getString(R.string.notification_ticker_success) : getString(R.string.notification_ticker_fail);
        // TODO: 25.03.2016 запускать RecordActivity для этой записи, добавить в стек RecordListActivity
        Intent intent = new Intent(SendMessageService.this, RecordListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(SendMessageService.this, 0, intent, 0);
        Notification notification = new Notification.Builder(SendMessageService.this)
                .setSmallIcon(success ? android.R.drawable.stat_sys_upload_done : android.R.drawable.stat_notify_error)
                .setTicker(ticker)
                .setContentTitle(ticker)
                .setContentText(getUserName(record.getUser()) + "\n" + noMore(record.getMessage(), MESSAGE_LENGTH_LIMIT))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .getNotification();

        int lastNotificationId = DataManager.getInstance().getLastNotificationId();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(++lastNotificationId, notification);
        DataManager.getInstance().setLastNotificationId(lastNotificationId);
    }
}
