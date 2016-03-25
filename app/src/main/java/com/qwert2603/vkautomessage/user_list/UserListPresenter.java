package com.qwert2603.vkautomessage.user_list;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.List;

import rx.Subscription;

public class UserListPresenter extends BasePresenter<List<VKApiUserFull>, UserListView> {

    private Subscription mSubscription;
    private int mSelectedUserId;

    public UserListPresenter(int selectedUserId) {
        loadFriendsList();
        mSelectedUserId = selectedUserId;
    }

    @Override
    public void unbindView() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        super.unbindView();
    }

    @Override
    protected void onUpdateView(@NonNull UserListView view) {
        List<VKApiUserFull> userList = getModel();
        if (userList == null) {
            if (mSubscription == null) {
                view.showError();
            } else {
                view.showLoading();
            }
        } else {
            if (userList.isEmpty()) {
                view.showEmpty();
            } else {
                view.showList(userList);
            }
        }
    }

    public void onReload() {
        setModel(null);
        loadFriendsList();
        updateView();
    }

    public void onUserClicked(VKApiUserFull user) {
        if (user.can_write_private_message) {
            mSelectedUserId = user.id;
            updateView();
        } else {
            getView().showCantWrite();
        }
    }

    public void onSubmitClicked() {
        getView().submitDode(mSelectedUserId);
    }

    public int getSelectedUserId() {
        return mSelectedUserId;
    }

    public void loadFriendsList() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        mSubscription = DataManager.getInstance()
                .getAllVkFriends()
                .subscribe(
                        model -> {
                            UserListPresenter.this.setModel(model);
                        },
                        throwable -> {
                            if (mSubscription != null) {
                                mSubscription.unsubscribe();
                                mSubscription = null;
                            }
                            setModel(null);
                            updateView();
                            LogUtils.e(throwable);
                        }
                );
    }

}
