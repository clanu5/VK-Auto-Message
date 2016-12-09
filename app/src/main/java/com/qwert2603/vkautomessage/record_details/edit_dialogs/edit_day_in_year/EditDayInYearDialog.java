package com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_day_in_year;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseDialog;

import java.util.Calendar;

import javax.inject.Inject;

public class EditDayInYearDialog extends BaseDialog<EditDayInYearPresenter> implements EditDayInYearView {

    private static final String monthKey = "month";
    private static final String dayOfMonthKey = "dayOfMonth";

    public static final String EXTRA_MONTH = "com.qwert2603.vkautomessage.EXTRA_MONTH";
    public static final String EXTRA_DAY_OF_MONTH = "com.qwert2603.vkautomessage.EXTRA_DAY_OF_MONTH";

    public static EditDayInYearDialog newInstance(int month, int dayOfMonth) {
        EditDayInYearDialog editTimeDialog = new EditDayInYearDialog();
        Bundle args = new Bundle();
        args.putInt(monthKey, month);
        args.putInt(dayOfMonthKey, dayOfMonth);
        editTimeDialog.setArguments(args);
        return editTimeDialog;
    }

    @Inject
    EditDayInYearPresenter mEditDayInYearPresenter;

    @NonNull
    @Override
    protected EditDayInYearPresenter getPresenter() {
        return mEditDayInYearPresenter;
    }

    @Override
    protected void setPresenter(@NonNull EditDayInYearPresenter presenter) {
        mEditDayInYearPresenter = presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(EditDayInYearDialog.this);
        mEditDayInYearPresenter.setMonth(getArguments().getInt(monthKey));
        mEditDayInYearPresenter.setDayOfMonth(getArguments().getInt(dayOfMonthKey));
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        return new DatePickerDialog(getActivity(),
                (view, year, monthOfYear, dayOfMonth) -> mEditDayInYearPresenter.onSubmitClicked(monthOfYear, dayOfMonth),
                calendar.get(Calendar.YEAR), mEditDayInYearPresenter.getMonth(), mEditDayInYearPresenter.getDayOfMonth());
    }

    @Override
    public void submitDone(int month, int dayOfMonth) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_MONTH, month);
        intent.putExtra(EXTRA_DAY_OF_MONTH, dayOfMonth);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
