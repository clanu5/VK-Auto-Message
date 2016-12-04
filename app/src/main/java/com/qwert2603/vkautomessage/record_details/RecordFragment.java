package com.qwert2603.vkautomessage.record_details;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.in_out_animation.AnimationFragment;
import com.qwert2603.vkautomessage.edit_day_in_year.EditDayOfYearDialog;
import com.qwert2603.vkautomessage.edit_days_in_week.EditDaysInWeekDialog;
import com.qwert2603.vkautomessage.edit_message.EditMessageDialog;
import com.qwert2603.vkautomessage.edit_period.EditPeriodDialog;
import com.qwert2603.vkautomessage.edit_repeat_type.EditRepeatTypeDialog;
import com.qwert2603.vkautomessage.edit_time.EditTimeDialog;
import com.qwert2603.vkautomessage.navigation.ActivityInterface;
import com.qwert2603.vkautomessage.navigation.NavigationActivity;
import com.qwert2603.vkautomessage.util.AndroidUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordFragment extends AnimationFragment<RecordPresenter> implements RecordView {

    private static final String recordIdKey = "recordId";
    private static final String drawingStartXKey = "drawingStartX";
    private static final String drawingStartYKey = "drawingStartY";

    private static final int REQUEST_EDIT_MESSAGE = 1;
    private static final int REQUEST_EDIT_TIME = 2;
    private static final int REQUEST_EDIT_REPEAT_TYPE = 3;
    private static final int REQUEST_EDIT_PERIOD = 4;
    private static final int REQUEST_EDIT_DAYS_IN_WEEK = 5;
    private static final int REQUEST_EDIT_DAY_IN_YEAR = 6;

    public static RecordFragment newInstance(int recordId, int drawingStartX, int drawingStartY) {
        RecordFragment recordFragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putInt(recordIdKey, recordId);
        args.putInt(drawingStartXKey, drawingStartX);
        args.putInt(drawingStartYKey, drawingStartY);
        recordFragment.setArguments(args);
        return recordFragment;
    }

    @BindView(R.id.root_view)
    View mRootView;

    @BindView(R.id.content_view)
    View mContentView;

    @BindView(R.id.photo_image_view)
    ImageView mPhotoImageView;

    @BindView(R.id.user_name_text_view)
    TextView mUsernameTextView;

    @BindView(R.id.enable_switch)
    SwitchCompat mEnableSwitch;

    @BindView(R.id.message_text_view)
    TextView mMessageTextView;

    @BindView(R.id.repeat_type_text_view)
    TextView mRepeatTypeTextView;

    @BindView(R.id.time_text_view)
    TextView mTimeTextView;

    @BindView(R.id.repeat_info_text_view)
    TextView mRepeatInfoTextView;

    @BindView(R.id.user_card)
    CardView mUserCardView;

    @BindView(R.id.message_card)
    CardView mMessageCardView;

    @BindView(R.id.repeat_type_card)
    CardView mRepeatTypeCardView;

    @BindView(R.id.time_card)
    CardView mTimeCardView;

    @BindView(R.id.repeat_info_card)
    CardView mRepeatInfoCardView;

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

        // TODO: 29.11.2016 transition для всех диалогов


        if (getArguments().getInt(drawingStartXKey) != RecordActivity.NO_DRAWING_START) {
            mRootView.setPivotX(getArguments().getInt(drawingStartXKey));
            mRootView.setPivotY(getArguments().getInt(drawingStartYKey));
        }

        mUserCardView.setOnClickListener(v -> mRecordPresenter.onUserClicked());
        mEnableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> mRecordPresenter.onEnableClicked(isChecked));
        mMessageCardView.setOnClickListener(v -> mRecordPresenter.onEditMessageClicked());
        mRepeatTypeCardView.setOnClickListener(v -> mRecordPresenter.onEditRepeatTypeClicked());
        mTimeCardView.setOnClickListener(v -> mRecordPresenter.onEditTimeClicked());
        mRepeatInfoCardView.setOnClickListener(v -> mRecordPresenter.onEditRepeatInfoClicked());

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
                int hour = data.getIntExtra(EditTimeDialog.EXTRA_HOUR, 19);
                int minute = data.getIntExtra(EditTimeDialog.EXTRA_MINUTE, 18);
                mRecordPresenter.onTimeEdited(hour, minute);
                break;
            case REQUEST_EDIT_REPEAT_TYPE:
                int repeatType = data.getIntExtra(EditRepeatTypeDialog.EXTRA_REPEAT_TYPE, 0);
                mRecordPresenter.onRepeatTypeEdited(repeatType);
                break;
            case REQUEST_EDIT_PERIOD:
                int period = data.getIntExtra(EditPeriodDialog.EXTRA_PERIOD, 0);
                mRecordPresenter.onPeriodEdited(period);
                break;
            case REQUEST_EDIT_DAYS_IN_WEEK:
                int daysInWeek = data.getIntExtra(EditDaysInWeekDialog.EXTRA_DAYS_IN_WEEK, 0);
                mRecordPresenter.onDaysInWeekEdited(daysInWeek);
                break;
            case REQUEST_EDIT_DAY_IN_YEAR:
                int month = data.getIntExtra(EditDayOfYearDialog.EXTRA_MONTH, 0);
                int dayOfMonth = data.getIntExtra(EditDayOfYearDialog.EXTRA_DAY_OF_MONTH, 0);
                mRecordPresenter.onDayInYearEdited(month, dayOfMonth);
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
        mEnableSwitch.jumpDrawablesToCurrentState();
    }

    @Override
    public void showTime(String time) {
        mTimeTextView.setText(time);
    }

    @Override
    public void showRepeatType(String repeatType) {
        mRepeatTypeTextView.setText(repeatType);
    }

    @Override
    public void showRepeatInfo(String repeatInfo) {
        mRepeatInfoTextView.setText(repeatInfo);
    }

    @Override
    public void showLoading() {
        mPhotoImageView.setImageBitmap(null);
        mUsernameTextView.setText(R.string.loading);
        mMessageTextView.setText(R.string.loading);
        mTimeTextView.setText(R.string.loading);
        mRepeatInfoTextView.setText(R.string.loading);
    }

    @Override
    public void showEditMessage(String message) {
        EditMessageDialog editMessageDialog = EditMessageDialog.newInstance(message);
        editMessageDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_MESSAGE);
        editMessageDialog.show(getFragmentManager(), editMessageDialog.getClass().getName());
    }

    @Override
    public void showEditTime(int hour, int minute) {
        EditTimeDialog editTimeDialog = EditTimeDialog.newInstance(hour, minute);
        editTimeDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_TIME);
        editTimeDialog.show(getFragmentManager(), editTimeDialog.getClass().getName());
    }

    @Override
    public void showEditRepeatType(int repeatType) {
        EditRepeatTypeDialog editRepeatTypeDialog = EditRepeatTypeDialog.newInstance(repeatType);
        editRepeatTypeDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_REPEAT_TYPE);
        editRepeatTypeDialog.show(getFragmentManager(), editRepeatTypeDialog.getClass().getName());
    }

    @Override
    public void showEditPeriod(int period) {
        EditPeriodDialog editPeriodDialog = EditPeriodDialog.newInstance(period);
        editPeriodDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_PERIOD);
        editPeriodDialog.show(getFragmentManager(), editPeriodDialog.getClass().getName());
    }

    @Override
    public void showEditDaysInWeek(int daysInWeek) {
        EditDaysInWeekDialog editDaysInWeekDialog = EditDaysInWeekDialog.newInstance(daysInWeek);
        editDaysInWeekDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_DAYS_IN_WEEK);
        editDaysInWeekDialog.show(getFragmentManager(), editDaysInWeekDialog.getClass().getName());
    }

    @Override
    public void showEditDayInYear(int month, int dayOfMonth) {
        EditDayOfYearDialog editDayOfYearDialog = EditDayOfYearDialog.newInstance(month, dayOfMonth);
        editDayOfYearDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_DAY_IN_YEAR);
        editDayOfYearDialog.show(getFragmentManager(), editDayOfYearDialog.getClass().getName());
    }

    @Override
    public void showToast(int stringRes) {
        Toast.makeText(getActivity(), stringRes, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Animator createEnterAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();

        if (!AndroidUtils.isLollipopOrHigher() && getArguments().getInt(drawingStartXKey) != RecordActivity.NO_DRAWING_START) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mRootView, "scaleX", 0.1f, 1);
            scaleX.setDuration(200);

            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mRootView, "scaleY", 0.1f, 1);
            scaleY.setDuration(200);

            animatorSet.play(scaleX).after(scaleY);
            animatorSet.setInterpolator(new AccelerateInterpolator());
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mContentView.setVisibility(View.INVISIBLE);
                    mRootView.setScaleX(0.1f);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mContentView.setVisibility(View.VISIBLE);
                }
            });
        }

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                Toolbar toolbar = ((ActivityInterface) getActivity()).getToolbar();
                ImageView toolbarIcon = ((ActivityInterface) getActivity()).getToolbarIcon();
                TextView toolbarTitle = ((ActivityInterface) getActivity()).getToolbarTitle();

                toolbarIcon.setTranslationY(-1.5f * toolbar.getHeight());
                toolbarTitle.setTranslationY(-1.5f * toolbar.getHeight());
            }
        });
        return animatorSet;
    }

    @Override
    protected Animator createExitAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();

        if (!AndroidUtils.isLollipopOrHigher() && getArguments().getInt(drawingStartXKey) != RecordActivity.NO_DRAWING_START) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mRootView, "scaleX", 1, 0.1f);
            scaleX.setDuration(200);

            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mRootView, "scaleY", 1, 0);
            scaleY.setDuration(200);

            animatorSet.play(scaleX).before(scaleY);
            animatorSet.setInterpolator(new AccelerateInterpolator());
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mContentView.setVisibility(View.INVISIBLE);
                }
            });
        }

        return animatorSet;
    }

    @Override
    protected Animator createInAnimator(boolean withLargeDelay) {
        ImageView toolbarIcon = ((ActivityInterface) getActivity()).getToolbarIcon();
        TextView toolbarTitle = ((ActivityInterface) getActivity()).getToolbarTitle();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(toolbarIcon, "translationY", 0);
        objectAnimator.setStartDelay(withLargeDelay ? 400 : 200);
        objectAnimator.setDuration(300);

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(toolbarTitle, "translationY", 0);
        objectAnimator1.setStartDelay(withLargeDelay ? 100 : 100);
        objectAnimator1.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(objectAnimator).with(objectAnimator1);
        return animatorSet;
    }

    @Override
    protected Animator createOutAnimator() {
        Toolbar toolbar = ((ActivityInterface) getActivity()).getToolbar();
        ImageView toolbarIcon = ((ActivityInterface) getActivity()).getToolbarIcon();
        TextView toolbarTitle = ((ActivityInterface) getActivity()).getToolbarTitle();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(toolbarTitle, "translationY", -1 * toolbar.getHeight());
        objectAnimator.setDuration(200);

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(toolbarIcon, "translationY", -1 * toolbar.getHeight());
        objectAnimator2.setStartDelay(100);
        objectAnimator2.setDuration(200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(objectAnimator).with(objectAnimator2);
        return animatorSet;
    }

    @Override
    public void performBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(NavigationActivity.EXTRA_ITEM_ID, getArguments().getInt(recordIdKey));
        getActivity().setResult(Activity.RESULT_OK, intent);
        super.performBackPressed();
    }
}
