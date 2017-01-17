package com.qwert2603.vkautomessage.user_details;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.StringUtils;

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

        view.showName(StringUtils.getUserName(user));
        view.showPhoto(user.getPhoto(), StringUtils.getUserInitials(user));

        if (user.getRecordsCount() == User.NO_INFO || user.getEnabledRecordsCount() == User.NO_INFO) {
            view.hideRecordsCount();
        } else {
            view.showRecordsCount(user.getRecordsCount(), user.getEnabledRecordsCount());
        }
    }

}
