package com.qwert2603.vkautomessage.delete_user;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.delete_item.DeleteItemDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DeleteUserDialog extends DeleteItemDialog<DeleteUserPresenter> implements DeleteUserView {

    private static final String userIdKey = "userId";

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
    protected AlertDialog.Builder modifyDialog(AlertDialog.Builder builder) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_user, null);

        ButterKnife.bind(DeleteUserDialog.this, view);

        return builder.setView(view);
    }

    @Override
    public void showLoading() {
        mUserNameTextView.setText(R.string.loading);
        mRecordsTextView.setText(R.string.loading);
        mEnabledRecordsTextView.setText(R.string.loading);
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

}
