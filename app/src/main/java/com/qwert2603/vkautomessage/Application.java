package com.qwert2603.vkautomessage;

import com.qwert2603.vkautomessage.model.data.DataManager;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.vk.sdk.VKSdk;
import com.vk.sdk.util.VKUtil;

public class Application extends android.app.Application {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
        DataManager.initWithContext(Application.this);
        for (String s : VKUtil.getCertificateFingerprint(this, this.getPackageName())) {
            LogUtils.d("CertificateFingerprint", "CertificateFingerprint == " + s);
        }
    }

}
