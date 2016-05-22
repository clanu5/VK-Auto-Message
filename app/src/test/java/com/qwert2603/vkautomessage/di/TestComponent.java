package com.qwert2603.vkautomessage.di;

import com.qwert2603.vkautomessage.user_details.UserPresenterTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AppTestModule.class,
        ModelModule.class,
        PresenterTestModule.class,
        ViewModule.class
})
public interface TestComponent extends AppComponent {
    void inject(UserPresenterTest userPresenterTest);
}
