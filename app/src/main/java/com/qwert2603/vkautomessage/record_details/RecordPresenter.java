package com.qwert2603.vkautomessage.record_details;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.entity.Record;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.text.DateFormat;
import java.util.Date;

import rx.Subscription;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;
import static com.qwert2603.vkautomessage.util.StringUtils.noMore;

public class RecordPresenter extends BasePresenter<Record, RecordView> {

    private static final int USERNAME_LENGTH_LIMIT = 26;

    private Subscription mSubscription;

    public RecordPresenter(int recordId) {
        mSubscription = DataManager.getInstance()
                .getRecordById(recordId)
                .subscribe(
                        model -> {
                            RecordPresenter.this.setModel(model);
                        },
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
            String loading = "Loading..";
            view.showMessage(loading);
            view.showUserName(loading);
            view.showTime(loading);
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
        view.showUserName(noMore(getUserName(record.getUser()), USERNAME_LENGTH_LIMIT));
        view.showMessage(record.getMessage());
        view.showEnabled(record.isEnabled());
        String timeString = DateFormat.getTimeInstance().format(record.getTime());
        view.showTime(timeString);
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

    public void onTimeChosen(Date time) {
        if (getModel().getTime().getTime() != time.getTime()) {
            getModel().setTime(time);
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
        if (!getModel().getMessage().equals(message)) {
            getModel().setMessage(message);
            DataManager.getInstance().justUpdateRecord(getModel());
            getView().showMessage(message);
        }
    }

    public void onChooseUserClicked() {
        getView().showChooseUser(getModel().getUser().id);
    }

    public void onEditMessageClicked() {
        getView().showEditMessage(getModel().getMessage());
    }

    public void onChooseTimeClicked() {
        getView().showChooseTime();
    }
}
