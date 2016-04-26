package com.qwert2603.vkautomessage.di;

import android.content.Context;

import com.qwert2603.vkautomessage.RxBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Context mAppContext;

    public AppModule(Context appContext) {
        mAppContext = appContext;
    }

    @Provides
    @Singleton
    Context provideAppContext() {
        return mAppContext;
    }

    @Provides
    @Singleton
    RxBus provideRxBus() {
        return new RxBus();
    }

}
