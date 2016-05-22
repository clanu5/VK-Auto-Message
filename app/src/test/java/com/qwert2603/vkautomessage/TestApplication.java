package com.qwert2603.vkautomessage;

import com.qwert2603.vkautomessage.di.AppComponent;
import com.qwert2603.vkautomessage.di.AppTestModule;
import com.qwert2603.vkautomessage.di.DaggerTestComponent;

public class TestApplication extends VkAutoMessageApplication {
    @Override
    protected AppComponent buildAppComponent() {
        return DaggerTestComponent.builder()
                .appTestModule(new AppTestModule(TestApplication.this))
                .build();
    }
}
