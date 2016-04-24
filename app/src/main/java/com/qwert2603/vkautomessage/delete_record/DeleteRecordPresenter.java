package com.qwert2603.vkautomessage.delete_record;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.util.LogUtils;

import javax.inject.Inject;

import rx.Subscription;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;
import static com.qwert2603.vkautomessage.util.StringUtils.noMore;

public class DeleteRecordPresenter extends BasePresenter<Record, DeleteRecordView> {

    private static final int MESSAGE_LENGTH_LIMIT = 52;

    private Subscription mSubscription;

    @Inject
    DataManager mDataManager;

    public DeleteRecordPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(DeleteRecordPresenter.this);
    }

    public void setRecordId(int recordId) {
        mSubscription = mDataManager
                .getRecordById(recordId)
                .subscribe(
                        record -> DeleteRecordPresenter.this.setModel(record),
                        throwable -> {
                            if (mSubscription != null) {
                                mSubscription.unsubscribe();
                                mSubscription = null;
                            }
                            LogUtils.e(throwable);
                        }
                );
    }

    @Override
    protected void onUpdateView(@NonNull DeleteRecordView view) {
        Record record = getModel();
        if (record == null) {
            view.showEmpty();
            return;
        }
        view.showUserName(getUserName(record.getUser()));
        view.showMessage(noMore(record.getMessage(), MESSAGE_LENGTH_LIMIT));
    }

    @Override
    public void unbindView() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        super.unbindView();
    }

    public void onSubmitClicked() {
        Record record = getModel();
        if (record != null) {
            getView().submitDone(getModel().getId());
        }
    }
}
