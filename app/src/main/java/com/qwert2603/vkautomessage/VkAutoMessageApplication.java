package com.qwert2603.vkautomessage;

import android.app.Application;

import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.vk.sdk.VKSdk;
import com.vk.sdk.util.VKUtil;

public class VkAutoMessageApplication extends Application {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
        DataManager.initWithContext(VkAutoMessageApplication.this);
        for (String s : VKUtil.getCertificateFingerprint(this, this.getPackageName())) {
            LogUtils.d("CertificateFingerprint", "CertificateFingerprint == " + s);
        }
        // TODO: 02.04.2016 добавить испанский язык. (или французский)
        // TODO: 22.04.2016 use dagger && переделать базовые фрагмент и диалог
        // TODO: 22.04.2016 use библиотеку для изображений
    }

}
