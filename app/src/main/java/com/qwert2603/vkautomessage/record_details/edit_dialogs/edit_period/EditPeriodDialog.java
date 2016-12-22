package com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_period;

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
import com.qwert2603.vkautomessage.model.Record;

import javax.inject.Inject;

public class EditPeriodDialog extends BaseDialog<EditPeriodPresenter> implements EditPeriodView {

    private static final String periodKey = "period";
    public static final String EXTRA_PERIOD = "com.qwert2603.vkautomessage.EXTRA_PERIOD";

    public static EditPeriodDialog newInstance(int period) {
        EditPeriodDialog editPeriodDialog = new EditPeriodDialog();
        Bundle args = new Bundle();
        args.putInt(periodKey, period);
        editPeriodDialog.setArguments(args);
        return editPeriodDialog;
    }

    @Inject
    EditPeriodPresenter mEditPeriodPresenter;

    @NonNull
    @Override
    protected EditPeriodPresenter getPresenter() {
        return mEditPeriodPresenter;
    }

    @Override
    protected void setPresenter(@NonNull EditPeriodPresenter presenter) {
        mEditPeriodPresenter = presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(EditPeriodDialog.this);
        mEditPeriodPresenter.setPeriod(getArguments().getInt(periodKey));
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] periods = new String[Record.PERIODS.length];
        for (int i = 0; i < Record.PERIODS.length; i++) {
            periods[i] = getContext().getResources().getQuantityString(R.plurals.hours, Record.PERIODS[i], Record.PERIODS[i]);
        }
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.choose_period)
                .setSingleChoiceItems(periods, mEditPeriodPresenter.getSelectedPeriodPosition(), (dialog, which) -> mEditPeriodPresenter.onPeriodChanged(Record.PERIODS[which]))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.submit, (dialog, which) -> mEditPeriodPresenter.onSubmitClicked())
                .create();
    }

    @Override
    public void submitDone(int period) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PERIOD, period);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
