package com.qwert2603.vkautomessage.user_list;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;

public class UserListPresenter extends BasePresenter<List<VKApiUserFull>, UserListView> {

    private Subscription mSubscription;
    private boolean mIsLoading;
    private int mSelectedUserId = 0;
    private int mSelectedUserPosition = -1;

    private String mQuery;
    private List<VKApiUserFull> mShowingUserList;

    @Inject
    DataManager mDataManager;

    public UserListPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(UserListPresenter.this);
        loadFriendsList();
    }

    public void setSelectedUserId(int selectedUserId) {
        mSelectedUserId = selectedUserId;
    }

    @Override
    protected void onUpdateView(@NonNull UserListView view) {
        if (mShowingUserList == null) {
            view.setRefreshingConfig(false, false);
            if (mSubscription == null) {
                view.showError();
            } else {
                view.showLoading();
            }
        } else {
            view.setRefreshingConfig(true, mIsLoading);
            if (mShowingUserList.isEmpty()) {
                if (mQuery == null || mQuery.isEmpty()) {
                    view.showEmpty();
                } else {
                    view.showNothingFound();
                }
            } else {
                view.showListWithSelectedItem(mShowingUserList, mSelectedUserPosition);
            }
        }
    }

    @Override
    public void unbindView() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        super.unbindView();
    }

    @Override
    protected void setModel(List<VKApiUserFull> userList) {
        super.setModel(userList);
        doSearch();
        updateView();
    }

    public String getCurrentQuery() {
        return mQuery;
    }

    public void onReload() {
        loadFriendsList();
        updateView();
    }

    public void onUserAtPositionClicked(int position) {
        VKApiUserFull user = mShowingUserList.get(position);
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

    public void onSearchQueryChanged(String query) {
        mQuery = query.toLowerCase();
        doSearch();
        updateView();
    }

    private void doSearch() {
        List<VKApiUserFull> userList = getModel();
        mShowingUserList = null;
        if (userList != null) {
            if (mQuery == null || mQuery.isEmpty()) {
                mShowingUserList = userList;
            } else {
                mShowingUserList = new ArrayList<>();
                for (VKApiUserFull user : userList) {
                    if (user.first_name.toLowerCase().startsWith(mQuery) || user.last_name.toLowerCase().startsWith(mQuery)) {
                        mShowingUserList.add(user);
                    }
                }
            }
            mSelectedUserPosition = findPositionOfUser(mShowingUserList, mSelectedUserId);
        }
    }

    private void loadFriendsList() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        mIsLoading = true;
        mSubscription = mDataManager
                .getAllVkFriends()
                .subscribe(
                        userList -> {
                            mIsLoading = false;
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

    private static int findPositionOfUser(List<VKApiUserFull> userList, int userId) {
        for (int i = 0, size = userList.size(); i < size; i++) {
            if (userList.get(i).id == userId) {
                return i;
            }
        }
        return -1;
    }

}
