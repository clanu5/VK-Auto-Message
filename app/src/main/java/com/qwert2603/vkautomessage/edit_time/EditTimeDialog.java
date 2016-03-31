package com.qwert2603.vkautomessage.edit_time;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.base.BaseDialog;

public class EditTimeDialog extends BaseDialog<EditTimePresenter> implements EditTimeView {

    private static final String timeInMillisKey = "timeInMillis";
    public static final String EXTRA_TIME_IN_MILLIS = "com.qwert2603.vkautomessage.EXTRA_TIME_IN_MILLIS";

    public static EditTimeDialog newInstance(long timeInMillis) {
        EditTimeDialog editTimeDialog = new EditTimeDialog();
        Bundle args = new Bundle();
        args.putLong(timeInMillisKey, timeInMillis);
        editTimeDialog.setArguments(args);
        return editTimeDialog;
    }

    @NonNull
    @Override
    protected EditTimePresenter createPresenter() {
        return new EditTimePresenter(getArguments().getLong(timeInMillisKey));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(),
                (view, hourOfDay, minute) -> getPresenter().onSubmitClicked(hourOfDay, minute),
                getPresenter().getHours(), getPresenter().getMinutes(), true);
    }

    @Override
    public void submitDone(long timeInMillis) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME_IN_MILLIS, timeInMillis);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
