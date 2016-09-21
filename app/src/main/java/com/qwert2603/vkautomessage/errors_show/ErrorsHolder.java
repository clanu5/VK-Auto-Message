package com.qwert2603.vkautomessage.errors_show;

import android.content.Context;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;

public class ErrorsHolder {

    private static final String TAG = "ErrorsHolder";

    private static final String FILE_NAME = "errors.txt";

    public ErrorsHolder() {
        VkAutoMessageApplication.getAppComponent().inject(ErrorsHolder.this);
    }

    @Inject
    Context mContext;

    @Inject
    @Named(Const.IO_THREAD)
    Scheduler mIoScheduler;

    @Inject
    @Named(Const.UI_THREAD)
    Scheduler mUiScheduler;

    public void addError(Throwable throwable) {
        synchronized (ErrorsHolder.this) {
            File file = new File(mContext.getFilesDir(), FILE_NAME);
            long length = file.length();
            LogUtils.d(TAG, "addError#length == " + length);
            if (length > 256 * 1024) {
                clearErrors();
            }
            try (FileOutputStream outputStream = mContext.openFileOutput(FILE_NAME, Context.MODE_APPEND)) {
                PrintWriter printWriter = new PrintWriter(outputStream, true);
                printWriter.write(new Date() + "\n");
                throwable.printStackTrace(printWriter);
                printWriter.write("\n\n");
                printWriter.flush();
                LogUtils.d(TAG, "addError#throwable == " + throwable);
            } catch (IOException ignored) {
                LogUtils.d(TAG, ignored.toString());
            }
        }
    }

    public Observable<String> getErrors() {
        return Observable
                .create((Observable.OnSubscribe<String>) subscriber -> {
                    LogUtils.d(TAG, "getErrors");
                    try (FileInputStream inputStream = mContext.openFileInput(FILE_NAME)) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String s;
                        while ((s = bufferedReader.readLine()) != null) {
                            stringBuilder.append(s);
                        }
                        LogUtils.d(TAG, "getErrors#stringBuilder == " + stringBuilder.toString());
                        subscriber.onNext(stringBuilder.toString());
                        subscriber.onCompleted();
                    } catch (IOException ignored) {
                        LogUtils.d(TAG, ignored.toString());
                        subscriber.onError(ignored);
                    }
                })
                .subscribeOn(mIoScheduler)
                .observeOn(mUiScheduler);
    }

    public void clearErrors() {
        boolean deleteFile = mContext.deleteFile(FILE_NAME);
        LogUtils.d(TAG, "clearErrors#deleteFile == " + deleteFile);
    }
}
