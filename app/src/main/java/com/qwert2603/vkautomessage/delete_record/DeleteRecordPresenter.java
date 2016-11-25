package com.qwert2603.vkautomessage.delete_record;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.RecordWithUser;
import com.qwert2603.vkautomessage.util.LogUtils;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;
import static com.qwert2603.vkautomessage.util.StringUtils.noMore;

public class DeleteRecordPresenter extends BasePresenter<RecordWithUser, DeleteRecordView> {

    private static final int MESSAGE_LENGTH_LIMIT = 52;

    private Subscription mSubscription = Subscriptions.unsubscribed();

    @Inject
    DataManager mDataManager;

    public DeleteRecordPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(DeleteRecordPresenter.this);
    }

    public void setRecordId(int recordId) {
        mSubscription.unsubscribe();
        mSubscription = mDataManager
                .getRecordById(recordId)
                .subscribe(
                        record -> DeleteRecordPresenter.this.setModel(record),
                        throwable -> {
                            mSubscription.unsubscribe();
                            LogUtils.e(throwable);
                        }
                );
    }

    @Override
    protected void onUpdateView(@NonNull DeleteRecordView view) {
        RecordWithUser recordWithUser = getModel();
        if (recordWithUser == null) {
            view.showLoading();
            return;
        }
        view.showUserName(getUserName(recordWithUser.mUser));
        view.showMessage(noMore(recordWithUser.mRecord.getMessage(), MESSAGE_LENGTH_LIMIT));
    }

    @Override
    public void unbindView() {
        mSubscription.unsubscribe();
        super.unbindView();
    }

    public void onSubmitClicked() {
        RecordWithUser model = getModel();
        if (model == null) {
            return;
        }
        getView().submitResult(true, model.mRecord.getId());
    }

    public void onCancelClicked() {
        RecordWithUser model = getModel();
        if (model == null) {
            return;
        }
        getView().submitResult(false, model.mRecord.getId());
    }
}
