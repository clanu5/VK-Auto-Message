package com.qwert2603.vkautomessage.choose_user;

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
import rx.subscriptions.Subscriptions;

public class ChooseUserPresenter extends BasePresenter<List<VKApiUserFull>, ChooseUserView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();
    private boolean mIsLoading;

    private String mQuery;
    private List<VKApiUserFull> mShowingUserList;

    @Inject
    DataManager mDataManager;

    public ChooseUserPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(ChooseUserPresenter.this);
    }

    @Override
    public void bindView(ChooseUserView view) {
        super.bindView(view);
        if (getModel() == null && mSubscription.isUnsubscribed()) {
            loadFriendsList();
        }
    }

    @Override
    protected void onUpdateView(@NonNull ChooseUserView view) {
        if (mShowingUserList == null) {
            view.setRefreshingConfig(false, false);
            if (mSubscription.isUnsubscribed()) {
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
                view.showList(mShowingUserList);
            }
        }
    }

    @Override
    public void unbindView() {
        mSubscription.unsubscribe();
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
            getView().showItemSelected(position);
            getView().submitDode(user.id);
        } else {
            getView().showCantWrite();
        }
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
        }
    }

    private void loadFriendsList() {
        mSubscription.unsubscribe();
        mIsLoading = true;
        mSubscription = mDataManager
                .getAllVkFriends()
                .subscribe(
                        userList -> {
                            mIsLoading = false;
                            ChooseUserPresenter.this.setModel(userList);
                        },
                        throwable -> {
                            mIsLoading = false;
                            mSubscription.unsubscribe();
                            setModel(null);
                            updateView();
                            LogUtils.e(throwable);
                        }
                );
    }

}
