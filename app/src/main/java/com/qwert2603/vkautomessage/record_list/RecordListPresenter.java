package com.qwert2603.vkautomessage.record_list;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.Date;
import java.util.List;

import rx.Subscription;

public class RecordListPresenter extends BasePresenter<List<Record>, RecordListView> {

    private Subscription mSubscription;

    public RecordListPresenter() {
        loadRecordList();
    }

    @Override
    public void unbindView() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        super.unbindView();
    }

    @Override
    protected void onUpdateView(@NonNull RecordListView view) {
        List<Record> recordList = getModel();
        if (recordList == null) {
            if (mSubscription == null) {
                view.showError();
            } else {
                view.showLoading();
            }
        } else {
            if (recordList.isEmpty()) {
                view.showEmpty();
            } else {
                view.showList(recordList);
            }
        }
    }

    public void onReload() {
        loadRecordList();
        updateView();
    }

    public void onResume() {
        updateView();
    }

    public void onNewRecordClicked() {
        getView().showChooseUser(0);
    }

    public void onUserForNewRecordChosen(int userId) {
        DataManager.getInstance()
                .getVkUserById(userId)
                .flatMap(
                        user -> {
                            Record record = new Record();
                            record.setMessage("VK Auto Message");
                            record.setUser(user);
                            record.setTime(new Date());
                            return DataManager.getInstance().addRecord(record);
                        }
                )
                .subscribe(
                        recordId -> {
                            RecordListView recordListView = getView();
                            if (recordListView != null) {
                                recordListView.moveToRecordDetails(recordId.intValue());
                            }
                        },
                        LogUtils::e);
    }

    public void onRecordClicked(int recordId) {
        getView().moveToRecordDetails(recordId);
    }

    public void onRecordLongClicked(int recordId) {
        getView().showDeleteRecord(recordId);
    }

    public void onRecordDeleteClicked(int recordId) {
        DataManager.getInstance()
                .removeRecord(recordId)
                .subscribe(
                        aLong -> {
                            updateView();
                        },
                        LogUtils::e
                );
    }

    private void loadRecordList() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        mSubscription = DataManager.getInstance()
                .getAllRecords()
                .subscribe(
                        records -> {
                            RecordListPresenter.this.setModel(records);
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

}
