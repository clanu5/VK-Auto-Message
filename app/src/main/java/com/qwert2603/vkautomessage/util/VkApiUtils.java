package com.qwert2603.vkautomessage.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.qwert2603.vkautomessage.activity.LoginActivity;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKRequest;

public final class VkApiUtils {
    /**
     * Задержка перед следующим запросом.
     * Чтобы запросы не посылались слишком часто. (Не больше 3 в секунду).
     */
    public static final long nextRequestDelay = 350;

    /**
     * Время, когда можно посылать следующий запрос.
     */
    private static long nextRequestTime;

    static {
        nextRequestTime = SystemClock.uptimeMillis();
    }

    /**
     * Обрботчик отправки запросов
     */
    private final static Handler mHandler;

    static {
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Отправить запрос.
     * Запросы выполняются последовательно.
     * Переданный запрос будет отправлен когда придет его время.
     */
    public static synchronized void sendRequest(VKRequest request, VKRequest.VKRequestListener listener) {
        if (!VKSdk.isLoggedIn()) {
            return;
        }
        if (nextRequestTime <= SystemClock.uptimeMillis()) {
            request.executeWithListener(listener);
            nextRequestTime = SystemClock.uptimeMillis();
        } else {
            mHandler.postAtTime(() -> {
                if (VKSdk.isLoggedIn()) {
                    request.executeWithListener(listener);
                }
            }, nextRequestTime);
        }
        nextRequestTime += nextRequestDelay;
    }

    /**
     * Выйти из ВК.
     *
     * @param activity - вызывающая Activity, она будет завершена.
     */
    public static void logOut(Activity activity) {
        if (VKSdk.isLoggedIn()) {
            VKSdk.logout();
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }
}
