package com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_repeat_type;

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

public class EditRepeatTypeDialog extends BaseDialog<EditRepeatTypePresenter> implements EditRepeatTypeView {

    private static final String repeatTypeKey = "repeatType";
    public static final String EXTRA_REPEAT_TYPE = "com.qwert2603.vkautomessage.EXTRA_REPEAT_TYPE";

    public static EditRepeatTypeDialog newInstance(int repeatType) {
        EditRepeatTypeDialog editPeriodDialog = new EditRepeatTypeDialog();
        Bundle args = new Bundle();
        args.putInt(repeatTypeKey, repeatType);
        editPeriodDialog.setArguments(args);
        return editPeriodDialog;
    }

    @Inject
    EditRepeatTypePresenter mEditRepeatTypePresenter;

    @NonNull
    @Override
    protected EditRepeatTypePresenter getPresenter() {
        return mEditRepeatTypePresenter;
    }

    @Override
    protected void setPresenter(@NonNull EditRepeatTypePresenter presenter) {
        mEditRepeatTypePresenter = presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(EditRepeatTypeDialog.this);
        mEditRepeatTypePresenter.onRepeatTypeChanged(getArguments().getInt(repeatTypeKey));
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.choose_repeat_type)
                .setSingleChoiceItems(R.array.repeat_types, getArguments().getInt(repeatTypeKey), (dialog, which) -> mEditRepeatTypePresenter.onRepeatTypeChanged(which))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.submit, (dialog, which) -> mEditRepeatTypePresenter.onSubmitClicked())
                .create();
    }

    @Override
    public void submitDone(int repeatType) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_REPEAT_TYPE, repeatType);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
