package com.qwert2603.vkautomessage.user_details;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.User;
import com.squareup.picasso.Picasso;

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
            Picasso.with(photoImageView.getContext()).load(user.getPhoto()).into(photoImageView);
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
            ImageView photoImageView = view.getPhotoImageView();
            if (photoImageView != null) {
                photoImageView.setImageBitmap(null);
                Picasso.with(photoImageView.getContext()).cancelRequest(photoImageView);
            }
        }
        super.onViewNotReady();
    }

}
