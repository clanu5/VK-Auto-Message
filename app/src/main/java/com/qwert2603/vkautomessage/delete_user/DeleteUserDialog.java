package com.qwert2603.vkautomessage.delete_user;

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
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DeleteUserDialog extends BaseDialog<DeleteUserPresenter> implements DeleteUserView {

    private static final String userIdKey = "userId";
    public static final String EXTRA_USER_TO_DELETE_ID = "com.qwert2603.vkautomessage.EXTRA_USER_TO_DELETE_ID";

    public static DeleteUserDialog newInstance(int userId) {
        DeleteUserDialog deleteRecordDialog = new DeleteUserDialog();
        Bundle args = new Bundle();
        args.putInt(userIdKey, userId);
        deleteRecordDialog.setArguments(args);
        return deleteRecordDialog;
    }

    @Inject
    DeleteUserPresenter mDeleteUserPresenter;

    @BindView(R.id.user_name_text_view)
    TextView mUserNameTextView;

    @BindView(R.id.enabled_records_count_text_view)
    TextView mEnabledRecordsTextView;

    @BindView(R.id.records_count_text_view)
    TextView mRecordsTextView;

    @NonNull
    @Override
    protected DeleteUserPresenter getPresenter() {
        return mDeleteUserPresenter;
    }

    @Override
    protected void setPresenter(@NonNull DeleteUserPresenter presenter) {
        mDeleteUserPresenter = presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(DeleteUserDialog.this);
        mDeleteUserPresenter.setUserId(getArguments().getInt(userIdKey));
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_user, null);

        ButterKnife.bind(DeleteUserDialog.this, view);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.submit, (dialog, which) -> mDeleteUserPresenter.onSubmitClicked())
                .create();
    }

    @Override
    public void showLoading() {
        mUserNameTextView.setText(R.string.loading);
        mRecordsTextView.setText(R.string.loading);
    }

    @Override
    public void showUserName(String userName) {
        mUserNameTextView.setText(userName);
    }

    @Override
    public void showRecordsCount(int recordsCount) {
        mRecordsTextView.setText(getResources()
                .getQuantityString(R.plurals.records, recordsCount, recordsCount));
    }

    @Override
    public void showEnabledRecordsCount(int enabledRecordsCount) {
        mEnabledRecordsTextView.setText(getResources()
                .getQuantityString(R.plurals.enabled_records, enabledRecordsCount, enabledRecordsCount));
    }

    @Override
    public void submitDone(int userId) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_TO_DELETE_ID, userId);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
