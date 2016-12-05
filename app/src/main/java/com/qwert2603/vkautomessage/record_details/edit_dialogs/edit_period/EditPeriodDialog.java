package com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_period;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseDialog;
import com.qwert2603.vkautomessage.model.Record;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @BindView(R.id.period_radio_group)
    RadioGroup mRadioGroup;

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
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_period, null);

        ButterKnife.bind(EditPeriodDialog.this, view);

        for (int i = 0; i < Record.PERIODS.length; i++) {
            int period = Record.PERIODS[i];
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setText(getResources().getQuantityString(R.plurals.hours, period, period));
            mRadioGroup.addView(radioButton);
            radioButton.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        mRadioGroup.requestLayout();

        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < Record.PERIODS.length; i++) {
                if (mRadioGroup.getChildAt(i).getId() == checkedId) {
                    mEditPeriodPresenter.onPeriodChanged(Record.PERIODS[i]);
                    break;
                }
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.submit, (dialog, which) -> {
                    mEditPeriodPresenter.onSubmitClicked();
                })
                .create();
    }

    @Override
    public void setPeriod(int period) {
        int index = Arrays.binarySearch(Record.PERIODS, period);
        if (index < 0) {
            return;
        }
        int checkedId = mRadioGroup.getChildAt(index).getId();
        if (checkedId != mRadioGroup.getCheckedRadioButtonId()) {
            mRadioGroup.check(checkedId);
        }
    }

    @Override
    public void submitDone(int period) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PERIOD, period);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
