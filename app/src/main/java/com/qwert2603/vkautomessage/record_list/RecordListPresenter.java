package com.qwert2603.vkautomessage.record_list;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.model.RecordListWithUser;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;

public class RecordListPresenter extends BasePresenter<RecordListWithUser, RecordListView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();

    @Inject
    DataManager mDataManager;

    private int mUserId;

    @Inject
    RxBus mRxBus;

    public RecordListPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(RecordListPresenter.this);
        mRxBus.toObservable()
                .subscribe(event -> {
                    RecordListWithUser model = getModel();
                    RecordListView view = getView();
                    if (model == null || view == null) {
                        return;
                    }
                    User user = model.mUser;
                    if (event.mEvent == RxBus.Event.EVENT_RECORD_ENABLED_CHANGED) {
                        if (event.mObject instanceof Record) {
                            Record record = (Record) event.mObject;
                            if (record.getUserId() == user.getId()) {
                                int enabledRecordsCount = user.getEnabledRecordsCount();
                                enabledRecordsCount += record.isEnabled() ? 1 : -1;
                                user.setEnabledRecordsCount(enabledRecordsCount);
                                showUserNameAndRecordsCount(user, view);
                            }
                        }
                    }
                }, LogUtils::e);
    }

    public void setUserId(int userId) {
        setModel(null);
        mSubscription.unsubscribe();
        mUserId = userId;
        loadRecordList();
    }

    @Override
    public void unbindView() {
        mSubscription.unsubscribe();
        super.unbindView();
    }

    @Override
    protected void onUpdateView(@NonNull RecordListView view) {
        RecordListWithUser recordListWithUser = getModel();
        if (recordListWithUser == null) {
            if (mSubscription.isUnsubscribed()) {
                view.showError();
            } else {
                view.showLoading();
            }
        } else {
            List<Record> recordList = recordListWithUser.mRecordList;
            if (recordList.isEmpty()) {
                view.showEmpty();
            } else {
                view.showList(recordList);
            }
            showUserNameAndRecordsCount(recordListWithUser.mUser, view);
        }
    }

    private void showUserNameAndRecordsCount(@NonNull User user, @NonNull RecordListView view) {
        view.showUserName(String.format(Locale.getDefault(),
                "(%d/%d) %s", user.getEnabledRecordsCount(), user.getRecordsCount(), getUserName(user)));
    }

    public void onReload() {
        loadRecordList();
        updateView();
    }

    public void onResume() {
        loadRecordList();
    }

    public void onNewRecordClicked() {
        RecordListWithUser recordListWithUser = getModel();
        if (recordListWithUser == null) {
            return;
        }
        if (recordListWithUser.mUser.getId() == Const.DEVELOPER_VK_ID) {
            if (new Random().nextBoolean()) {
                getView().showDontWriteToDeveloper();
                return;
            }
        }
        Record record = new Record(recordListWithUser.mUser.getId());
        mDataManager.addRecord(record)
                .subscribe(aVoid -> {
                    RecordListWithUser model = getModel();
                    RecordListView view = getView();
                    if (model == null || view == null) {
                        return;
                    }
                    List<Record> recordList = model.mRecordList;
                    User user = model.mUser;
                    recordList.add(record);
                    user.setRecordsCount(user.getRecordsCount() + 1);
                    if (recordList.size() > 1) {
                        showUserNameAndRecordsCount(user, view);
                        view.notifyItemInserted(recordList.size() - 1);
                    } else {
                        updateView();
                    }
                    view.moveToRecordDetails(record.getId());
                }, LogUtils::e);
    }

    public void onRecordAtPositionClicked(int position) {
        RecordListWithUser model = getModel();
        if (model == null) {
            return;
        }
        getView().moveToRecordDetails(model.mRecordList.get(position).getId());
    }

    public void onRecordAtPositionLongClicked(int position) {
        showDeleteRecord(position);
    }

    public void onRecordDeleteClicked(int recordId) {
        getView().showRecordSelected(-1);
        int position = getRecordPosition(recordId);
        mDataManager.removeRecord(recordId)
                .subscribe(aLong -> {
                    RecordListWithUser model = getModel();
                    RecordListView view = getView();
                    if (model == null || view == null) {
                        return;
                    }
                    List<Record> recordList = model.mRecordList;
                    User user = model.mUser;
                    user.setRecordsCount(user.getRecordsCount() - 1);
                    if (recordList.get(position).isEnabled()) {
                        user.setEnabledRecordsCount(user.getEnabledRecordsCount() - 1);
                    }
                    recordList.remove(position);
                    if (recordList.size() > 0) {
                        showUserNameAndRecordsCount(user, view);
                        view.notifyItemRemoved(position);
                    } else {
                        updateView();
                    }
                }, LogUtils::e);
    }

    public void onRecordDeleteCanceled(int recordId) {
        getView().showRecordSelected(-1);
    }

    public void onRecordDismissed(int position) {
        showDeleteRecord(position);
    }

    private void loadRecordList() {
        mSubscription.unsubscribe();
        mSubscription = mDataManager.getRecordsForUser(mUserId)
                .subscribe(
                        recordListWithUser -> RecordListPresenter.this.setModel(recordListWithUser),
                        throwable -> {
                            mSubscription.unsubscribe();
                            updateView();
                            LogUtils.e(throwable);
                        }
                );
    }

    private void showDeleteRecord(int position) {
        RecordListWithUser model = getModel();
        if (model == null) {
            return;
        }
        getView().showDeleteRecord(model.mRecordList.get(position).getId());
        getView().showRecordSelected(position);
    }

    private int getRecordPosition(int recordId) {
        List<Record> recordList = getModel().mRecordList;
        for (int i = 0, size = recordList.size(); i < size; ++i) {
            if (recordList.get(i).getId() == recordId) {
                return i;
            }
        }
        return -1;
    }

}
