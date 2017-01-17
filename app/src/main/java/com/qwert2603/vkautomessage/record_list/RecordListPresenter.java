package com.qwert2603.vkautomessage.record_list;

import android.support.annotation.NonNull;
import android.widget.ImageView;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class RecordListPresenter extends ListPresenter<Record, RecordListWithUser, RecordListView> {

    public static final int FILTER_ENABLED = 1 << 1;
    public static final int FILTER_DISABLED = 1 << 2;

    public static final int FILTER_PERIODICALLY_BY_HOURS = 1 << 3;
    public static final int FILTER_DAYS_IN_WEEK = 1 << 4;
    public static final int FILTER_DAY_IN_YEAR = 1 << 5;

    public static final int NO_FILTER = FILTER_ENABLED | FILTER_DISABLED | FILTER_PERIODICALLY_BY_HOURS | FILTER_DAYS_IN_WEEK | FILTER_DAY_IN_YEAR;

    private Subscription mSubscription = Subscriptions.unsubscribed();
    private Subscription mRxBusSubscription = Subscriptions.unsubscribed();

    private int mPrevRecordsCount = -1;
    private int mPrevEnabledRecordsCount = -1;

    @Inject
    DataManager mDataManager;

    private int mUserId;

    @Inject
    RxBus mRxBus;

    private int mFilterState = NO_FILTER;

    @Override
    protected Transformer<RecordListWithUser, List<Record>> listFromModel() {
        return recordListWithUser -> recordListWithUser == null ? null : recordListWithUser.mRecordList;
    }

    @Override
    protected Transformer<RecordListWithUser, List<Record>> showingListFromModel() {
        return recordListWithUser -> {
            if (recordListWithUser == null) {
                return null;
            }
            if (mFilterState == NO_FILTER) {
                return recordListWithUser.mRecordList;
            }
            List<Record> showingList = new ArrayList<>();
            for (Record record : recordListWithUser.mRecordList) {
                if (record.isEnabled() && (mFilterState & FILTER_ENABLED) == 0)
                    continue;
                if (!record.isEnabled() && (mFilterState & FILTER_DISABLED) == 0)
                    continue;
                if (record.getRepeatType() == Record.REPEAT_TYPE_HOURS_IN_DAY && (mFilterState & FILTER_PERIODICALLY_BY_HOURS) == 0)
                    continue;
                if (record.getRepeatType() == Record.REPEAT_TYPE_DAYS_IN_WEEK && (mFilterState & FILTER_DAYS_IN_WEEK) == 0)
                    continue;
                if (record.getRepeatType() == Record.REPEAT_TYPE_DAY_IN_YEAR && (mFilterState & FILTER_DAY_IN_YEAR) == 0)
                    continue;
                showingList.add(record);
            }
            return showingList;
        };
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
    protected boolean isSearching() {
        return super.isSearching() || mFilterState != NO_FILTER;
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
                        int recordPosition = getItemPosition(recordWithUser.mRecord.getId());
                        model.mRecordList.set(recordPosition, recordWithUser.mRecord);
                        model.mUser.setRecordsCount(recordWithUser.mUser.getRecordsCount());
                        model.mUser.setEnabledRecordsCount(recordWithUser.mUser.getEnabledRecordsCount());
                        if (canUpdateView()) {
                            showUserRecordsCount(model.mUser, getView());
                            getView().updateItem(getShowingItemPosition(recordWithUser.mRecord.getId()));
                            // TODO: 15.01.2017 this updateItem cancels checkBox animation
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
                Picasso.with(photoImageView.getContext()).load(user.getPhoto()).into(photoImageView);
            }
        } else {
            view.showLoadingUserInfo();
        }
    }

    @Override
    public void onViewNotReady() {
        super.onViewNotReady();
        RecordListView view = getView();
        if (view != null && view.getUserPhotoImageView() != null) {
            Picasso.with(view.getUserPhotoImageView().getContext()).cancelRequest(view.getUserPhotoImageView());
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
                            int recordPosition = getItemPosition(recordWithUser.mRecord.getId());
                            if (recordPosition != -1) {
                                LogUtils.d("doLoadItem " + id + " %% " + recordWithUser);
                                model.mUser.setRecordsCount(recordWithUser.mUser.getRecordsCount());
                                model.mUser.setEnabledRecordsCount(recordWithUser.mUser.getEnabledRecordsCount());
                                model.mRecordList.set(recordPosition, recordWithUser.mRecord);
                                LogUtils.d("doLoadItem canUpdateView() ==" + canUpdateView());
                                if (canUpdateView()) {
                                    getView().updateItem(getShowingItemPosition(recordWithUser.mRecord.getId()));
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
                    updateShowingList();

                    User user = model.mUser;
                    user.setRecordsCount(user.getRecordsCount() + 1);
                    showUserRecordsCount(user, view);

                    int showingItemPosition = getShowingItemPosition(record.getId());
                    view.moveToDetailsForItem(record.getId(), showingItemPosition >= 0, showingItemPosition);
                }, LogUtils::e);
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
        Record record = getShowingList().get(position);
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
        int position = getItemPosition(id);
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
        updateShowingList();

        mDataManager.removeRecord(id)
                .subscribe(aLong -> {
                }, LogUtils::e);
    }

    public void onResetFilterClicked() {
        if (mFilterState == NO_FILTER) return;
        mFilterState = NO_FILTER;
        updateShowingList();
        getView().scrollToTop();
    }

    public void onFilterStateChanged(int filterParam, boolean enabled) {
        int newFilterState = mFilterState;
        if (enabled) {
            newFilterState |= filterParam;
        } else {
            newFilterState &= ~(filterParam);
        }
        if (newFilterState == mFilterState) return;
        mFilterState = newFilterState;
        updateShowingList();
        getView().scrollToTop();
    }

    public int getFilterState() {
        return mFilterState;
    }

}
