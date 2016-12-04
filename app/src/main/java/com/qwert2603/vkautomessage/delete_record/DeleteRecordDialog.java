package com.qwert2603.vkautomessage.delete_record;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.delete_item.DeleteItemDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeleteRecordDialog extends DeleteItemDialog<DeleteRecordPresenter> implements DeleteRecordView {

    private static final String recordIdKey = "recordId";

    public static DeleteRecordDialog newInstance(int recordId) {
        DeleteRecordDialog deleteRecordDialog = new DeleteRecordDialog();
        Bundle args = new Bundle();
        args.putInt(recordIdKey, recordId);
        deleteRecordDialog.setArguments(args);
        return deleteRecordDialog;
    }

    @BindView(R.id.user_name_text_view)
    TextView mUserNameTextView;

    @BindView(R.id.message_text_view)
    TextView mMessageTextView;

    @Inject
    DeleteRecordPresenter mDeleteRecordPresenter;

    @NonNull
    @Override
    protected DeleteRecordPresenter getPresenter() {
        return mDeleteRecordPresenter;
    }

    @Override
    protected void setPresenter(@NonNull DeleteRecordPresenter presenter) {
        mDeleteRecordPresenter = presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(DeleteRecordDialog.this);
        mDeleteRecordPresenter.setRecordId(getArguments().getInt(recordIdKey));
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @Override
    protected AlertDialog.Builder modifyDialog(AlertDialog.Builder builder) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_record, null);

        ButterKnife.bind(DeleteRecordDialog.this, view);

        return builder.setView(view);
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
    public void showLoading() {
        mUserNameTextView.setText(R.string.loading);
        mMessageTextView.setText(R.string.loading);
    }

}
