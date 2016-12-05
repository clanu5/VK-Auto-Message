package com.qwert2603.vkautomessage.choose_user;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.in_out_animation.ShouldCheckIsInningOrInside;
import com.qwert2603.vkautomessage.base.list.ListPresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.VkUser;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class ChooseUserPresenter extends ListPresenter<VkUser, List<VkUser>, ChooseUserView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();
    private boolean mIsLoading;

    private String mQuery;
    private List<VkUser> mShowingUserList;

    @Inject
    DataManager mDataManager;

    public ChooseUserPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(ChooseUserPresenter.this);
    }

    @Override
    protected List<VkUser> getList() {
        return mShowingUserList;
    }

    @Override
    protected boolean isError() {
        return getModel() == null && mSubscription.isUnsubscribed();
    }

    @Override
    protected boolean isFirstAnimateInWithLargeDelay() {
        return false;
    }

    @Override
    protected void doLoadList() {
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

    @Override
    protected void doLoadItem(int id) {
        onReloadList();
    }

    @Override
    public void bindView(ChooseUserView view) {
        super.bindView(view);
        if (getModel() == null && mSubscription.isUnsubscribed()) {
            doLoadList();
        }
    }

    @Override
    protected void onUpdateView(@NonNull ChooseUserView view) {
        super.onUpdateView(view);
        if (mShowingUserList == null) {
            view.setRefreshingConfig(false, false);
        } else {
            view.setRefreshingConfig(true, mIsLoading);
            if (mShowingUserList.isEmpty()) {
                if (mQuery != null && !mQuery.isEmpty()) {
                    view.showNothingFound();
                }
            }
        }
    }

    @Override
    public void unbindView() {
        mSubscription.unsubscribe();
        super.unbindView();
    }

    @Override
    public void onReloadList() {
        if (mIsLoading) {
            return;
        }
        super.onReloadList();
    }

    @Override
    protected void setModel(List<VkUser> userList) {
        doSearch(userList);
        super.setModel(userList);
    }

    public String getCurrentQuery() {
        return mQuery;
    }

    @Override
    protected void performMoveToItem(int itemIdToMove, boolean moveWithSetPressed) {
        getView().submitDode(itemIdToMove);
    }

    @ShouldCheckIsInningOrInside
    @Override
    public void onItemAtPositionClicked(int position) {
        if (!isInningOrInside()) {
            return;
        }
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
            super.onItemAtPositionClicked(position);
        } else {
            getView().showCantWrite();
        }
    }

    @Override
    public void onItemAtPositionLongClicked(int position) {
        onItemAtPositionClicked(position);
    }

    public void onSearchQueryChanged(String query) {
        mQuery = query.toLowerCase();
        doSearch(getModel());
        updateView();
    }

    private void doSearch(List<VkUser> userList) {
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

}
