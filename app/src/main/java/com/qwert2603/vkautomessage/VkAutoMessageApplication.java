package com.qwert2603.vkautomessage;

import android.app.Application;

import com.qwert2603.vkautomessage.di.AppComponent;
import com.qwert2603.vkautomessage.di.AppModule;
import com.qwert2603.vkautomessage.di.DaggerAppComponent;
import com.vk.sdk.VKSdk;

public class VkAutoMessageApplication extends Application {

    private static AppComponent sAppComponent;

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sAppComponent = buildAppComponent();

        VKSdk.initialize(VkAutoMessageApplication.this);
//        for (String s : VKUtil.getCertificateFingerprint(VkAutoMessageApplication.this,
//                VkAutoMessageApplication.this.getPackageName())) {
//            LogUtils.d("CertificateFingerprint", "CertificateFingerprint == " + s);
//        }
    }

    protected AppComponent buildAppComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(VkAutoMessageApplication.this))
                .build();
    }

}
