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

    private AnimationState mListAnimationState = AnimationState.WAITING_FOR_TRIGGER;
    private InOutState mInOutState = InOutState.FIRST_TIME;

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
    public void unbindView() {
        mSubscription.unsubscribe();
        super.unbindView();
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
                mListAnimationState = AnimationState.STARTED;
            }
        }
    }

    public void onReadyToAnimateIn() {
        LogUtils.d("onReadyToAnimateIn");
        if (mInOutState == InOutState.FIRST_TIME) {
            mInOutState = InOutState.INNING;
            getView().prepareForIn();
            getView().animateIn(true);
        } else if (mInOutState == InOutState.OUTSIDE) {
            // TODO: 27.11.2016 при уничтожении активити SceneTransitionAnimation не работает.
            // если получится сделать, чтобы работало, то можно и анимацию In делать.
            //getView().prepareForIn();
        }
    }

    public void onNeedToReloadUserList() {
        loadUserList();
    }

    public void onReadyAnimateList() {
        mInOutState = InOutState.INSIDE;

        if (mListAnimationState == AnimationState.WAITING_FOR_TRIGGER) {
            mListAnimationState = AnimationState.SHOULD_START;
            updateView();
        }
    }

    public void onAnimateOutFinished(int userId) {
        mInOutState = InOutState.OUTSIDE;
        getView().moveToRecordsForUser(userId);
    }

    public void onReturnFromRecordsForUser() {
        LogUtils.d("onReturnFromRecordsForUser " + mInOutState);
        if (mInOutState == InOutState.OUTSIDE) {
            mInOutState = InOutState.INNING;
            getView().animateIn(false);
        }
    }

    public void onUserAtPositionClicked(int position) {
        if (mInOutState != InOutState.INSIDE) {
            // TODO: 26.11.2016 сделать функцию isInside
            return;
        }
        List<User> model = getModel();
        if (model == null) {
            return;
        }
        animateOut(model.get(position).getId());
    }

    public void onUserAtPositionLongClicked(int position) {
        if (mInOutState != InOutState.INSIDE) {
            return;
        }
        showDeleteUser(position);
    }

    public void onUserDeleteClicked(int userId) {
        if (mInOutState != InOutState.INSIDE) {
            return;
        }
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
        if (mInOutState != InOutState.INSIDE) {
            return;
        }
        getView().showChooseUser();
    }

    public void onUserDismissed(int position) {
        if (mInOutState != InOutState.INSIDE) {
            return;
        }
        LogUtils.d("UserListPresenter onUserDismissed " + position);
        showDeleteUser(position);
    }

    public void onReload() {
        if (mInOutState != InOutState.INSIDE) {
            return;
        }
        loadUserList();
        updateView();
    }

    public void onUserChosen(int userId) {
        if (mInOutState != InOutState.INSIDE) {
            return;
        }
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

    private void animateOut(int userId) {
        mInOutState = InOutState.OUTING;
        getView().animateOut(userId);
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
