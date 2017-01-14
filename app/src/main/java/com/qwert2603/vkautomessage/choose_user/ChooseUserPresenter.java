package com.qwert2603.vkautomessage.choose_user;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.list.ListPresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.VkUser;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class ChooseUserPresenter extends ListPresenter<VkUser, List<VkUser>, ChooseUserView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();
    private boolean mIsLoading;

    private String mQuery;

    @Inject
    DataManager mDataManager;

    public ChooseUserPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(ChooseUserPresenter.this);
    }

    @Override
    protected Transformer<List<VkUser>, List<VkUser>> listFromModel() {
        return vkUsers -> vkUsers;
    }

    @Override
    protected Transformer<List<VkUser>, List<VkUser>> showingListFromModel() {
        return vkUsers -> {
            if (vkUsers == null) {
                return null;
            }
            List<VkUser> showingList = null;
            if (mQuery == null || mQuery.isEmpty()) {
                showingList = vkUsers;
            } else {
                showingList = new ArrayList<>();
                for (VkUser user : vkUsers) {
                    if (user.getFirstName().toLowerCase().startsWith(mQuery) || user.getLastName().toLowerCase().startsWith(mQuery)) {
                        showingList.add(user);
                    }
                }
            }
            return showingList;
        };
    }

    @Override
    protected boolean isError() {
        return getModel() == null && mSubscription.isUnsubscribed();
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
    protected Observable<Void> removeItem(int id) {
        return Observable.error(new RuntimeException("Should not be called!"));
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
        if (getShowingList() == null) {
            view.setRefreshingConfig(false, false);
        } else {
            view.setRefreshingConfig(true, mIsLoading);
            if (getShowingList().isEmpty()) {
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

    public String getCurrentQuery() {
        return mQuery;
    }

    @Override
    public void onItemAtPositionClicked(int position) {
        VkUser user = getShowingList().get(position);
        if (user.isCanWrite()) {
            switch (user.getId()) {
                case Const.DEVELOPER_VK_ID:
                    getView().showDontWriteToDeveloper();
                    break;
                case Const.RITA_VK_ID:
                    getView().showGreatChoice();
                    break;
            }
            getView().submitDode(getShowingList().get(position).getId());
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
        updateShowingList();
        getView().scrollToTop();
    }

    public void onCancelClicked() {
        getView().submitCancel();
    }

}
