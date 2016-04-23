package com.qwert2603.vkautomessage.di;

import android.content.Context;

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

}