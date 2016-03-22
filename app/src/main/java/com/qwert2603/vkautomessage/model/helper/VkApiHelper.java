package com.qwert2603.vkautomessage.model.helper;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

import java.util.List;

import rx.Observable;

public final class VkApiHelper {

    /**
     * Задержка перед следующим запросом.
     * Чтобы запросы не посылались слишком часто. (Не больше 3 в секунду).
     */
    public final long nextRequestDelay = 350;

    /**
     * Время, когда можно посылать следующий запрос.
     */
    private long nextRequestTime;

    {
        nextRequestTime = SystemClock.uptimeMillis();
    }

    /**
     * Обрботчик отправки запросов
     */
    private final Handler mHandler;

    {
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Отправить запрос.
     * Запросы выполняются последовательно.
     * Переданный запрос будет отправлен когда придет его время.
     */
    public synchronized void sendRequest(VKRequest request, VKRequest.VKRequestListener listener) {
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

    public void logOut() {
        if (VKSdk.isLoggedIn()) {
            VKSdk.logout();
        }
    }

    public Observable<List<VKApiUserFull>> getFriends() {
        return Observable
                .create(subscriber -> {
                    VKParameters vkParameters = VKParameters.from(VKApiConst.FIELDS, "photo_100, can_write_private_message");
                    VKRequest request = VKApi.friends().get(vkParameters);
                    request.setUseLooperForCallListener(false);
                    sendRequest(request, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            subscriber.onNext((VKUsersArray) response.parsedModel);
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onError(VKError error) {
                            subscriber.onError(new RuntimeException(error.toString()));
                        }
                    });
                });
    }

    public Observable<VKApiUserFull> getUserById(int userId) {
        return Observable
                .create(subscriber -> {
                    VKParameters vkParameters = VKParameters.from(VKApiConst.USER_IDS, String.valueOf(userId),
                            VKApiConst.FIELDS, "photo_100");
                    VKRequest request = VKApi.users().get(VKParameters.from(vkParameters));
                    request.setUseLooperForCallListener(false);
                    sendRequest(request, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            subscriber.onNext(((VKUsersArray) response.parsedModel).get(0));
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onError(VKError error) {
                            subscriber.onError(new RuntimeException(error.toString()));
                        }
                    });
                });
    }

}
