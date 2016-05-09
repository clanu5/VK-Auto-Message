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
import com.qwert2603.vkautomessage.model.RecordWithUser;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.record_details.RecordActivity;
import com.qwert2603.vkautomessage.record_list.RecordListActivity;
import com.qwert2603.vkautomessage.util.InternetUtils;
import com.qwert2603.vkautomessage.util.LogUtils;

import javax.inject.Inject;

import rx.Observable;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;
import static com.qwert2603.vkautomessage.util.StringUtils.noMore;

public class SendMessageService extends IntentService {

    public static final String EXTRA_RECORD_ID = "com.qwert2603.vkautomessage.service.EXTRA_RECORD_ID";

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
                .flatMap(recordWithUser -> {
                    if (!InternetUtils.isInternetConnected(SendMessageService.this)) {
                        Throwable throwable = new VkApiHelper.SendMessageException
                                ("SendMessageService ## Internet not connected!", recordWithUser);
                        return Observable.error(throwable);
                    }
                    User user = recordWithUser.mUser;
                    return mDataManager.sendVkMessage(user.getId(), recordWithUser.mRecord.getMessage(), recordWithUser);
                })
                .subscribe(
                        record -> {
                            showResultNotification((RecordWithUser) record, true);
                        },
                        throwable -> {
                            LogUtils.e(throwable);
                            RecordWithUser record = (RecordWithUser) ((VkApiHelper.SendMessageException) throwable).mToken;
                            showResultNotification(record, false);
                        }
                );
    }

    private void showResultNotification(RecordWithUser recordWithUser, boolean success) {
        String ticker = success ? getString(R.string.notification_ticker_success) : getString(R.string.notification_ticker_fail);

        Intent intent = new Intent(SendMessageService.this, RecordActivity.class);
        intent.putExtra(RecordActivity.EXTRA_RECORD_ID, recordWithUser.mRecord.getId());

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(SendMessageService.this)
                .addParentStack(RecordActivity.class)
                .addNextIntent(intent);
        taskStackBuilder.editIntentAt(1)
                .putExtra(RecordListActivity.EXTRA_USER_ID, recordWithUser.mUser.getId());
        PendingIntent pendingIntent = taskStackBuilder
                .getPendingIntent(recordWithUser.mRecord.getId(), PendingIntent.FLAG_ONE_SHOT);

        String contentText = getUserName(recordWithUser.mUser)
                + "\n" + noMore(recordWithUser.mRecord.getMessage(), MESSAGE_LENGTH_LIMIT);

        Notification notification = new NotificationCompat.Builder(SendMessageService.this)
                .setSmallIcon(success ? android.R.drawable.stat_sys_upload_done : android.R.drawable.stat_notify_error)
                .setTicker(ticker)
                .setContentTitle(ticker)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        int lastNotificationId = mDataManager.getLastNotificationId();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(++lastNotificationId, notification);
        mDataManager.setLastNotificationId(lastNotificationId);

        // назначаем следующую отправку сообщения.
        mDataManager.putRecordToSendMessageService(recordWithUser.mRecord);
    }
}
