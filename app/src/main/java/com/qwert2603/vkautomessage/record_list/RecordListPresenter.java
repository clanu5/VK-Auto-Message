package com.qwert2603.vkautomessage.record_list;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.in_out_animation.ShouldCheckIsInningOrInside;
import com.qwert2603.vkautomessage.base.list.ListPresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.model.RecordListWithUser;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.qwert2603.vkautomessage.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class RecordListPresenter extends ListPresenter<Record, RecordListWithUser, RecordListView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();

    @Inject
    DataManager mDataManager;

    private int mUserId;

    @Inject
    RxBus mRxBus;

    @Override
    protected List<Record> getList() {
        return getModel() == null ? null : getModel().mRecordList;
    }

    @Override
    protected boolean isError() {
        return getModel() == null && mSubscription.isUnsubscribed();
    }

    @Override
    protected boolean isFirstAnimateInWithLargeDelay() {
        return false;
    }

    public RecordListPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(RecordListPresenter.this);
    }

    public void setUserId(int userId) {
        setModel(null);
        mSubscription.unsubscribe();
        mUserId = userId;
        doLoadList();
    }

    @Override
    public void unbindView() {
        mSubscription.unsubscribe();
        super.unbindView();
    }

    @Override
    protected void onUpdateView(@NonNull RecordListView view) {
        super.onUpdateView(view);
        RecordListWithUser recordListWithUser = getModel();
        if (recordListWithUser != null) {
            showUserNameAndRecordsCount(recordListWithUser.mUser, view);
        }
    }

    private void showUserNameAndRecordsCount(@NonNull User user, @NonNull RecordListView view) {
        view.showUserName(String.format(Locale.getDefault(),
                "(%d/%d) %s", user.getEnabledRecordsCount(), user.getRecordsCount(), StringUtils.getUserName(user)));
    }

    @Override
    protected void doLoadList() {
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

    @Override
    protected void doLoadItem(int id) {
        mSubscription.unsubscribe();
        mSubscription = mDataManager.getRecordById(id)
                .subscribe(
                        recordWithUser -> {
                            RecordListWithUser model = getModel();
                            if (model == null) {
                                return;
                            }
                            int recordPosition = getRecordPosition(recordWithUser.mRecord.getId());
                            if (recordPosition != -1) {
                                onRecordEnableChanged(recordPosition, recordWithUser.mRecord.isEnabled());
                                model.mRecordList.set(recordPosition, recordWithUser.mRecord);
                                RecordListView view = getView();
                                if (view != null) {
                                    view.notifyItemsUpdated(Collections.singletonList(recordPosition));
                                }
                            }
                        },
                        throwable -> {
                            mSubscription.unsubscribe();
                            updateView();
                            LogUtils.e(throwable);
                        }
                );
    }

    public void onRecordEnableChanged(int position, boolean enabled) {
        if (getModel().mRecordList.get(position).isEnabled() == enabled) {
            return;
        }
        User user = getModel().mUser;
        int enabledRecordsCount = user.getEnabledRecordsCount();
        enabledRecordsCount += enabled ? 1 : -1;
        user.setEnabledRecordsCount(enabledRecordsCount);
        showUserNameAndRecordsCount(user, getView());
    }

    @ShouldCheckIsInningOrInside
    public void onNewRecordClicked() {
        if (!isInningOrInside()) {
            return;
        }
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

                    showUserNameAndRecordsCount(user, view);

                    view.animateAllItemsEnter(false);
                    view.delayEachItemEnterAnimation(false);
                    if (view.getLastCompletelyVisibleItemPosition() == recordList.size() - 2) {
                        view.notifyItemInserted(recordList.size() - 1, record.getId());
                    }
                    if (recordList.size() == 1) {
                        view.showList(recordList);
                    } else {
                        view.scrollToPosition(recordList.size() - 1);
                    }


                    setItemIdToMove(record.getId(), true);
                    animateOut();
                }, LogUtils::e);
    }

    @ShouldCheckIsInningOrInside
    public void onItemDeleteSubmitted(int id) {
        if (!isInningOrInside()) {
            return;
        }
        super.onItemDeleteSubmitted(id);
        int position = getRecordPosition(id);
        RecordListWithUser model = getModel();
        RecordListView view = getView();
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

        mDataManager.removeRecord(id)
                .subscribe(aLong -> {
                }, LogUtils::e);
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
