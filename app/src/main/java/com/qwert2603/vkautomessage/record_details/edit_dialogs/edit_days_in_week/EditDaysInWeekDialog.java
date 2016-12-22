package com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_days_in_week;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseDialog;

import javax.inject.Inject;

public class EditDaysInWeekDialog extends BaseDialog<EditDaysInWeekPresenter> implements EditDaysInWeekView {

    private static final String daysInWeekKey = "daysInWeek";
    public static final String EXTRA_DAYS_IN_WEEK = "com.qwert2603.vkautomessage.EXTRA_DAYS_IN_WEEK";

    public static EditDaysInWeekDialog newInstance(int daysInWeek) {
        EditDaysInWeekDialog editPeriodDialog = new EditDaysInWeekDialog();
        Bundle args = new Bundle();
        args.putInt(daysInWeekKey, daysInWeek);
        editPeriodDialog.setArguments(args);
        return editPeriodDialog;
    }

    @Inject
    EditDaysInWeekPresenter mEditRepeatTypePresenter;

    @NonNull
    @Override
    protected EditDaysInWeekPresenter getPresenter() {
        return mEditRepeatTypePresenter;
    }

    @Override
    protected void setPresenter(@NonNull EditDaysInWeekPresenter presenter) {
        mEditRepeatTypePresenter = presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(EditDaysInWeekDialog.this);
        mEditRepeatTypePresenter.setDaysInWeek(getArguments().getInt(daysInWeekKey));
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.choose_days_in_week)
                .setMultiChoiceItems(R.array.days_of_week, mEditRepeatTypePresenter.getSelectedDaysInWeek(),
                        (dialog, which, isChecked) -> mEditRepeatTypePresenter.onDayInWeekEnableChanged(which, isChecked))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.submit, (dialog, which) -> mEditRepeatTypePresenter.onSubmitClicked())
                .create();
    }

    @Override
    public void submitDone(int daysInWeek) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DAYS_IN_WEEK, daysInWeek);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
