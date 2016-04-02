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
    private boolean mIsLoading;
    private int mSelectedUserId = 0;
    private int mSelectedUserPosition = -1;

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
            view.setRefreshingConfig(false, false);
            if (mSubscription == null) {
                view.showError();
            } else {
                view.showLoading();
            }
        } else {
            view.setRefreshingConfig(true, mIsLoading);
            if (userList.isEmpty()) {
                view.showEmpty();
            } else {
                view.showList(userList);
                view.setSelectedItemPosition(mSelectedUserPosition);
            }
        }
    }

    public void onReload() {
        loadFriendsList();
        updateView();
    }

    public void onUserAtPositionClicked(int position) {
        VKApiUserFull user = getModel().get(position);
        if (user.can_write_private_message) {
            mSelectedUserId = user.id;
            mSelectedUserPosition = position;
            getView().setSelectedItemPosition(mSelectedUserPosition);
        } else {
            getView().showCantWrite();
        }
    }

    public void onSubmitClicked() {
        getView().submitDode(mSelectedUserId);
    }

    public void loadFriendsList() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        mIsLoading = true;
        mSubscription = DataManager.getInstance()
                .getAllVkFriends()
                .subscribe(
                        userList -> {
                            mIsLoading = false;
                            mSelectedUserPosition = findPositionOfUser(userList, mSelectedUserId);
                            UserListPresenter.this.setModel(userList);
                        },
                        throwable -> {
                            mIsLoading = false;
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

    private int findPositionOfUser(List<VKApiUserFull> userList, int userId) {
        for (int i = 0, size = userList.size(); i < size; i++) {
            if (userList.get(i).id == userId) {
                return i;
            }
        }
        return -1;
    }

}
