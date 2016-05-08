package com.qwert2603.vkautomessage.user_details;

import android.support.annotation.NonNull;
import android.widget.ImageView;

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
        ImageView photoImageView = view.getPhotoImageView();
        if (photoImageView != null) {
            ImageLoader.getInstance().displayImage(user.getPhoto(), photoImageView);
        }
        // todo передавать recordsCount одним методом.
        view.showRecordsCount(user.getRecordsCount() != User.NO_INFO ? String.valueOf(user.getRecordsCount()) : "");
        view.showEnabledRecordsCount(user.getEnabledRecordsCount() != User.NO_INFO ? String.valueOf(user.getEnabledRecordsCount()) : "");
        // TODO: 08.05.2016 переделать layout, чтобы recordsCount не уходило за границу.
    }

    @Override
    public void onViewNotReady() {
        UserView view = getView();
        if (view != null) {
            ImageLoader.getInstance().cancelDisplayTask(view.getPhotoImageView());
        }
        super.onViewNotReady();
    }

}
