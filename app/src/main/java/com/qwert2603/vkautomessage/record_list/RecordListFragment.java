package com.qwert2603.vkautomessage.record_list;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.base.list.ListFragment;
import com.qwert2603.vkautomessage.delete_record.DeleteRecordDialog;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.record_details.RecordActivity;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;
import com.qwert2603.vkautomessage.util.TransitionUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordListFragment extends ListFragment<Record> implements RecordListView {

    private static final String userIdKey = "userId";

    public static RecordListFragment newInstance(int userId) {
        RecordListFragment recordListFragment = new RecordListFragment();
        Bundle args = new Bundle();
        args.putInt(userIdKey, userId);
        recordListFragment.setArguments(args);
        return recordListFragment;
    }

    @BindView(R.id.item_user)
    LinearLayout mItemUserLinearLayout;

    @BindView(R.id.user_name_text_view)
    protected TextView mUserNameTextView;

    @BindView(R.id.photo_image_view)
    ImageView mUserPhotoImageView;

    @BindView(R.id.records_count_layout)
    LinearLayout mRecordsCountLinearLayout;

    @BindView(R.id.records_count_text_view)
    TextView mRecordsCountTextView;

    @BindView(R.id.enabled_records_count_text_view)
    TextView mEnabledRecordsCountTextView;


    @BindView(R.id.new_record_fab)
    FloatingActionButton mNewRecordFAB;

    @Inject
    RecordListPresenter mRecordListPresenter;

    @Inject
    RecordListAdapter mRecordListAdapter;

    @NonNull
    @Override
    protected RecordListPresenter getPresenter() {
        return mRecordListPresenter;
    }

    @NonNull
    @Override
    protected BaseRecyclerViewAdapter<Record, ?, ?> getAdapter() {
        return mRecordListAdapter;
    }

    @Override
    protected int getToolbarContentRes() {
        return R.layout.toolbar_user;
    }

    @Override
    protected int getScreenContentRes() {
        return R.layout.fragment_record_list;
    }

    @Override
    protected boolean isNavigationButtonVisible() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(RecordListFragment.this);
        mRecordListPresenter.setUserId(getArguments().getInt(userIdKey));
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(RecordListFragment.this, view);

        // TODO: 12.12.2016 фильтрация активных и неактивных записей

        // TODO: 13.12.2016 в альбомной ориентации -- 2 столбца

        // to allow marquee scrolling.
        mUserNameTextView.setSelected(true);
        mUserNameTextView.setHorizontallyScrolling(true);

        mNewRecordFAB.setOnClickListener(v -> mRecordListPresenter.onNewRecordClicked());

        mRecyclerItemAnimator.setEnterOrigin(RecyclerItemAnimator.EnterOrigin.LEFT);

        mRecordListAdapter.setRecordEnableChangedCallback((position, enabled) -> mRecordListPresenter.onRecordEnableChanged(position, enabled));

        int duration = getResources().getInteger(R.integer.transition_duration);
        TransitionUtils.setSharedElementTransitionsDuration(getActivity(), duration);

        Slide slideContent = new Slide(Gravity.START);
        slideContent.excludeTarget(android.R.id.navigationBarBackground, true);
        slideContent.excludeTarget(android.R.id.statusBarBackground, true);
        for (int i = 0; i < mViewAnimator.getChildCount(); i++) {
            slideContent.excludeTarget(mViewAnimator.getChildAt(i), true);
        }

        Slide slideFab = new Slide(Gravity.END);
        slideFab.addTarget(mNewRecordFAB);

        Slide slideRecordsCount = new Slide(Gravity.END);
        slideRecordsCount.addTarget(mRecordsCountLinearLayout);

        Slide slideIcon = new Slide(Gravity.START);
        slideIcon.addTarget(mToolbarIconImageView);

        TransitionSet transitionSet = new TransitionSet()
                .addTransition(slideFab)
                .addTransition(slideRecordsCount)
                .addTransition(slideIcon)
                .addTransition(slideContent)
                .setDuration(duration);

        getActivity().getWindow().setExitTransition(transitionSet);
        getActivity().getWindow().setReenterTransition(transitionSet);
        getActivity().getWindow().setEnterTransition(transitionSet);
        getActivity().getWindow().setReturnTransition(transitionSet);

        return view;
    }

    @Override
    public void showUserName(String name) {
        mUserNameTextView.setText(name);
    }

    public ImageView getUserPhotoImageView() {
        return mUserPhotoImageView;
    }

    @Override
    public void showRecordsCount(int recordsCount, int enabledRecordsCount) {
        mRecordsCountTextView.setText(String.valueOf(recordsCount));
        mEnabledRecordsCountTextView.setText(String.valueOf(enabledRecordsCount));
    }

    @Override
    protected void moveToDetailsForItem(Record record) {
        prepareRecyclerViewForTransition();
        ActivityOptions activityOptions = null;
        RecordListAdapter.RecordViewHolder viewHolder =
                (RecordListAdapter.RecordViewHolder) mRecyclerView.findViewHolderForItemId(record.getId());

        if (viewHolder != null) {
            TextView messageTextView = viewHolder.mMessageTextView;
            activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                    Pair.create(messageTextView, messageTextView.getTransitionName()),
                    Pair.create(mUserNameTextView, mUserNameTextView.getTransitionName()),
                    Pair.create(mUserPhotoImageView, mUserPhotoImageView.getTransitionName())
            );
        }
        Intent intent = new Intent(getActivity(), RecordActivity.class);
        // TODO: 16.12.2016 ??? intent.putExtra(RecordActivity.EXTRA_ITEM, record);
        intent.putExtra(RecordActivity.EXTRA_ITEM_ID, record.getId());
        startActivityForResult(intent, REQUEST_DETAILS_FOT_ITEM, activityOptions != null ? activityOptions.toBundle() : null);
    }

    @Override
    public void askDeleteItem(int recordId) {
        DeleteRecordDialog deleteRecordDialog = DeleteRecordDialog.newInstance(recordId);
        deleteRecordDialog.setTargetFragment(RecordListFragment.this, REQUEST_DELETE_ITEM);
        deleteRecordDialog.show(getFragmentManager(), deleteRecordDialog.getClass().getName());
    }

    @Override
    public void showDontWriteToDeveloper() {
        Toast.makeText(getActivity(), R.string.toast_i_told_you, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void performBackPressed() {
        prepareRecyclerViewForTransition();
        Intent intent = new Intent();
        intent.putExtra(BaseActivity.EXTRA_ITEM_ID, getArguments().getInt(userIdKey));
        getActivity().setResult(Activity.RESULT_OK, intent);
        super.performBackPressed();
    }

    @Override
    public void scrollToTop() {
        super.scrollToTop();
        mNewRecordFAB.animate().translationX(0);
    }

    @Override
    public void enableUI() {
        super.enableUI();
        mNewRecordFAB.setEnabled(true);
    }

    @Override
    public void disableUI() {
        super.disableUI();
        mNewRecordFAB.setEnabled(false);
    }
}
