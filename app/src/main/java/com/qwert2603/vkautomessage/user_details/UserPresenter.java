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
        if (user.getRecordsCount() == User.NO_INFO || user.getEnabledRecordsCount() == User.NO_INFO) {
            view.hideRecordsCount();
        } else {
            view.showRecordsCount(user.getRecordsCount(), user.getEnabledRecordsCount());
        }
    }

    @Override
    public void onViewNotReady() {
        UserView view = getView();
        if (view != null) {
            if (view.getPhotoImageView() != null) {
                view.getPhotoImageView().setImageBitmap(null);
            }
            ImageLoader.getInstance().cancelDisplayTask(view.getPhotoImageView());
        }
        super.onViewNotReady();
    }

}
