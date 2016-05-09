package com.qwert2603.vkautomessage.record_details;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseFragment;
import com.qwert2603.vkautomessage.edit_message.EditMessageDialog;
import com.qwert2603.vkautomessage.edit_period.EditPeriodDialog;
import com.qwert2603.vkautomessage.edit_time.EditTimeDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordFragment extends BaseFragment<RecordPresenter> implements RecordView {

    private static final String recordIdKey = "recordId";

    private static final int REQUEST_EDIT_MESSAGE = 1;
    private static final int REQUEST_EDIT_TIME = 2;
    private static final int REQUEST_EDIT_PERIOD = 3;

    public static RecordFragment newInstance(int recordId) {
        RecordFragment recordFragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putInt(recordIdKey, recordId);
        recordFragment.setArguments(args);
        return recordFragment;
    }

    @BindView(R.id.photo_image_view)
    ImageView mPhotoImageView;

    @BindView(R.id.user_name_text_view)
    TextView mUsernameTextView;

    @BindView(R.id.enable_switch)
    Switch mEnableSwitch;

    @BindView(R.id.message_text_view)
    TextView mMessageTextView;

    @BindView(R.id.time_text_view)
    TextView mTimeTextView;

    @BindView(R.id.period_text_view)
    TextView mPeriodTextView;

    @BindView(R.id.user_card)
    CardView mUserCardView;

    @BindView(R.id.message_card)
    CardView mMessageCardView;

    @BindView(R.id.time_card)
    CardView mTimeCardView;

    @BindView(R.id.period_card)
    CardView mPeriodCardView;

    @Inject
    RecordPresenter mRecordPresenter;

    @NonNull
    @Override
    protected RecordPresenter getPresenter() {
        return mRecordPresenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(RecordFragment.this);
        mRecordPresenter.setRecordId(getArguments().getInt(recordIdKey));
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_details, container, false);

        ButterKnife.bind(RecordFragment.this, view);

        mEnableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> mRecordPresenter.onEnableClicked(isChecked));
        mMessageCardView.setOnClickListener(v -> mRecordPresenter.onEditMessageClicked());
        mTimeCardView.setOnClickListener(v -> mRecordPresenter.onEditTimeClicked());
        mPeriodCardView.setOnClickListener(v -> mRecordPresenter.onEditPeriodClicked());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_EDIT_MESSAGE:
                String message = data.getStringExtra(EditMessageDialog.EXTRA_MESSAGE);
                mRecordPresenter.onMessageEdited(message);
                break;
            case REQUEST_EDIT_TIME:
                int minuteAtDay = data.getIntExtra(EditTimeDialog.EXTRA_MINUTE_AT_DAY, 0);
                mRecordPresenter.onTimeEdited(minuteAtDay);
                break;
            case REQUEST_EDIT_PERIOD:
                int period = data.getIntExtra(EditPeriodDialog.EXTRA_PERIOD, 0);
                mRecordPresenter.onPeriodEdited(period);
                break;
        }
    }

    @Override
    public ImageView getPhotoImageView() {
        return mPhotoImageView;
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
    public void showPeriod(int period) {
        mPeriodTextView.setText(getResources().getQuantityString(R.plurals.hours, period, period));
    }

    @Override
    public void showLoading() {
        mPhotoImageView.setImageBitmap(null);
        mUsernameTextView.setText(R.string.loading);
        mMessageTextView.setText(R.string.loading);
        mTimeTextView.setText(R.string.loading);
    }

    @Override
    public void showEditMessage(String message) {
        EditMessageDialog editMessageDialog = EditMessageDialog.newInstance(message);
        editMessageDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_MESSAGE);
        editMessageDialog.show(getFragmentManager(), editMessageDialog.getClass().getName());
    }

    @Override
    public void showEditTime(int minuteAtDay) {
        EditTimeDialog editTimeDialog = EditTimeDialog.newInstance(minuteAtDay);
        editTimeDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_TIME);
        editTimeDialog.show(getFragmentManager(), editTimeDialog.getClass().getName());
    }

    @Override
    public void showEditPeriod(int period) {
        EditPeriodDialog editPeriodDialog = EditPeriodDialog.newInstance(period);
        editPeriodDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_PERIOD);
        editPeriodDialog.show(getFragmentManager(), editPeriodDialog.getClass().getName());
    }

    @Override
    public void showToast(int stringRes) {
        Toast.makeText(getActivity(), stringRes, Toast.LENGTH_SHORT).show();
    }
}
