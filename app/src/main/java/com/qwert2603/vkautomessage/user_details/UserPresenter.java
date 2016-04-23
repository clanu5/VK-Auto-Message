package com.qwert2603.vkautomessage.user_details;

import android.support.annotation.NonNull;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.vk.sdk.api.model.VKApiUserFull;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;

public class UserPresenter extends BasePresenter<VKApiUserFull, UserView> {

    public UserPresenter() {
    }

    public void setUser(VKApiUserFull user) {
        setModel(user);
    }

    @Override
    protected void onUpdateView(@NonNull UserView view) {
        VKApiUserFull user = getModel();
        if (user == null) {
            return;
        }
        view.showName(getUserName(user));
        ImageLoader.getInstance().displayImage(user.photo_100, view.getPhotoImageView());
    }

    public VKApiUserFull getUser() {
        return getModel();
    }
}
