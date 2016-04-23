package com.qwert2603.vkautomessage.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.util.LogUtils;

import rx.Observable;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DataManager.getInstance()
                .getAllRecords()
                .flatMap(Observable::from)
                .subscribe(
                        record -> {
                            if (record.isEnabled()) {
                                DataManager.getInstance().putRecordToSendMessageService(record);
                            }
                        },
                        LogUtils::e
                );
    }
}
