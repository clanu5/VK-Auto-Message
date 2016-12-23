package com.qwert2603.vkautomessage.record_details;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.model.RecordWithUser;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.qwert2603.vkautomessage.util.StringUtils;

import java.util.Locale;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;

public class RecordPresenter extends BasePresenter<RecordWithUser, RecordView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();

    @Inject
    Context mAppContext;

    @Inject
    DataManager mDataManager;

    @Inject
    RxBus mRxBus;

    private String[] mRepeatTypes;
    private String[] mMonths;
    private String[] mDaysOfWeek;

    public RecordPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(RecordPresenter.this);
    }

    public void setRecordId(int recordId) {
        setModel(null);
        mSubscription.unsubscribe();
        mSubscription = mDataManager.getRecordById(recordId)
                .subscribe(
                        model -> RecordPresenter.this.setModel(model),
                        throwable -> {
                            mSubscription.unsubscribe();
                            LogUtils.e(throwable);
                        }
                );
    }

    public void setRecord(RecordWithUser recordWithUser) {
        setModel(recordWithUser);
    }

    @Override
    public void bindView(RecordView view) {
        super.bindView(view);
        mRepeatTypes = mAppContext.getResources().getStringArray(R.array.repeat_types);
        mMonths = mAppContext.getResources().getStringArray(R.array.months);
        mDaysOfWeek = mAppContext.getResources().getStringArray(R.array.days_of_week_short);
    }

    @Override
    public void unbindView() {
        mSubscription.unsubscribe();
        super.unbindView();
    }

    @Override
    public void onViewNotReady() {
        RecordView view = getView();
        if (view != null && view.getPhotoImageView() != null) {
            ImageLoader.getInstance().cancelDisplayTask(view.getPhotoImageView());
        }
        super.onViewNotReady();
    }

    @Override
    protected void onUpdateView(@NonNull RecordView view) {
        RecordWithUser recordWithUser = getModel();
        if (recordWithUser == null) {
            view.showLoading();
            return;
        }
        User user = recordWithUser.mUser;
        Record record = recordWithUser.mRecord;

        ImageView photoImageView = view.getPhotoImageView();
        if (photoImageView != null) {
            ImageLoader.getInstance().displayImage(user.getPhoto(), photoImageView);
        }
        view.showUserName(getUserName(user));
        view.showMessage(record.getMessage());
        view.showEnabled(record.isEnabled());
        view.showTime(getTimeString());
        showRepeatTypeAndInfo();
    }

    private void showRepeatTypeAndInfo() {
        RecordWithUser model = getModel();
        RecordView view = getView();
        if (model == null || view == null) {
            return;
        }
        Record record = model.mRecord;
        view.showRepeatType(mRepeatTypes[record.getRepeatType()]);
        switch (record.getRepeatType()) {
            case Record.REPEAT_TYPE_HOURS_IN_DAY:
                int period = record.getPeriod();
                view.showRepeatInfo(mAppContext.getResources().getQuantityString(R.plurals.hours, period, period));
                break;
            case Record.REPEAT_TYPE_DAYS_IN_WEEK:
                if (record.getDaysInWeekCount() == Const.DAYS_PER_WEEK) {
                    view.showRepeatInfo(mAppContext.getString(R.string.all_days));
                    break;
                }
                if (record.getDaysInWeekCount() == Const.DAYS_PER_WEEK - 1) {
                    for (int i = 1; i < Const.DAYS_PER_WEEK + 1; i++) {
                        if (!record.isDayOfWeekEnabled(i)) {
                            view.showRepeatInfo(mAppContext.getString(R.string.all_except, mDaysOfWeek[i - 1]));
                            break;
                        }
                    }
                    break;
                }
                String delimiter = ", ";
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 1; i < Const.DAYS_PER_WEEK + 1; i++) {
                    if (record.isDayOfWeekEnabled(i)) {
                        stringBuilder.append(mDaysOfWeek[i - 1]).append(delimiter);
                    }
                }
                int length = stringBuilder.length();
                stringBuilder.delete(length - delimiter.length(), length);
                view.showRepeatInfo(stringBuilder.toString());
                break;
            case Record.REPEAT_TYPE_DAY_IN_YEAR:
                view.showRepeatInfo(String.format(Locale.getDefault(),
                        "%s, %d", mMonths[record.getMonth()], record.getDayOfMonth()));
                break;
        }
    }

    public void onTimeEdited(int hour, int minute) {
        RecordWithUser model = getModel();
        if (model == null) {
            return;
        }
        Record record = model.mRecord;
        if (hour != record.getHour() || minute != record.getMinute()) {
            record.setHour(hour);
            record.setMinute(minute);
            getView().showTime(getTimeString());
            mDataManager.onRecordUpdated(model.mRecord);
        }
    }

    public void onEnableClicked(boolean enable) {
        RecordWithUser model = getModel();
        if (model == null) {
            return;
        }
        Record record = model.mRecord;
        if (record.isEnabled() != enable) {
            record.setEnabled(enable);

            int enabledRecordsCount = model.mUser.getEnabledRecordsCount();
            enabledRecordsCount += record.isEnabled() ? 1 : -1;
            model.mUser.setEnabledRecordsCount(enabledRecordsCount);

            mRxBus.send(new RxBus.Event(RxBus.Event.EVENT_RECORD_ENABLED_CHANGED, model));

            mDataManager.onRecordUpdated(record);
            mDataManager.onUserUpdated(model.mUser);
        }
    }

    public void onMessageEdited(String message) {
        RecordWithUser model = getModel();
        if (model == null) {
            return;
        }
        if (message.isEmpty()) {
            getView().showToast(R.string.empty_message_toast);
            return;
        }
        if (message.equals(Const.MODE_SHOW_ERRORS_ON)) {
            mRxBus.send(new RxBus.Event(RxBus.Event.EVENT_MODE_SHOW_ERRORS_CHANGED, true));
        } else if (message.equals(Const.MODE_SHOW_ERRORS_OFF)) {
            mRxBus.send(new RxBus.Event(RxBus.Event.EVENT_MODE_SHOW_ERRORS_CHANGED, false));
        }
        Record record = model.mRecord;
        if (!record.getMessage().equals(message)) {
            record.setMessage(message);
            getView().showMessage(message);
            mDataManager.onRecordUpdated(record);
        }
    }

    public void onRepeatTypeEdited(int repeatType) {
        RecordWithUser model = getModel();
        if (model == null) {
            return;
        }
        Record record = model.mRecord;
        if (record.getRepeatType() != repeatType) {
            record.setRepeatType(repeatType);
            showRepeatTypeAndInfo();
            mDataManager.onRecordUpdated(record);
        }
    }

    public void onPeriodEdited(int period) {
        RecordWithUser model = getModel();
        if (model == null) {
            return;
        }
        Record record = model.mRecord;
        if (record.getRepeatType() == Record.REPEAT_TYPE_HOURS_IN_DAY && record.getPeriod() != period) {
            record.setPeriod(period);
            showRepeatTypeAndInfo();
            mDataManager.onRecordUpdated(record);
        }
    }

    public void onDaysInWeekEdited(int daysInWeek) {
        RecordWithUser model = getModel();
        if (model == null) {
            return;
        }
        if (daysInWeek == 0) {
            getView().showToast(R.string.no_days_in_week_selected_toast);
            return;
        }
        Record record = model.mRecord;
        if (record.getRepeatType() == Record.REPEAT_TYPE_DAYS_IN_WEEK && record.getDaysInWeek() != daysInWeek) {
            record.setDaysOfWeek(daysInWeek);
            showRepeatTypeAndInfo();
            mDataManager.onRecordUpdated(record);
        }
    }

    public void onDayInYearEdited(int month, int dayOfMonth) {
        RecordWithUser model = getModel();
        if (model == null) {
            return;
        }
        Record record = model.mRecord;
        if (record.getRepeatType() == Record.REPEAT_TYPE_DAY_IN_YEAR
                && (record.getMonth() != month || record.getDayOfMonth() != dayOfMonth)) {
            record.setMonth(month);
            record.setDayOfMonth(dayOfMonth);
            showRepeatTypeAndInfo();
            mDataManager.onRecordUpdated(record);
        }
    }

    public void onUserClicked() {
        getView().showToast(R.string.receiver_of_message);
    }

    public void onEditMessageClicked() {
        RecordWithUser model = getModel();
        if (model == null) {
            return;
        }
        getView().showEditMessage(model.mRecord.getMessage());
    }

    public void onEditTimeClicked() {
        RecordWithUser model = getModel();
        if (model == null) {
            return;
        }
        Record record = model.mRecord;
        getView().showEditTime(record.getHour(), record.getMinute());
    }

    public void onEditRepeatTypeClicked() {
        RecordWithUser model = getModel();
        if (model == null) {
            return;
        }
        Record record = model.mRecord;
        getView().showEditRepeatType(record.getRepeatType());
    }

    public void onEditRepeatInfoClicked() {
        RecordWithUser model = getModel();
        if (model == null) {
            return;
        }
        Record record = model.mRecord;
        switch (record.getRepeatType()) {
            case Record.REPEAT_TYPE_HOURS_IN_DAY:
                getView().showEditPeriod(record.getPeriod());
                break;
            case Record.REPEAT_TYPE_DAYS_IN_WEEK:
                getView().showEditDaysInWeek(record.getDaysInWeek());
                break;
            case Record.REPEAT_TYPE_DAY_IN_YEAR:
                getView().showEditDayInYear(record.getMonth(), record.getDayOfMonth());
                break;
        }
    }

    private String getTimeString() {
        RecordWithUser model = getModel();
        return model == null ? "" : StringUtils.getRecordTime(model.mRecord);
    }
}
