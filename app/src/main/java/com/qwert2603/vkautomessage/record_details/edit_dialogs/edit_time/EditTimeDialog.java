package com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_time;

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

    private static final String hourKey = "hour";
    private static final String minuteKey = "minuteAtDay";

    public static final String EXTRA_HOUR = "com.qwert2603.vkautomessage.EXTRA_HOUR";
    public static final String EXTRA_MINUTE = "com.qwert2603.vkautomessage.EXTRA_MINUTE";

    public static EditTimeDialog newInstance(int hour, int minute) {
        EditTimeDialog editTimeDialog = new EditTimeDialog();
        Bundle args = new Bundle();
        args.putInt(hourKey, hour);
        args.putInt(minuteKey, minute);
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
        mEditTimePresenter.setHour(getArguments().getInt(hourKey));
        mEditTimePresenter.setMinute(getArguments().getInt(minuteKey));
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(),
                (view, hourOfDay, minute) -> mEditTimePresenter.onSubmitClicked(hourOfDay, minute),
                mEditTimePresenter.getHours(), mEditTimePresenter.getMinutes(), true);
    }

    @Override
    public void submitDone(int hour, int minute) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_HOUR, hour);
        intent.putExtra(EXTRA_MINUTE, minute);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
