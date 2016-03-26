package com.qwert2603.vkautomessage.delete_record;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;

public class DeleteRecordDialog extends DialogFragment implements DeleteRecordView {
    // TODO: 26.03.2016  использовать RecordPresenter

    private static final String recordIdKey = "recordId";
    public static final String EXTRA_RECORD_TO_DELETE_ID = "com.qwert2603.vkautomessage.EXTRA_RECORD_TO_DELETE_ID";

    public static DeleteRecordDialog newInstance(int recordId) {
        DeleteRecordDialog deleteRecordDialog = new DeleteRecordDialog();
        Bundle args = new Bundle();
        args.putInt(recordIdKey, recordId);
        deleteRecordDialog.setArguments(args);
        return deleteRecordDialog;
    }

    private DeleteRecordPresenter mDeleteRecordPresenter;

    private TextView mUserNameTextView;
    private TextView mMessageTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeleteRecordPresenter = new DeleteRecordPresenter(getArguments().getInt(recordIdKey));
        mDeleteRecordPresenter.bindView(this);
    }

    @Override
    public void onDestroy() {
        mDeleteRecordPresenter.unbindView();
        super.onDestroy();
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
                .setPositiveButton(R.string.submit, (dialog, which) -> mDeleteRecordPresenter.onSubmitClicked())
                .create();
    }


    @Override
    public void onStart() {
        super.onStart();
        mDeleteRecordPresenter.onViewReady();
    }

    @Override
    public void onStop() {
        mDeleteRecordPresenter.onViewNotReady();
        super.onStop();
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
    public void submitDone(int recordId) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RECORD_TO_DELETE_ID, recordId);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
