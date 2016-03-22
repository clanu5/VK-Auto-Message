package com.qwert2603.vkautomessage.record_details;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.entity.Record;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.qwert2603.vkautomessage.util.StringUtils;

import java.text.DateFormat;
import java.util.Date;

import rx.Subscription;

public class RecordPresenter extends BasePresenter<Record, RecordView> {

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
            return;
        }
        DataManager.getInstance()
                .getPhotoByUrl(record.getUser().photo_100)
                .subscribe(
                        view::showPhoto,
                        LogUtils::e
                );
        view.showUserName(StringUtils.getUserName(record.getUser()));
        view.showMessage(record.getMessage());
        view.showEnabled(record.isEnabled());
        String timeString = DateFormat.getTimeInstance().format(record.getTime());
        view.showTime(timeString);
    }

    public int getModelId() {
        return getModel() == null ? -1 : getModel().getId();
    }

    public void onUserChosen(int userId) {
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

    public void onTimeChosen(Date time) {
        getModel().setTime(time);
        DataManager.getInstance().justUpdateRecord(getModel());
    }

    public void onEnableClicked(boolean enable) {
        getModel().setIsEnabled(enable);
        DataManager.getInstance().justUpdateRecord(getModel());
    }

    public void onMessageEdited(String message) {
        getModel().setMessage(message);
        DataManager.getInstance().justUpdateRecord(getModel());
    }

    public void onChooseUserClicked() {
        getView().showChooseUser(getModelId());
    }

    public void onChooseTimeClicked() {
        getView().showChooseTime();
    }
}
