package com.qwert2603.vkautomessage.record_list;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseFragment;
import com.qwert2603.vkautomessage.delete_record.DeleteRecordDialog;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.navigation.NavigationView;
import com.qwert2603.vkautomessage.record_details.RecordActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordListFragment extends BaseFragment<RecordListPresenter> implements RecordListView {

    private static final String userIdKey = "userId";

    public static RecordListFragment newInstance(int userId) {
        RecordListFragment recordListFragment = new RecordListFragment();
        Bundle args = new Bundle();
        args.putInt(userIdKey, userId);
        recordListFragment.setArguments(args);
        return recordListFragment;
    }

    private static final int POSITION_RECYCLER_VIEW = 0;
    private static final int POSITION_LOADING_TEXT_VIEW = 1;
    private static final int POSITION_ERROR_TEXT_VIEW = 2;
    private static final int POSITION_EMPTY_TEXT_VIEW = 3;

    private static final int REQUEST_DELETE_RECORD = 1;

    @BindView(R.id.view_animator)
    ViewAnimator mViewAnimator;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(RecordListFragment.this);
        mRecordListPresenter.setUserId(getArguments().getInt(userIdKey));
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);

        ButterKnife.bind(RecordListFragment.this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mRecordListAdapter);
        mRecordListAdapter.setClickCallbacks(mRecordListPresenter::onRecordAtPositionClicked);
        mRecordListAdapter.setLongClickCallbacks(mRecordListPresenter::onRecordAtPositionLongClicked);

        mViewAnimator.getChildAt(POSITION_ERROR_TEXT_VIEW).setOnClickListener(v -> mRecordListPresenter.onReload());

        mNewRecordFAB.setOnClickListener(v -> mRecordListPresenter.onNewRecordClicked());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecordListPresenter.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_DELETE_RECORD:
                int recordId = data.getIntExtra(DeleteRecordDialog.EXTRA_RECORD_TO_DELETE_ID, 0);
                mRecordListPresenter.onRecordDeleteClicked(recordId);
                break;
        }
    }

    @Override
    public void showLoading() {
        setViewAnimatorDisplayedChild(POSITION_LOADING_TEXT_VIEW);
    }

    @Override
    public void showError() {
        setViewAnimatorDisplayedChild(POSITION_ERROR_TEXT_VIEW);
    }

    @Override
    public void showEmpty() {
        setViewAnimatorDisplayedChild(POSITION_EMPTY_TEXT_VIEW);
    }

    @Override
    public void showList(List<Record> list) {
        setViewAnimatorDisplayedChild(POSITION_RECYCLER_VIEW);
        mRecordListAdapter.setModelList(list);
    }

    @Override
    public void showUserName(String userName) {
        ((NavigationView) getActivity()).setToolbarTitle(userName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void moveToRecordDetails(int recordId) {
        ActivityOptions activityOptions = null;
        RecordListAdapter.RecordViewHolder viewHolder = mRecordListAdapter.getViewHolderForModel(recordId);
        if (viewHolder != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TextView messageTextView = viewHolder.mMessageTextView;
            TextView timeTextView = viewHolder.mTimeTextView;
            TextView periodTextView = viewHolder.mRepeatInfoTextView;
            CheckBox enableCheckBox = viewHolder.mEnableCheckBox;
            View toolbarTitle = getActivity().findViewById(R.id.toolbar_title_text_view);
            activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                    Pair.create(messageTextView, messageTextView.getTransitionName()),
                    Pair.create(timeTextView, timeTextView.getTransitionName()),
                    Pair.create(periodTextView, periodTextView.getTransitionName()),
                    Pair.create(enableCheckBox, enableCheckBox.getTransitionName()),
                    Pair.create(toolbarTitle, toolbarTitle.getTransitionName()));
        }
        Intent intent = new Intent(getActivity(), RecordActivity.class);
        intent.putExtra(RecordActivity.EXTRA_RECORD_ID, recordId);
        startActivity(intent, activityOptions != null ? activityOptions.toBundle() : null);
    }

    @Override
    public void showDeleteRecord(int recordId) {
        DeleteRecordDialog deleteRecordDialog = DeleteRecordDialog.newInstance(recordId);
        deleteRecordDialog.setTargetFragment(RecordListFragment.this, REQUEST_DELETE_RECORD);
        deleteRecordDialog.show(getFragmentManager(), deleteRecordDialog.getClass().getName());
    }

    @Override
    public void notifyItemRemoved(int position) {
        mRecordListAdapter.notifyItemRemoved(position);
    }

    @Override
    public void notifyItemInserted(int position) {
        mRecordListAdapter.notifyItemInserted(position);
    }

    @Override
    public void showDontWriteToDeveloper() {
        Toast.makeText(getActivity(), R.string.toast_i_told_you, Toast.LENGTH_SHORT).show();
    }

    private void setViewAnimatorDisplayedChild(int position) {
        if (mViewAnimator.getDisplayedChild() != position) {
            mViewAnimator.setDisplayedChild(position);
        }
    }
}
