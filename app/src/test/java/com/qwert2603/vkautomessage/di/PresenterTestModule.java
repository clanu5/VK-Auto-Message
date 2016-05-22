package com.qwert2603.vkautomessage.di;

import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.user_details.UserView;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PresenterTestModule {

    @Provides
    @Singleton
    DataManager provideDataManager() {
        return Mockito.mock(DataManager.class);
    }

    @Provides
    @Singleton
    UserView provideUserView() {
        return Mockito.mock(UserView.class);
    }

}
