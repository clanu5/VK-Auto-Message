package com.qwert2603.vkautomessage.user_list;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.data.DataManager;
import com.qwert2603.vkautomessage.model.entity.Record;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.List;

import rx.Subscription;

public class UserListPresenter extends BasePresenter<List<VKApiUserFull>, UserListView> {

    private Subscription mSubscription;
    private VKApiUserFull mSelectedUser;

    public UserListPresenter(Record record) {
        loadFriendsList();
        mSelectedUser = record.getUser();
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
                view.showUserSelected(mSelectedUser);
            }
        }
    }

    public void onReload() {
        loadFriendsList();
    }

    public void onUserSelected(VKApiUserFull user) {
        if (user.can_write_private_message) {
            mSelectedUser = user;
        } else {
            getView().showCantWrite(user);
        }
    }

    public void onSubmitClicked() {
        getView().submitDode(mSelectedUser);
    }

    public void loadFriendsList() {
        mSubscription = DataManager.getInstance()
                .getAllFriends()
                .subscribe(
                        UserListPresenter.this::setModel,
                        throwable -> {
                            if (mSubscription != null) {
                                mSubscription.unsubscribe();
                                mSubscription = null;
                            }
                            updateView();
                            throwable.printStackTrace();
                        }
                );
    }

}
