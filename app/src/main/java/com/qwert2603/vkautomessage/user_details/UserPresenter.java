package com.qwert2603.vkautomessage.user_details;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.util.StringUtils;
import com.vk.sdk.api.model.VKApiUserFull;

public class UserPresenter extends BasePresenter<VKApiUserFull, UserView> {
    @Override
    protected void onUpdateView(@NonNull UserView view) {
        VKApiUserFull user = getModel();
        if (user == null) {
            return;
        }
        view.showName(StringUtils.getUserName(user));
        // TODO: 18.03.2016  view.showPhoto();
    }
}
