package com.qwert2603.vkautomessage.di;

import android.content.Context;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.model.User;

import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppTestModule {

    private Context mTestAppContext;

    public AppTestModule(Context testAppContext) {
        mTestAppContext = testAppContext;
    }

    @Provides
    @Singleton
    Context provideTestAppContext() {
        return mTestAppContext;
    }

    @Provides
    @Singleton
    RxBus provideRxBus() {
        return new RxBus();
    }

    @Provides
    @Singleton
    List<User> provideUserList() {
        return Arrays.asList(
                new User(Const.DEVELOPER_VK_ID, "Alex", "Zhdanov", "link_alex"),
                new User(Const.RITA_VK_ID, "Rita", "Zhdanova", "link_rita")
        );
    }

}
