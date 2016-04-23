package com.qwert2603.vkautomessage.di;

import android.content.Context;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.helper.DatabaseHelper;
import com.qwert2603.vkautomessage.helper.PreferenceHelper;
import com.qwert2603.vkautomessage.helper.SendMessageHelper;
import com.qwert2603.vkautomessage.helper.VkApiHelper;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class ModelModule {

    // TODO: 23.04.2016 использовать зависимость, если можно.
    private Context mAppContext;

    public ModelModule(Context appContext) {
        mAppContext = appContext;
    }

    @Provides
    @Singleton
    PreferenceHelper providePreferenceHelper() {
        return new PreferenceHelper();
    }

    @Provides
    @Singleton
    SendMessageHelper provideSendMessageHelper() {
        return new SendMessageHelper();
    }

    @Provides
    @Singleton
    VkApiHelper provideVkApiHelper() {
        return new VkApiHelper();
    }

    @Provides
    @Singleton
    DatabaseHelper provideDatabaseHelper() {
        return new DatabaseHelper(mAppContext);
    }

    @Provides
    @Singleton
    @Named(Const.UI_THREAD)
    Scheduler provideSchedulerUI() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @Singleton
    @Named(Const.IO_THREAD)
    Scheduler provideSchedulerIO() {
        return Schedulers.io();
    }
}
