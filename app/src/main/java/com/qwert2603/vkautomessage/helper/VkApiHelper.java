package com.qwert2603.vkautomessage.helper;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.qwert2603.vkautomessage.util.LogUtils;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

import org.json.JSONException;

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
                    VKParameters vkParameters = VKParameters.from(
                            VKApiConst.FIELDS, "photo_100, can_write_private_message",
                            "order", "hints");
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
        VKParameters vkParameters = VKParameters.from(VKApiConst.USER_IDS, String.valueOf(userId),
                VKApiConst.FIELDS, "photo_100");
        return getUsers(vkParameters);
    }

    public Observable<VKApiUserFull> getMyself() {
        VKParameters vkParameters = VKParameters.from(VKApiConst.FIELDS, "photo_100");
        return getUsers(vkParameters);
    }

    private Observable<VKApiUserFull> getUsers(VKParameters vkParameters) {
        return Observable
                .create(subscriber -> {
                    VKRequest request = VKApi.users().get(vkParameters);
                    request.setUseLooperForCallListener(false);
                    sendRequest(request, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            try {
                                VKApiUserFull user = new VKApiUserFull()
                                        .parse(response.json.getJSONArray("response").getJSONObject(0));
                                subscriber.onNext(user);
                            } catch (JSONException e) {
                                LogUtils.e(e);
                            }
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onError(VKError error) {
                            subscriber.onError(new RuntimeException(error.toString()));
                        }
                    });
                });
    }

    /**
     * Исключение -- ошибка отправки сообщения
     */
    public static class SendMessageException extends Exception {
        /**
         * Объект для идентификации сообщения, которое не удалось отправить.
         */
        public Object mToken;

        public SendMessageException(String detailMessage, Object token) {
            super(detailMessage);
            mToken = token;
        }
    }

    /**
     * Отправить сообщение.
     * @param token объект, который будет передаст Observable, который вернет метод.
     *              (для идентификации сообщения и адресата).
     *              При ошибке будет передано исключение SendMessageException.
     *              mToken этого иключения будет содержать переданный методу @param token.
     */
    public Observable<Object> sendMessage(int userId, String message, Object token) {
        return Observable
                .create(subscriber -> {
                    VKParameters parameters = VKParameters.from(
                            VKApiConst.USER_ID, userId, VKApiConst.MESSAGE, message);
                    VKRequest request = new VKRequest("messages.send", parameters);
                    request.setUseLooperForCallListener(false);
                    sendRequest(request, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            subscriber.onNext(token);
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onError(VKError error) {
                            subscriber.onError(new SendMessageException(error.toString(), token));
                        }
                    });
                });
    }

}
