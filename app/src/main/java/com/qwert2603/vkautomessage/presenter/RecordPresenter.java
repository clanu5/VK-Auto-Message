package com.qwert2603.vkautomessage.presenter;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.model.data.DataManager;
import com.qwert2603.vkautomessage.model.entity.Record;
import com.qwert2603.vkautomessage.util.StringUtils;
import com.qwert2603.vkautomessage.view.RecordView;
import com.vk.sdk.api.model.VKApiUserFull;

import java.text.DateFormat;
import java.util.Date;

import rx.Subscription;

public class RecordPresenter extends BasePresenter<Record, RecordView> {

    private Subscription mSubscription;

    public void setModelId(int recordId) {
        mSubscription = DataManager.getInstance().getRecordById(recordId).subscribe(
                this::setModel,
                throwable -> {
                    if (mSubscription != null) {
                        mSubscription.unsubscribe();
                        mSubscription = null;
                    }
                    updateView();
                    throwable.printStackTrace();
                }
        );
    }

    public int getModelId() {
        return getModel().getId();
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

    public void onUserChosen(VKApiUserFull user) {
        getModel().setUser(user);
    }

    public void onTimeChosen(Date time) {
        getModel().setTime(time);
    }

    public void onEnableClicked(boolean enable) {
        getModel().setIsEnabled(enable);
    }

    public void onMessageEdited(String message) {
        getModel().setMessage(message);
    }

    public void onChooseUserClicked() {
        getView().showChooseUser();
    }

    public void onChooseTimeClicked() {
        getView().showChooseTime();
    }
}
