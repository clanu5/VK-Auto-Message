package com.qwert2603.vkautomessage.errors_show;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.util.LogUtils;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class ErrorsShowPresenter extends BasePresenter<String, ErrorsShowView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();

    @Inject
    ErrorsHolder mErrorsHolder;

    @Inject
    DataManager mDataManager;

    public ErrorsShowPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(ErrorsShowPresenter.this);
    }

    @Override
    protected void onUpdateView(@NonNull ErrorsShowView view) {
        String errors = getModel();
        if (errors != null) {
            view.showErrors(errors);
        }
    }

    @Override
    public void bindView(ErrorsShowView view) {
        super.bindView(view);
        if (getModel() == null && mSubscription.isUnsubscribed()) {
            loadErrors();
        }
    }

    @Override
    public void unbindView() {
        mSubscription.unsubscribe();
        super.unbindView();
    }

    public void onClearErrorsClicked() {
        mErrorsHolder.clearErrors();
        loadErrors();
    }

    public void onSendToDeveloperClicked() {
        String errors = getModel();
        mDataManager.sendVkMessage(Const.DEVELOPER_VK_ID, errors, new Object())
                .subscribe(
                        o -> {
                        },
                        LogUtils::e
                );
    }

    private void loadErrors() {
        mSubscription.unsubscribe();
        mSubscription = mErrorsHolder.getErrors()
                .subscribe(
                        this::setModel,
                        throwable -> {
                            mSubscription.unsubscribe();
                            updateView();
                            LogUtils.e(throwable);
                            ErrorsShowView view = getView();
                            if (view != null) {
                                view.showErrorWhileLoadingErrors(throwable.toString());
                            }
                        }
                );
    }
}
