package com.qwert2603.vkautomessage.edit_time;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import com.qwert2603.vkautomessage.base.BaseDialog;
import com.qwert2603.vkautomessage.base.BasePresenter;

public class EditTimeDialog extends BaseDialog implements EditTimeView {

    private static final String timeInMillisKey = "timeInMillis";
    public static final String EXTRA_TIME_IN_MILLIS = "com.qwert2603.vkautomessage.EXTRA_TIME_IN_MILLIS";

    public static EditTimeDialog newInstance(long timeInMillis) {
        EditTimeDialog editTimeDialog = new EditTimeDialog();
        Bundle args = new Bundle();
        args.putLong(timeInMillisKey, timeInMillis);
        editTimeDialog.setArguments(args);
        return editTimeDialog;
    }

    private EditTimePresenter mEditTimePresenter;

    @Override
    protected BasePresenter getPresenter() {
        if (mEditTimePresenter == null) {
            mEditTimePresenter = new EditTimePresenter(getArguments().getLong(timeInMillisKey));
        }
        return mEditTimePresenter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(),
                (view, hourOfDay, minute) -> mEditTimePresenter.onSubmitClicked(hourOfDay, minute),
                mEditTimePresenter.getHours(), mEditTimePresenter.getMinutes(), true);
    }

    @Override
    public void submitDone(long timeInMillis) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME_IN_MILLIS, timeInMillis);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
