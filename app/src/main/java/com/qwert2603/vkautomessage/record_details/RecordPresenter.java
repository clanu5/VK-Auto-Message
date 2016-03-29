package com.qwert2603.vkautomessage.record_details;

import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.Date;

import rx.Subscription;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;

public class RecordPresenter extends BasePresenter<Record, RecordView> {

    private Subscription mSubscription;

    public RecordPresenter(int recordId) {
        mSubscription = DataManager.getInstance()
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

    public RecordPresenter(Record record) {
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
    protected void onUpdateView(@NonNull RecordView view) {
        Record record = getModel();
        if (record == null) {
            view.showLoading();
            return;
        }
        DataManager.getInstance()
                .getPhotoByUrl(record.getUser().photo_100)
                .subscribe(
                        photo -> {
                            RecordView recordView = getView();
                            if (recordView != null) {
                                recordView.showPhoto(photo);
                            }
                        },
                        LogUtils::e
                );
        view.showPhoto(null);
        view.showUserName(getUserName(record.getUser()));
        view.showMessage(record.getMessage());
        view.showEnabled(record.isEnabled());
        view.showTime(getTimeString());
    }

    public int getModelId() {
        return getModel() == null ? -1 : getModel().getId();
    }

    public void onUserChosen(int userId) {
        if (getModel().getUser().id != userId) {
            DataManager.getInstance()
                    .getVkUserById(userId)
                    .subscribe(
                            user -> {
                                getModel().setUser(user);
                                updateView();
                                DataManager.getInstance().justUpdateRecord(getModel());
                            },
                            LogUtils::e
                    );
        }
    }

    public void onTimeEdited(long time) {
        if (getModel().getTime().getTime() != time) {
            getModel().setTime(new Date(time));
            getView().showTime(getTimeString());
            DataManager.getInstance().justUpdateRecord(getModel());
        }
    }

    public void onEnableClicked(boolean enable) {
        if (getModel().isEnabled() != enable) {
            getModel().setIsEnabled(enable);
            DataManager.getInstance().justUpdateRecord(getModel());
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
            DataManager.getInstance().justUpdateRecord(getModel());
        }
    }

    public void onChooseUserClicked() {
        getView().showChooseUser(getModel().getUser().id);
    }

    public void onEditMessageClicked() {
        getView().showEditMessage(getModel().getMessage());
    }

    public void onChooseTimeClicked() {
        getView().showEditTime(getModel().getTime().getTime());
    }

    private String getTimeString() {
        return DateFormat.format("kk:mm", getModel().getTime()).toString();
    }
}
