package com.qwert2603.vkautomessage.presenter;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.util.StringUtils;
import com.qwert2603.vkautomessage.view.UserView;
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
