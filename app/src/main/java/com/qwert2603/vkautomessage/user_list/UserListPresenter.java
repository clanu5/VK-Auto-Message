package com.qwert2603.vkautomessage.user_list;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class UserListPresenter extends BasePresenter<List<User>, UserListView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();

    @Inject
    DataManager mDataManager;

    @Inject
    RxBus mRxBus;

    private boolean mPendingToolbarIntroAnimation = true;
    private AnimationState mListAnimationState = AnimationState.WAITING_FOR_TRIGGER;

    public UserListPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(UserListPresenter.this);
        mRxBus.toObservable()
                .subscribe(event -> {
                    if (event.mEvent == RxBus.Event.EVENT_USERS_PHOTO_UPDATED) {
                        loadUserList();
                    }
                }, LogUtils::e);
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
        } else if (mListAnimationState != AnimationState.WAITING_FOR_TRIGGER) {
            if (userList.isEmpty()) {
                view.showEmpty();
            } else {
                view.showList(userList, mListAnimationState == AnimationState.SHOULD_START);
            }
            if (mListAnimationState == AnimationState.SHOULD_START) {
                view.runFABIntroAnimation();
                mListAnimationState = AnimationState.STARTED;
            }
        }
    }

    @Override
    public void unbindView() {
        mSubscription.unsubscribe();
        super.unbindView();
    }

    public void onCreateOptionsMenu() {
        if (mPendingToolbarIntroAnimation) {
            mPendingToolbarIntroAnimation = false;
            getView().prepareForIntroAnimation();
            getView().runToolbarIntroAnimation();
        }
    }

    public void onResume() {
        loadUserList();
    }

    public void onToolbarIntroAnimationFinished() {
        mListAnimationState = AnimationState.SHOULD_START;
        updateView();
    }

    public void onUserAtPositionClicked(int position) {
        List<User> model = getModel();
        if (model == null) {
            return;
        }
        getView().moveToRecordsForUser(model.get(position).getId());
    }

    public void onUserAtPositionLongClicked(int position) {
        showDeleteUser(position);
    }

    public void onUserDeleteClicked(int userId) {
        getView().showUserSelected(-1);
        int position = getUserPosition(userId);
        mDataManager.removeUser(userId)
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

    public void onUserDeleteCanceled(int recordId) {
        getView().showUserSelected(-1);
    }

    public void onChooseUserClicked() {
        getView().showChooseUser();
    }

    public void onUserDismissed(int position) {
        LogUtils.d("UserListPresenter onUserDismissed " + position);
        showDeleteUser(position);
    }

    public void onReload() {
        loadUserList();
        updateView();
    }

    public void onUserChosen(int userId) {
        int userPosition = getUserPosition(userId);
        if (userPosition >= 0) {
            getView().moveToRecordsForUser(userId);
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
                                view.moveToRecordsForUser(userId);
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

    private void showDeleteUser(int position) {
        List<User> model = getModel();
        if (model == null) {
            return;
        }
        getView().showDeleteUser(model.get(position).getId());
        getView().showUserSelected(position);
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
