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

import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class RecordListPresenter extends ListPresenter<Record, RecordListWithUser, RecordListView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();
    private Subscription mRxBusSubscription = Subscriptions.unsubscribed();

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
                        model.mUser.setRecordsCount(recordWithUser.mUser.getRecordsCount());
                        model.mUser.setEnabledRecordsCount(recordWithUser.mUser.getEnabledRecordsCount());
                        RecordListView view1 = getView();
                        if (view1 != null) {
                            showUserRecordsCount(model.mUser, view1, true);
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
            showUserRecordsCount(user, view, false);
            ImageView photoImageView = view.getUserPhotoImageView();
            if (photoImageView != null) {
                ImageLoader.getInstance().displayImage(user.getPhoto(), photoImageView);
            }
        }
    }

    private void showUserRecordsCount(@NonNull User user, @NonNull RecordListView view, boolean updated) {
        view.showRecordsCount(user.getRecordsCount(), user.getEnabledRecordsCount(), updated);
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
                                if (canUpdateView()) {
                                    getView().notifyItemChanged(recordPosition);
                                    showUserRecordsCount(model.mUser, getView(), false);
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
                    showUserRecordsCount(user, view, true);

                    view.moveToDetailsForItem(record.getId(), true, recordList.size() - 1);
                }, t -> {
                    RecordListView view = getView();
                    if (view != null) {
                        view.enableUI();
                    }
                    LogUtils.e(t);
                });
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
        showUserRecordsCount(user, view, true);
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
