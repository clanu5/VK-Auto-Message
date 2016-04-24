package com.qwert2603.vkautomessage;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.qwert2603.vkautomessage.di.AppComponent;
import com.qwert2603.vkautomessage.di.AppModule;
import com.qwert2603.vkautomessage.di.DaggerAppComponent;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.vk.sdk.VKSdk;
import com.vk.sdk.util.VKUtil;

import java.io.File;

public class VkAutoMessageApplication extends Application {

    private static AppComponent sAppComponent;

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate() {
        super.onCreate();

        sAppComponent = buildAppComponent();

        VKSdk.initialize(this);
        for (String s : VKUtil.getCertificateFingerprint(this, this.getPackageName())) {
            LogUtils.d("CertificateFingerprint", "CertificateFingerprint == " + s);
        }

        File cacheDir = new File(VkAutoMessageApplication.this.getFilesDir(), "images");
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(VkAutoMessageApplication.this)
                .threadPriority(Thread.NORM_PRIORITY - 3)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new LruMemoryCache(12 * 1024 * 1024))
                .memoryCacheSize(12 * 1024 * 1024)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .defaultDisplayImageOptions(displayImageOptions)
                .build();
        ImageLoader.getInstance().init(config);

        // TODO: 02.04.2016 добавить испанский язык. (или французский)
    }

    protected AppComponent buildAppComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(VkAutoMessageApplication.this))
                .build();
    }

}
