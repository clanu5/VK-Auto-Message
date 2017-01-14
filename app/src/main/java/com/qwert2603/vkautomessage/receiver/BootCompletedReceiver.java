package com.qwert2603.vkautomessage.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.util.LogUtils;

import javax.inject.Inject;

import rx.Observable;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Inject
    DataManager mDataManager;

    public BootCompletedReceiver() {
        if (!Const.IS_TESTING) {
            VkAutoMessageApplication.getAppComponent().inject(BootCompletedReceiver.this);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d("BootCompletedReceiver#onReceive");
        mDataManager
                .getAllRecords()
                .flatMap(Observable::from)
                .subscribe(
                        record -> {
                            if (record.isEnabled()) {
                                mDataManager.putRecordToSendMessageService(record);
                            }
                        },
                        LogUtils::e
                );
    }
}
