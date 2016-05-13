package com.qwert2603.vkautomessage.edit_days_in_week;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @BindView(R.id.linear_layout)
    LinearLayout mLinearLayout;

    private List<CheckBox> mCheckBoxList = new ArrayList<>();

    private String[] mDaysOfWeek;

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
        mDaysOfWeek = getResources().getStringArray(R.array.days_of_week);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_days_in_week, null);

        ButterKnife.bind(EditDaysInWeekDialog.this, view);

        /**
         * {@link Calendar#SUNDAY} == 1, поэтому добавим в начало один CheckBox, но не будем его отображать.
         */
        mCheckBoxList.add(new CheckBox(getActivity()));

        for (String s : mDaysOfWeek) {
            CheckBox checkBox = new CheckBox(getActivity());
            checkBox.setText(s);
            mCheckBoxList.add(checkBox);
            mLinearLayout.addView(checkBox);
        }

        for (int i = 1, mCheckBoxListSize = mCheckBoxList.size(); i < mCheckBoxListSize; i++) {
            CheckBox checkBox = mCheckBoxList.get(i);
            final int finalI = i;
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    mEditRepeatTypePresenter.onDayInWeekEnableChanged(finalI, isChecked));
        }

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(R.string.submit, (dialog, which) -> {
                    mEditRepeatTypePresenter.onSubmitClicked();
                })
                .create();
    }

    @Override
    public void setDayInWeekEnable(int dayInWeek, boolean enable) {
        mCheckBoxList.get(dayInWeek).setChecked(enable);
    }

    @Override
    public void submitDone(int repeatType) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DAYS_IN_WEEK, repeatType);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
