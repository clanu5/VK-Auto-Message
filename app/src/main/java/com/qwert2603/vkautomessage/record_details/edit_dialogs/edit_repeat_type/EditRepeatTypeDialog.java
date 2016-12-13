package com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_repeat_type;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @BindView(R.id.repeat_type_radio_group)
    RadioGroup mRadioGroup;

    private String[] mRepeatTypes;

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
        mEditRepeatTypePresenter.setRepeatType(getArguments().getInt(repeatTypeKey));
        super.onCreate(savedInstanceState);
        mRepeatTypes = getResources().getStringArray(R.array.repeat_types);
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_repeat_type, null);

        ButterKnife.bind(EditRepeatTypeDialog.this, view);


        for (String s : mRepeatTypes) {
            AppCompatRadioButton radioButton = new AppCompatRadioButton(getActivity());
            radioButton.setText(s);
            mRadioGroup.addView(radioButton);
            radioButton.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        mRadioGroup.requestLayout();

        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < mRepeatTypes.length; i++) {
                if (mRadioGroup.getChildAt(i).getId() == checkedId) {
                    mEditRepeatTypePresenter.onRepeatTypeChanged(i);
                    break;
                }
            }
        });

        // TODO: 10.12.2016
        // use AlertDialog#setItems
        // https://developer.android.com/guide/topics/ui/dialogs.html
        // во всех диалогах

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.submit, (dialog, which) -> mEditRepeatTypePresenter.onSubmitClicked())
                .create();
    }

    @Override
    public void setRepeatType(int repeatType) {
        int id = mRadioGroup.getChildAt(repeatType).getId();
        if (id != mRadioGroup.getCheckedRadioButtonId()) {
            mRadioGroup.check(id);
        }
    }

    @Override
    public void submitDone(int repeatType) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_REPEAT_TYPE, repeatType);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
