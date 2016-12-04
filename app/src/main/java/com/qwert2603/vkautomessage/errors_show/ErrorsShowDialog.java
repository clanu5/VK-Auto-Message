package com.qwert2603.vkautomessage.errors_show;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ErrorsShowDialog extends BaseDialog<ErrorsShowPresenter> implements ErrorsShowView {

    public static ErrorsShowDialog newInstance() {
        return new ErrorsShowDialog();
    }

    @Inject
    ErrorsShowPresenter mPresenter;

    @BindView(R.id.errors_text_view)
    TextView mErrorsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(ErrorsShowDialog.this);
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected ErrorsShowPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void setPresenter(@NonNull ErrorsShowPresenter presenter) {
        mPresenter = presenter;
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_errors_show, null);
        ButterKnife.bind(ErrorsShowDialog.this, view);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(R.string.ok, null)
                .setNeutralButton(R.string.send_to_developer,(dialog, which) -> {
                    mPresenter.onSendToDeveloperClicked();
                })
                .setNegativeButton(R.string.clear_errors, (dialog, which) -> {
                    mPresenter.onClearErrorsClicked();
                })
                .create();
    }

    @Override
    public void showErrors(String errors) {
        mErrorsTextView.setText(errors);
    }

    @Override
    public void showErrorWhileLoadingErrors(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }
}
