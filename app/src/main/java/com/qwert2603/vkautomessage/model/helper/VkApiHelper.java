package com.qwert2603.vkautomessage.model.helper;

import com.qwert2603.vkautomessage.util.VkApiUtils;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

import java.util.List;

import rx.Observable;

public final class VkApiHelper {
    private static VkApiHelper sVkApiHelper = new VkApiHelper();

    private VkApiHelper() {
    }

    public static VkApiHelper getInstance() {
        return sVkApiHelper;
    }

    public Observable<List<VKApiUserFull>> getFriends() {
        return Observable
                .create(subscriber -> {
                    VKRequest request = VKApi.friends().get(VKParameters.from());
                    request.setUseLooperForCallListener(false);
                    VkApiUtils.sendRequest(request, new VKRequest.VKRequestListener() {
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

}
