package com.qwert2603.vkautomessage.record_details;

import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.qwert2603.vkautomessage.util.StringUtils;

import java.util.Date;

import javax.inject.Inject;

import rx.Subscription;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;

public class RecordPresenter extends BasePresenter<Record, RecordView> {

    private Subscription mSubscription;

    @Inject
    DataManager mDataManager;

    public RecordPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(RecordPresenter.this);
    }

    public void setRecordId(int recordId) {
        setModel(null);
        mSubscription = mDataManager
                .getRecordById(recordId)
                .subscribe(
                        record -> RecordPresenter.this.setModel(record),
                        throwable -> {
                            if (mSubscription != null) {
                                mSubscription.unsubscribe();
                                mSubscription = null;
                            }
                            LogUtils.e(throwable);
                        }
                );
    }

    public void setRecord(Record record) {
        setModel(record);
    }

    @Override
    public void unbindView() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        super.unbindView();
    }

    @Override
    public void onViewNotReady() {
        RecordView view = getView();
        if (view != null) {
            ImageLoader.getInstance().cancelDisplayTask(view.getPhotoImageView());
        }
        super.onViewNotReady();
    }

    @Override
    protected void onUpdateView(@NonNull RecordView view) {
        Record record = getModel();
        if (record == null) {
            view.showLoading();
            return;
        }
        ImageLoader.getInstance().displayImage(record.getUser().photo_100, view.getPhotoImageView());
        view.showUserName(getUserName(record.getUser()));
        view.showMessage(record.getMessage());
        view.showEnabled(record.isEnabled());
        view.showTime(getTimeString());
    }

    public void onUserChosen(int userId) {
        if (getModel().getUserId() != userId) {
            getModel().setUserId(userId);
            updateView();
            mDataManager.onRecordUpdated(getModel());
        }
    }

    public void onTimeEdited(long time) {
        if (getModel().getTime().getTime() != time) {
            getModel().setTime(new Date(time));
            getView().showTime(getTimeString());
            mDataManager.onRecordUpdated(getModel());
        }
    }

    public void onEnableClicked(boolean enable) {
        if (getModel().isEnabled() != enable) {
            getModel().setEnabled(enable);
            mDataManager.onRecordUpdated(getModel());
        }
    }

    public void onMessageEdited(String message) {
        if (message.isEmpty()) {
            getView().showToast(R.string.empty_message_toast);
            return;
        }
        if (!getModel().getMessage().equals(message)) {
            getModel().setMessage(message);
            getView().showMessage(message);
            mDataManager.onRecordUpdated(getModel());
        }
    }

    public void onChooseUserClicked() {
        getView().showChooseUser(getModel().getUserId());
    }

    public void onEditMessageClicked() {
        getView().showEditMessage(getModel().getMessage());
    }

    public void onChooseTimeClicked() {
        getView().showEditTime(getModel().getTime().getTime());
    }

    private String getTimeString() {
        Record record = getModel();
        return StringUtils.getRecordTime(record);
    }
}
