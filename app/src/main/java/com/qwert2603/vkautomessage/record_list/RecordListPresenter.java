package com.qwert2603.vkautomessage.record_list;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.data.DataManager;
import com.qwert2603.vkautomessage.model.entity.Record;
import com.qwert2603.vkautomessage.util.LogUtils;

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
    }

    public void onNewRecordClicked() {
        if (getModel() != null) {
            // TODO: 18.03.2016
        }
    }

    public void onRecordClicked(int recordId) {
        getView().moveToRecordDetails(recordId);
    }

    public void onRecordRemoveClicked(int recordId) {
        // TODO: 18.03.2016
    }

    private void loadRecordList() {
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
                            throwable.printStackTrace();
                        }
                );
    }

}
