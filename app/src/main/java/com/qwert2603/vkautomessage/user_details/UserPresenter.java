package com.qwert2603.vkautomessage.user_details;

import android.support.annotation.NonNull;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.User;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;

public class UserPresenter extends BasePresenter<User, UserView> {

    public UserPresenter() {
    }

    public void setUser(User user) {
        setModel(user);
    }

    @Override
    protected void onUpdateView(@NonNull UserView view) {
        User user = getModel();
        if (user == null) {
            return;
        }
        view.showName(getUserName(user));
        ImageLoader.getInstance().displayImage(user.getPhoto(), view.getPhotoImageView());
    }

    @Override
    public void onViewNotReady() {
        UserView view = getView();
        if (view != null) {
            ImageLoader.getInstance().cancelDisplayTask(view.getPhotoImageView());
        }
        super.onViewNotReady();
    }

    public User getUser() {
        // TODO: 03.05.2016 нужен ли этот метод?
        return getModel();
    }
}
