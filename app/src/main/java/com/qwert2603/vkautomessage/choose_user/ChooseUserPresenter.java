package com.qwert2603.vkautomessage.choose_user;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.VkUser;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class ChooseUserPresenter extends BasePresenter<List<VkUser>, ChooseUserView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();
    private boolean mIsLoading;

    private String mQuery;
    private List<VkUser> mShowingUserList;

    @Inject
    DataManager mDataManager;

    private boolean mPendingIntroAnimation = true;

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
                if (mPendingIntroAnimation) {
                    new Handler(Looper.getMainLooper()).post(() -> view.showList(mShowingUserList, true));
                } else {
                    view.showList(mShowingUserList, false);
                }
            }
            if (mPendingIntroAnimation) {
                mPendingIntroAnimation = false;
            }
        }
    }

    @Override
    public void unbindView() {
        mSubscription.unsubscribe();
        super.unbindView();
    }

    @Override
    protected void setModel(List<VkUser> userList) {
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
        VkUser user = mShowingUserList.get(position);
        if (user.isCanWrite()) {
            switch (user.getId()) {
                case Const.DEVELOPER_VK_ID:
                    getView().showDontWriteToDeveloper();
                    break;
                case Const.RITA_VK_ID:
                    getView().showGreatChoice();
                    break;
            }
            getView().showItemSelected(position);
            getView().submitDode(user.getId());
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
        List<VkUser> userList = getModel();
        mShowingUserList = null;
        if (userList != null) {
            if (mQuery == null || mQuery.isEmpty()) {
                mShowingUserList = userList;
            } else {
                mShowingUserList = new ArrayList<>();
                for (VkUser user : userList) {
                    if (user.getFirstName().toLowerCase().startsWith(mQuery) || user.getLastName().toLowerCase().startsWith(mQuery)) {
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
