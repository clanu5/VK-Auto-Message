package com.qwert2603.vkautomessage.user_details;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.user_list.UserListPresenter;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.vk.sdk.api.model.VKApiUserFull;

import java.lang.ref.WeakReference;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;
import static com.qwert2603.vkautomessage.util.StringUtils.noMore;

public class UserPresenter extends BasePresenter<VKApiUserFull, UserView> {

    private static final int USERNAME_LENGTH_LIMIT = 26;

    private WeakReference<UserListPresenter> mUserListPresenter;

    public UserPresenter(VKApiUserFull user, @Nullable UserListPresenter userListPresenter) {
        setModel(user);
        if (userListPresenter != null) {
            mUserListPresenter = new WeakReference<>(userListPresenter);
        }
    }

    @Override
    protected void onUpdateView(@NonNull UserView view) {
        VKApiUserFull user = getModel();
        if (user == null) {
            return;
        }
        view.showName(noMore(getUserName(user), USERNAME_LENGTH_LIMIT));
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
        view.showSelected(false);
        if (mUserListPresenter != null) {
            UserListPresenter userListPresenter = mUserListPresenter.get();
            if (userListPresenter != null) {
                view.showSelected(userListPresenter.getSelectedUserId() == getModel().id);
            }
        }
    }

    public VKApiUserFull getUser() {
        return getModel();
    }
}
