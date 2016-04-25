package com.qwert2603.vkautomessage.record_list;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;

public class RecordListPresenter extends BasePresenter<List<Record>, RecordListView> {

    private Subscription mSubscription;

    @Inject
    DataManager mDataManager;

    public RecordListPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(RecordListPresenter.this);
    }

    @Override
    public void bindView(RecordListView view) {
        super.bindView(view);
        if (getModel() == null && mSubscription == null) {
            loadRecordList();
        }
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
                // TODO: 02.04.2016 сделать группировку по пользователям.
                /*
                 * при запуске приложения появляется список пользователей, для которых есть записи.
                 * при нажатии на пользователя открывается список записей для этого пользователя.
                 */
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
        mDataManager
                .getVkUserById(userId)
                .flatMap(
                        user -> {
                            Record record = new Record();
                            record.setMessage("VK Auto Message");
                            record.setUser(user);
                            record.setTime(new Date());
                            return mDataManager.addRecord(record);
                        }
                )
                .subscribe(
                        recordId -> {
                            RecordListView view = getView();
                            if (view != null) {
                                view.moveToRecordDetails(recordId.intValue());
                            }
                        },
                        LogUtils::e);
    }

    public void onRecordAtPositionClicked(int position) {
        getView().moveToRecordDetails(getModel().get(position).getId());
    }

    public void onRecordAtPositionLongClicked(int position) {
        getView().showDeleteRecord(getModel().get(position).getId());
    }

    public void onRecordDeleteClicked(int recordId) {
        int position = getRecordPosition(recordId);
        mDataManager
                .removeRecord(recordId)
                .subscribe(aLong -> {
                    if (getModel().size() > 1) {
                        RecordListView view = getView();
                        if (view != null) {
                            view.notifyItemRemoved(position);
                        }
                    } else {
                        updateView();
                    }
                }, LogUtils::e);
    }

    private void loadRecordList() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        mSubscription = mDataManager
                .getAllRecords()
                .subscribe(
                        records -> RecordListPresenter.this.setModel(records),
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

    private int getRecordPosition(int recordId) {
        List<Record> recordList = getModel();
        for (int i = 0, size = (recordList == null ? 0 : recordList.size()); i < size; i++) {
            if (recordList.get(i).getId() == recordId) {
                return i;
            }
        }
        return -1;
    }

}
