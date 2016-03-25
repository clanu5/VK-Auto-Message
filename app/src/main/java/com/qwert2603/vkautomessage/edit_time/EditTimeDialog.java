package com.qwert2603.vkautomessage.edit_time;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

public class EditTimeDialog extends DialogFragment implements EditTimeView {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEditTimePresenter = new EditTimePresenter(getArguments().getLong(timeInMillisKey));
        mEditTimePresenter.bindView(this);
    }

    @Override
    public void onDestroy() {
        mEditTimePresenter.unbindView();
        super.onDestroy();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(),
                (view, hourOfDay, minute) -> mEditTimePresenter.onSubmitClicked(hourOfDay, minute),
                mEditTimePresenter.getHours(), mEditTimePresenter.getMinutes(), true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mEditTimePresenter.onViewReady();
    }

    @Override
    public void onStop() {
        mEditTimePresenter.onViewNotReady();
        super.onStop();
    }

    @Override
    public void submitDone(long timeInMillis) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME_IN_MILLIS, timeInMillis);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
