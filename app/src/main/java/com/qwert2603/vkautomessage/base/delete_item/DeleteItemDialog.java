package com.qwert2603.vkautomessage.base.delete_item;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseDialog;

public abstract class DeleteItemDialog<P extends DeleteItemPresenter> extends BaseDialog<P> implements DeleteItemView {

    public static final String EXTRA_ITEM_TO_DELETE_ID = "com.qwert2603.vkautomessage.EXTRA_ITEM_TO_DELETE_ID";

    private boolean mSubmitResultSent = false;

    protected abstract AlertDialog.Builder modifyDialog(AlertDialog.Builder builder);

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setNegativeButton(R.string.cancel, (dialog, which) -> getPresenter().onCancelClicked())
                .setPositiveButton(R.string.submit, (dialog, which) -> getPresenter().onSubmitClicked());

        builder = modifyDialog(builder);
        return builder.create();
    }

    @Override
    public void submitResult(boolean submit, int id) {
        mSubmitResultSent = true;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ITEM_TO_DELETE_ID, id);
        getTargetFragment().onActivityResult(getTargetRequestCode(), submit ? Activity.RESULT_OK : Activity.RESULT_CANCELED, intent);
    }

    @Override
    public void onDestroy() {
        if (!mSubmitResultSent) {
            getPresenter().onCancelClicked();
        }
        super.onDestroy();
    }

}
