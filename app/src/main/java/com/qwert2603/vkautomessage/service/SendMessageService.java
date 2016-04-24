package com.qwert2603.vkautomessage.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.helper.VkApiHelper;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.record_details.RecordActivity;
import com.qwert2603.vkautomessage.util.InternetUtils;
import com.qwert2603.vkautomessage.util.LogUtils;

import javax.inject.Inject;

import rx.Observable;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;
import static com.qwert2603.vkautomessage.util.StringUtils.noMore;

public class SendMessageService extends IntentService {

    public static final String EXTRA_RECORD_ID = "com.qwert2603.vkautomessage.EXTRA_RECORD_TO_DELETE_ID";

    private static final int MESSAGE_LENGTH_LIMIT = 52;

    @Inject
    DataManager mDataManager;

    public SendMessageService() {
        super("SendMessageService");
        VkAutoMessageApplication.getAppComponent().inject(SendMessageService.this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mDataManager
                .getRecordById(intent.getIntExtra(EXTRA_RECORD_ID, 0))
                .flatMap(record -> {
                    if (!InternetUtils.isInternetConnected(SendMessageService.this)) {
                        Throwable throwable = new VkApiHelper.SendMessageException
                                ("SendMessageService ## Internet not connected!", record);
                        return Observable.error(throwable);
                    }
                    return mDataManager.sendVkMessage(record.getUser().id, record.getMessage(), record);
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

    private void showResultNotification(Record record, boolean success) {
        String ticker = success ? getString(R.string.notification_ticker_success) : getString(R.string.notification_ticker_fail);

        Intent intent = new Intent(SendMessageService.this, RecordActivity.class);
        intent.putExtra(RecordActivity.EXTRA_RECORD_ID, record.getId());

        PendingIntent pendingIntent = TaskStackBuilder.create(SendMessageService.this)
                .addParentStack(RecordActivity.class)
                .addNextIntent(intent)
                .getPendingIntent(record.getId(), PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new NotificationCompat.Builder(SendMessageService.this)
                .setSmallIcon(success ? android.R.drawable.stat_sys_upload_done : android.R.drawable.stat_notify_error)
                .setTicker(ticker)
                .setContentTitle(ticker)
                .setContentText(getUserName(record.getUser()) + "\n" + noMore(record.getMessage(), MESSAGE_LENGTH_LIMIT))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        int lastNotificationId = mDataManager.getLastNotificationId();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(++lastNotificationId, notification);
        mDataManager.setLastNotificationId(lastNotificationId);
    }
}
