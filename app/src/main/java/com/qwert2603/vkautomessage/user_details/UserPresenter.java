package com.qwert2603.vkautomessage.user_details;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.vk.sdk.api.model.VKApiUserFull;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;

public class UserPresenter extends BasePresenter<VKApiUserFull, UserView> {

    public UserPresenter(VKApiUserFull user) {
        setModel(user);
    }

    @Override
    protected void onUpdateView(@NonNull UserView view) {
        VKApiUserFull user = getModel();
        if (user == null) {
            return;
        }
        view.showName(getUserName(user));
        view.showPhoto(null);
        DataManager.getInstance()
                .getPhotoByUrl(user.photo_100)
                .subscribe(
                        photo -> {
                            UserView userView = getView();
                            if (userView != null) {
                                userView.showPhoto(photo);
                            }
                        },
                        LogUtils::e
                );
    }

    public VKApiUserFull getUser() {
        return getModel();
    }
}
