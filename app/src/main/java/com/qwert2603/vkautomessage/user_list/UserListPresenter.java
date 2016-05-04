package com.qwert2603.vkautomessage.user_list;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class UserListPresenter extends BasePresenter<List<User>, UserListView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();

    @Inject
    DataManager mDataManager;

    @Inject
    RxBus mRxBus;

    @Inject
    @Named(Const.UI_THREAD)
    Scheduler mUiScheduler;

    public UserListPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(UserListPresenter.this);
        mRxBus.toObservable()
                .observeOn(mUiScheduler)
                .subscribe(o -> {
                    if ((o instanceof Integer)) {
                        int integer = (Integer) o;
                        if (integer == RxBus.EVENT_USERS_PHOTO_UPDATED) {
                            updateView();
                        }
                    }
                });
    }

    @Override
    public void bindView(UserListView view) {
        super.bindView(view);
        if (getModel() == null && mSubscription.isUnsubscribed()) {
            loadUserList();
        }
    }

    @Override
    protected void onUpdateView(@NonNull UserListView view) {
        List<User> userList = getModel();
        if (userList == null) {
            if (mSubscription.isUnsubscribed()) {
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

    public void onUserAtPositionClicked(int position) {
        getView().moveToRecordsForUser(getModel().get(position).getId());
    }

    public void onUserAtPositionLongClicked(int position) {
        getView().showDeleteUser(getModel().get(position).getId());
    }

    public void onUserDeleteClicked(int userId) {
        int position = getUserPosition(userId);
        mDataManager.removeUser(userId)
                .subscribe(aVoid -> {
                    if (getModel().size() > 1) {
                        UserListView view = getView();
                        if (view != null) {
                            view.notifyItemRemoved(position);
                        }
                    } else {
                        updateView();
                    }
                }, LogUtils::e);
    }

    public void onChooseUserClicked() {
        getView().showChooseUser();
    }

    public void onReload() {
        loadUserList();
        updateView();
    }

    public void onUserChosen(int userId) {
        int userPosition = getUserPosition(userId);
        if (userPosition > 0) {
            getView().moveToRecordsForUser(userId);
        } else {
            mDataManager.getUserById(userId)
                    .doOnNext(user -> {
                        List<User> userList = getModel();
                        if (userList != null) {
                            userList.add(user);
                        }
                    })
                    .flatMap(mDataManager::addUser)
                    .subscribe(
                            aVoid -> {
                                UserListView view = getView();
                                if (view != null) {
                                    updateView();
                                    view.moveToRecordsForUser(userId);
                                }
                            }, LogUtils::e
                    );
        }
    }

    private void loadUserList() {
        mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllUsers()
                .subscribe(
                        model -> UserListPresenter.this.setModel(model),
                        throwable -> {
                            mSubscription.unsubscribe();
                            updateView();
                            LogUtils.e(throwable);
                        }
                );

    }

    private int getUserPosition(int userId) {
        List<User> userList = getModel();
        for (int i = 0, size = (userList == null ? 0 : userList.size()); i < size; ++i) {
            if (userList.get(i).getId() == userId) {
                return i;
            }
        }
        return -1;
    }
}
