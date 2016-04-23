package com.qwert2603.vkautomessage.record_details;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseFragment;
import com.qwert2603.vkautomessage.edit_message.EditMessageDialog;
import com.qwert2603.vkautomessage.edit_time.EditTimeDialog;
import com.qwert2603.vkautomessage.user_list.UserListDialog;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecordFragment extends BaseFragment<RecordPresenter> implements RecordView {

    private static final String recordIdKey = "recordId";

    private static final int REQUEST_CHOOSE_USER = 1;
    private static final int REQUEST_EDIT_MESSAGE = 2;
    private static final int REQUEST_EDIT_TIME = 3;

    public static RecordFragment newInstance(int recordId) {
        RecordFragment recordFragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putInt(recordIdKey, recordId);
        recordFragment.setArguments(args);
        return recordFragment;
    }

    @Bind(R.id.photo_image_view)
    ImageView mPhotoImageView;

    @Bind(R.id.user_name_text_view)
    TextView mUsernameTextView;

    @Bind(R.id.enable_switch)
    Switch mEnableSwitch;

    @Bind(R.id.message_text_view)
    TextView mMessageTextView;

    @Bind(R.id.time_text_view)
    TextView mTimeTextView;

    @Bind(R.id.user_card)
    CardView userCardView;

    @Bind(R.id.message_card)
    CardView messageCardView;

    @Bind(R.id.time_card)
    CardView timeCardView;

    @Override
    protected RecordPresenter createPresenter() {
        return new RecordPresenter(getArguments().getInt(recordIdKey));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        ButterKnife.bind(RecordFragment.this, view);

        userCardView.setOnClickListener(v -> getPresenter().onChooseUserClicked());
        mEnableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> getPresenter().onEnableClicked(isChecked));
        messageCardView.setOnClickListener(v -> getPresenter().onEditMessageClicked());
        timeCardView.setOnClickListener(v -> getPresenter().onChooseTimeClicked());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CHOOSE_USER:
                int userId = data.getIntExtra(UserListDialog.EXTRA_SELECTED_USER_ID, 0);
                getPresenter().onUserChosen(userId);
                break;
            case REQUEST_EDIT_MESSAGE:
                String message = data.getStringExtra(EditMessageDialog.EXTRA_MESSAGE);
                getPresenter().onMessageEdited(message);
                break;
            case REQUEST_EDIT_TIME:
                long currentTimeInMillis = data.getLongExtra(EditTimeDialog.EXTRA_TIME_IN_MILLIS, 0);
                getPresenter().onTimeEdited(currentTimeInMillis);
                break;
        }
    }

    @Override
    public void showPhoto(Bitmap photo) {
        mPhotoImageView.setImageBitmap(photo);
    }

    @Override
    public void showUserName(String userName) {
        mUsernameTextView.setText(userName);
    }

    @Override
    public void showMessage(String message) {
        mMessageTextView.setText(message);
    }

    @Override
    public void showEnabled(boolean enabled) {
        mEnableSwitch.setChecked(enabled);
    }

    @Override
    public void showTime(String time) {
        mTimeTextView.setText(time);
    }

    @Override
    public void showLoading() {
        mPhotoImageView.setImageBitmap(null);
        mUsernameTextView.setText(R.string.loading);
        mMessageTextView.setText(R.string.loading);
        mTimeTextView.setText(R.string.loading);
    }

    @Override
    public void showChooseUser(int currentUserId) {
        UserListDialog userListDialog = UserListDialog.newInstance(currentUserId);
        userListDialog.setTargetFragment(RecordFragment.this, REQUEST_CHOOSE_USER);
        userListDialog.show(getFragmentManager(), userListDialog.getClass().getName());
    }

    @Override
    public void showEditMessage(String message) {
        EditMessageDialog editMessageDialog = EditMessageDialog.newInstance(message);
        editMessageDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_MESSAGE);
        editMessageDialog.show(getFragmentManager(), editMessageDialog.getClass().getName());
    }

    @Override
    public void showEditTime(long currentTimeInMillis) {
        EditTimeDialog editTimeDialog = EditTimeDialog.newInstance(currentTimeInMillis);
        editTimeDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_TIME);
        editTimeDialog.show(getFragmentManager(), editTimeDialog.getClass().getName());
    }

    @Override
    public void showToast(int stringRes) {
        Toast.makeText(getActivity(), stringRes, Toast.LENGTH_SHORT).show();
    }
}
