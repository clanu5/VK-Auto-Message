package com.qwert2603.vkautomessage.record_list;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.list.ListPresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.model.RecordListWithUser;
import com.qwert2603.vkautomessage.model.RecordWithUser;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.qwert2603.vkautomessage.util.StringUtils;

import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class RecordListPresenter extends ListPresenter<Record, RecordListWithUser, RecordListView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();
    private Subscription mRxBusSubscription = Subscriptions.unsubscribed();

    private int mPrevRecordsCount = -1;
    private int mPrevEnabledRecordsCount = -1;

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
    public void bindView(RecordListView view) {
        super.bindView(view);
        mRxBusSubscription = mRxBus.toObservable()
                .filter(event -> event.mEvent == RxBus.Event.EVENT_RECORD_ENABLED_CHANGED)
                .subscribe(event -> {
                    if (!(event.mObject instanceof RecordWithUser)) {
                        return;
                    }
                    RecordListWithUser model = getModel();
                    if (model == null) {
                        return;
                    }
                    RecordWithUser recordWithUser = (RecordWithUser) event.mObject;
                    if (model.mUser.getId() == recordWithUser.mUser.getId()) {
                        LogUtils.d("EVENT_RECORD_ENABLED_CHANGED " + recordWithUser);
                        int recordPosition = getRecordPosition(recordWithUser.mRecord.getId());
                        model.mRecordList.set(recordPosition, recordWithUser.mRecord);
                        model.mUser.setRecordsCount(recordWithUser.mUser.getRecordsCount());
                        model.mUser.setEnabledRecordsCount(recordWithUser.mUser.getEnabledRecordsCount());
                        if (canUpdateView()) {
                            showUserRecordsCount(model.mUser, getView());
                        }
                    }
                }, LogUtils::e);
    }

    @Override
    public void unbindView() {
        mRxBusSubscription.unsubscribe();
        mRxBusSubscription = Subscriptions.unsubscribed();
        mSubscription.unsubscribe();
        super.unbindView();
    }

    @Override
    protected void onUpdateView(@NonNull RecordListView view) {
        super.onUpdateView(view);
        RecordListWithUser recordListWithUser = getModel();
        if (recordListWithUser != null) {
            User user = recordListWithUser.mUser;
            view.setUser(user);
            view.showUserName(StringUtils.getUserName(user));
            showUserRecordsCount(user, view);
            ImageView photoImageView = view.getUserPhotoImageView();
            if (photoImageView != null) {
                ImageLoader.getInstance().displayImage(user.getPhoto(), photoImageView);
            }
        }
    }

    private void showUserRecordsCount(@NonNull User user, @NonNull RecordListView view) {
        view.showRecordsCount(user.getRecordsCount(), user.getEnabledRecordsCount());
    }

    @Override
    protected void doLoadList() {
        mSubscription.unsubscribe();
        mSubscription = mDataManager.getRecordsForUser(mUserId).subscribe(
                model -> RecordListPresenter.this.setModel(model),
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
                                LogUtils.d("doLoadItem " + id + " %% " + recordWithUser);
                                model.mUser.setRecordsCount(recordWithUser.mUser.getRecordsCount());
                                model.mUser.setEnabledRecordsCount(recordWithUser.mUser.getEnabledRecordsCount());
                                model.mRecordList.set(recordPosition, recordWithUser.mRecord);
                                LogUtils.d("doLoadItem canUpdateView() ==" + canUpdateView());
                                notifyItemChanged(recordPosition);
                                if (canUpdateView()) {
                                    showUserRecordsCount(model.mUser, getView());
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

    @Override
    protected Observable<Void> removeItem(int id) {
        return mDataManager.removeRecord(id);
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
        getView().disableUI();
        Record record = new Record(recordListWithUser.mUser.getId());
        mDataManager.addRecord(record)
                .subscribe(aVoid -> {
                    RecordListWithUser model = getModel();
                    RecordListView view = getView();
                    if (model == null || view == null) {
                        return;
                    }

                    List<Record> recordList = model.mRecordList;
                    recordList.add(record);
                    view.showList(recordList);

                    User user = model.mUser;
                    user.setRecordsCount(user.getRecordsCount() + 1);
                    showUserRecordsCount(user, view);

                    view.moveToDetailsForItem(record.getId(), true, recordList.size() - 1);
                }, t -> {
                    RecordListView view = getView();
                    if (view != null) {
                        view.enableUI();
                    }
                    LogUtils.e(t);
                });
    }

    @Override
    public void onDeleteSelectedClicked() {
        // TODO: 24.12.2016 disable records (& enable if undo)
        LogUtils.d("onDeleteSelectedClicked " + mSelectedIds);
        User user = getModel().mUser;
        mPrevRecordsCount = user.getRecordsCount();
        mPrevEnabledRecordsCount = user.getEnabledRecordsCount();
        for (Record record : getModel().mRecordList) {
            if (mSelectedIds.contains(record.getId())) {
                user.setRecordsCount(user.getRecordsCount() - 1);
                if (record.isEnabled()) {
                    user.setEnabledRecordsCount(user.getEnabledRecordsCount() - 1);
                }
            }
        }
        super.onDeleteSelectedClicked();
    }

    @Override
    public void onUndoDeletionClicked() {
        if (mPrevRecordsCount != -1) {
            getModel().mUser.setRecordsCount(mPrevRecordsCount);
            getModel().mUser.setEnabledRecordsCount(mPrevEnabledRecordsCount);
            mPrevRecordsCount = -1;
            mPrevEnabledRecordsCount = -1;
        }
        super.onUndoDeletionClicked();
    }

    @Override
    public void onItemDismissed(int position) {
        Record record = getList().get(position);
        LogUtils.d("onItemDismissed " + position + " " + record);

        User user = getModel().mUser;
        user.setRecordsCount(user.getRecordsCount() - 1);
        if (record.isEnabled()) {
            user.setEnabledRecordsCount(user.getEnabledRecordsCount() - 1);
        }
        super.onItemDismissed(position);
    }

    public void onItemDeleteSubmitted(int id) {
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
        showUserRecordsCount(user, view);
        recordList.remove(position);
        if (recordList.size() > 0) {
            view.showList(recordList);
        } else {
            view.showEmpty();
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
