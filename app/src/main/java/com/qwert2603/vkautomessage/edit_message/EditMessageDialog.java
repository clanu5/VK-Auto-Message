package com.qwert2603.vkautomessage.edit_message;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseDialog;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditMessageDialog extends BaseDialog<EditMessagePresenter> implements EditMessageView {

    private static final String messageKey = "message";
    public static final String EXTRA_MESSAGE = "com.qwert2603.vkautomessage.EXTRA_MESSAGE";

    public static EditMessageDialog newInstance(String message) {
        EditMessageDialog editMessageDialog = new EditMessageDialog();
        Bundle args = new Bundle();
        args.putString(messageKey, message);
        editMessageDialog.setArguments(args);
        return editMessageDialog;
    }

    @Bind(R.id.message_edit_text)
    EditText mMessageEditText;

    @Inject
    EditMessagePresenter mEditMessagePresenter;

    @NonNull
    @Override
    protected EditMessagePresenter getPresenter() {
        return mEditMessagePresenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(EditMessageDialog.this);
        mEditMessagePresenter.setMessage(getArguments().getString(messageKey));
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_message, null);

        ButterKnife.bind(EditMessageDialog.this, view);

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditMessagePresenter.onMessageEdited(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(R.string.submit, (dialog, which) -> {
                    mEditMessagePresenter.onSubmitClicked();
                })
                .create();
    }

    @Override
    public void setMessage(String message) {
        if (! message.equals(mMessageEditText.getText().toString())) {
            mMessageEditText.setText(message);
        }
    }

    @Override
    public void submitDone(String message) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_MESSAGE, message);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
