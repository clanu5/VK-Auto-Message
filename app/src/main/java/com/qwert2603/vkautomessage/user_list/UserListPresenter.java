package com.qwert2603.vkautomessage.user_list;

import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.in_out_animation.ShouldCheckIsInningOrInside;
import com.qwert2603.vkautomessage.base.list.ListPresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class UserListPresenter extends ListPresenter<User, List<User>, UserListView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();
    private Subscription mRxBusSubscription = Subscriptions.unsubscribed();

    @Inject
    DataManager mDataManager;

    @Inject
    RxBus mRxBus;

    public UserListPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(UserListPresenter.this);
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
        mRxBusSubscription = mRxBus.toObservable()
                .filter(event -> event.mEvent == RxBus.Event.EVENT_USERS_VK_DATA_UPDATED)
                .subscribe(event -> {
                    List<User> userList = getModel();
                    Map<Integer, User> updatedUsers = (Map<Integer, User>) event.mObject;
                    if (userList == null || updatedUsers == null) {
                        return;
                    }
                    List<Integer> updatedPositions = new ArrayList<>();
                    for (int i = 0; i < userList.size(); i++) {
                        int id = userList.get(i).getId();
                        if (updatedUsers.containsKey(id)) {
                            userList.get(i).setVkDataFrom(updatedUsers.get(id));
                            updatedPositions.add(i);
                        }
                    }
                    UserListView view1 = getView();
                    if (view1 == null || updatedPositions.isEmpty()) {
                        return;
                    }
                    view1.notifyUsersUpdated(updatedPositions);
                }, LogUtils::e);
    }

    @Override
    public void unbindView() {
        mRxBusSubscription.unsubscribe();
        mRxBusSubscription = Subscriptions.unsubscribed();
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

    @ShouldCheckIsInningOrInside
    public void onItemDeleteSubmitted(int id) {
        if (!isInningOrInside()) {
            return;
        }
        super.onItemDeleteSubmitted(id);
        int position = getUserPosition(id);
        List<User> userList = getModel();
        UserListView view = getView();
        userList.remove(position);
        if (userList.size() > 0) {
            view.notifyItemRemoved(position);
        } else {
            updateView();
        }

        mDataManager.removeUser(id)
                .subscribe(aVoid -> {
                }, LogUtils::e);
    }

    public void onChooseUserClicked() {
        getView().showChooseUser();
    }

    @ShouldCheckIsInningOrInside
    public void onUserChosen(int userId) {
        if (!isInningOrInside()) {
            return;
        }

        int userPosition = getUserPosition(userId);
        if (userPosition >= 0) {
            List<User> userList = getModel();
            UserListView view = getView();
            if (userList == null || view == null) {
                return;
            }
            getView().scrollToPosition(userPosition);
            animateOut(userId);
        } else {
            mDataManager.getVkUserById(userId)
                    .flatMap(mDataManager::addUser)
                    .doOnNext(user -> {
                        for (int i = 0; i < 300; i++) {
                            mDataManager.addRecord(new Record(userId)).subscribe();
                        }
                    })
                    .subscribe(user -> {
                        List<User> userList = getModel();
                        UserListView view = getView();
                        if (userList == null || view == null) {
                            return;
                        }

                        user.setRecordsCount(0);
                        user.setEnabledRecordsCount(0);
                        userList.add(user);

                        view.animateAllItemsEnter(false);
                        view.delayEachItemEnterAnimation(false);
                        if (userList.size() == 1) {
                            view.showList(userList);
                        } else {
                            view.scrollListToBottom();
                        }
                        view.notifyItemInserted(userList.size() - 1, userId);

                        animateOut(userId);
                    }, LogUtils::e);
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
