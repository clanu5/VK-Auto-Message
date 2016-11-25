package com.qwert2603.vkautomessage.delete_user;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.qwert2603.vkautomessage.util.StringUtils;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class DeleteUserPresenter extends BasePresenter<User, DeleteUserView> {
    private Subscription mSubscription = Subscriptions.unsubscribed();

    @Inject
    DataManager mDataManager;

    public DeleteUserPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(DeleteUserPresenter.this);
    }

    public void setUserId(int userId) {
        mSubscription.unsubscribe();
        mSubscription = mDataManager
                .getUserById(userId)
                .subscribe(
                        user -> DeleteUserPresenter.this.setModel(user),
                        throwable -> {
                            mSubscription.unsubscribe();
                            LogUtils.e(throwable);
                        }
                );
    }

    @Override
    protected void onUpdateView(@NonNull DeleteUserView view) {
        User user = getModel();
        if (user == null) {
            view.showLoading();
            return;
        }
        view.showUserName(StringUtils.getUserName(user));
        view.showRecordsCount(user.getRecordsCount());
        view.showEnabledRecordsCount(user.getEnabledRecordsCount());
    }

    @Override
    public void unbindView() {
        mSubscription.unsubscribe();
        super.unbindView();
    }

    public void onSubmitClicked() {
        User user = getModel();
        if (user == null) {
            return;
        }
        getView().submitResult(true, user.getId());
    }

    public void onCancelClicked() {
        User user = getModel();
        if (user == null) {
            return;
        }
        getView().submitResult(false, user.getId());
    }
}
