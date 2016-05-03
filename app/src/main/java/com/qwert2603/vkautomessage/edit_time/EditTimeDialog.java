package com.qwert2603.vkautomessage.edit_time;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseDialog;

import javax.inject.Inject;

public class EditTimeDialog extends BaseDialog<EditTimePresenter> implements EditTimeView {

    private static final String minuteAtDayKey = "minuteAtDay";
    public static final String EXTRA_MINUTE_AT_DAY = "com.qwert2603.vkautomessage.EXTRA_MINUTE_AT_DAY";

    public static EditTimeDialog newInstance(int minuteAtDay) {
        EditTimeDialog editTimeDialog = new EditTimeDialog();
        Bundle args = new Bundle();
        args.putInt(minuteAtDayKey, minuteAtDay);
        editTimeDialog.setArguments(args);
        return editTimeDialog;
    }

    @Inject
    EditTimePresenter mEditTimePresenter;

    @NonNull
    @Override
    protected EditTimePresenter getPresenter() {
        return mEditTimePresenter;
    }

    @Override
    protected void setPresenter(@NonNull EditTimePresenter presenter) {
        mEditTimePresenter = presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(EditTimeDialog.this);
        mEditTimePresenter.setMinuteAtDay(getArguments().getInt(minuteAtDayKey));
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(),
                (view, hourOfDay, minute) -> mEditTimePresenter.onSubmitClicked(hourOfDay, minute),
                mEditTimePresenter.getHours(), mEditTimePresenter.getMinutes(), true);
    }

    @Override
    public void submitDone(long minuteAtDay) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_MINUTE_AT_DAY, minuteAtDay);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
