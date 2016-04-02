package com.qwert2603.vkautomessage.delete_record;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseDialog;

public class DeleteRecordDialog extends BaseDialog<DeleteRecordPresenter> implements DeleteRecordView {

    private static final String recordIdKey = "recordId";
    public static final String EXTRA_RECORD_TO_DELETE_ID = "com.qwert2603.vkautomessage.EXTRA_RECORD_TO_DELETE_ID";

    public static DeleteRecordDialog newInstance(int recordId) {
        DeleteRecordDialog deleteRecordDialog = new DeleteRecordDialog();
        Bundle args = new Bundle();
        args.putInt(recordIdKey, recordId);
        deleteRecordDialog.setArguments(args);
        return deleteRecordDialog;
    }

    private TextView mUserNameTextView;
    private TextView mMessageTextView;

    @NonNull
    @Override
    protected DeleteRecordPresenter createPresenter() {
        return new DeleteRecordPresenter(getArguments().getInt(recordIdKey));
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_record, null);
        mUserNameTextView = (TextView) view.findViewById(R.id.user_name_text_view);
        mMessageTextView = (TextView) view.findViewById(R.id.message_text_view);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.submit, (dialog, which) -> getPresenter().onSubmitClicked())
                .create();
    }

    @Override
    public void showUserName(String userName) {
        mUserNameTextView.setText(userName);
    }

    @Override
    public void showMessage(String message) {
        mMessageTextView.setText(message);
    }

    @Override
    public void showEmpty() {
        mUserNameTextView.setText(R.string.loading);
        mMessageTextView.setText(R.string.loading);
    }

    @Override
    public void submitDone(int recordId) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RECORD_TO_DELETE_ID, recordId);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}