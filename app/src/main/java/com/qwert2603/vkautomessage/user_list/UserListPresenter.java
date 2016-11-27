package com.qwert2603.vkautomessage.user_list;

import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.list.ListPresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class UserListPresenter extends ListPresenter<User, List<User>, UserListView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();

    @Inject
    DataManager mDataManager;

    @Inject
    RxBus mRxBus;

    public UserListPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(UserListPresenter.this);
        mRxBus.toObservable()
                .subscribe(event -> {
                    if (event.mEvent == RxBus.Event.EVENT_USERS_PHOTO_UPDATED) {
                        doLoadList();
                    }
                }, LogUtils::e);
    }

    @Override
    protected List<User> getList() {
        return getModel();
    }

    @Override
    protected boolean isError() {
        return getModel() == null && mSubscription.isUnsubscribed();
    }

    @Override
    protected boolean isFirstAnimateInWithLargeDelay() {
        return true;
    }

    @Override
    public void bindView(UserListView view) {
        super.bindView(view);
        if (getModel() == null && mSubscription.isUnsubscribed()) {
            doLoadList();
        }
    }

    @Override
    public void unbindView() {
        mSubscription.unsubscribe();
        super.unbindView();
    }

    @Override
    protected void doLoadList() {
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

    public void onItemDeleteSubmitted(int id) {
        super.onItemDeleteSubmitted(id);
        int position = getUserPosition(id);
        mDataManager.removeUser(id)
                .subscribe(aVoid -> {
                    List<User> userList = getModel();
                    UserListView view = getView();
                    if (userList == null || view == null) {
                        return;
                    }
                    userList.remove(position);
                    if (userList.size() > 0) {
                        view.notifyItemRemoved(position);
                    } else {
                        updateView();
                    }
                }, LogUtils::e);
    }

    public void onChooseUserClicked() {
        getView().showChooseUser();
    }

    public void onUserChosen(int userId) {
        int userPosition = getUserPosition(userId);
        if (userPosition >= 0) {
            animateOut(userId);
        } else {
            mDataManager.getVkUserById(userId)
                    .flatMap(mDataManager::addUser)
                    .subscribe(
                            user -> {
                                List<User> userList = getModel();
                                UserListView view = getView();
                                if (userList == null || view == null) {
                                    return;
                                }
                                userList.add(user);
                                if (userList.size() > 1) {
                                    view.notifyItemInserted(userList.size() - 1);
                                } else {
                                    updateView();
                                }
                                animateOut(userId);
                            }, LogUtils::e
                    );
        }
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
