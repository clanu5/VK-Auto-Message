package com.qwert2603.vkautomessage.record_details;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.entity.Record;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.qwert2603.vkautomessage.util.StringUtils;
import com.vk.sdk.api.model.VKApiUserFull;

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
                            updateView();
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
        // TODO: 18.03.2016  view.showPhoto();
        view.showUserName(StringUtils.getUserName(record.getUser()));
        view.showMessage(record.getMessage());
        view.showEnabled(record.isEnabled());
        String timeString = DateFormat.getTimeInstance().format(record.getTime());
        view.showTime(timeString);
    }

    public int getModelId() {
        return getModel() == null ? -1 : getModel().getId();
    }

    public void onUserChosen(VKApiUserFull user) {
        getModel().setUser(user);
        DataManager.getInstance().justUpdateRecord(getModel());
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
        getView().showChooseUser();
    }

    public void onChooseTimeClicked() {
        getView().showChooseTime();
    }
}
