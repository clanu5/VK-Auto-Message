package com.qwert2603.vkautomessage.record_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseFragment;
import com.qwert2603.vkautomessage.delete_record.DeleteRecordDialog;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.record_details.RecordActivity;
import com.qwert2603.vkautomessage.user_list.UserListDialog;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordListFragment extends BaseFragment<RecordListPresenter> implements RecordListView {

    public static RecordListFragment newInstance() {
        return new RecordListFragment();
    }

    private static final int POSITION_RECYCLER_VIEW = 0;
    private static final int POSITION_LOADING_TEXT_VIEW = 1;
    private static final int POSITION_ERROR_TEXT_VIEW = 2;
    private static final int POSITION_EMPTY_TEXT_VIEW = 3;

    private static final int REQUEST_CHOOSE_USER = 1;
    private static final int REQUEST_DELETE_RECORD = 2;

    @BindView(R.id.view_animator)
    ViewAnimator mViewAnimator;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.new_record_fab)
    FloatingActionButton mNewRecordFAB;

    @Inject
    RecordListPresenter mRecordListPresenter;

    @NonNull
    @Override
    protected RecordListPresenter getPresenter() {
        return mRecordListPresenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(RecordListFragment.this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);

        ButterKnife.bind(RecordListFragment.this, view);

        mRecyclerView = (RecyclerView) mViewAnimator.getChildAt(POSITION_RECYCLER_VIEW);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
            case REQUEST_CHOOSE_USER:
                int userId = data.getIntExtra(UserListDialog.EXTRA_SELECTED_USER_ID, 0);
                mRecordListPresenter.onUserForNewRecordChosen(userId);
                break;
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
        RecordListAdapter adapter = (RecordListAdapter) mRecyclerView.getAdapter();
        if (adapter != null && adapter.isShowingList(list)) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new RecordListAdapter(list);
            adapter.setClickCallbacks(position -> mRecordListPresenter.onRecordAtPositionClicked(position));
            adapter.setLongClickCallbacks(position -> mRecordListPresenter.onRecordAtPositionLongClicked(position));
            mRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void moveToRecordDetails(int recordId) {
        Intent intent = new Intent(getActivity(), RecordActivity.class);
        intent.putExtra(RecordActivity.EXTRA_RECORD_ID, recordId);
        getActivity().startActivity(intent);
    }

    @Override
    public void showChooseUser(int currentUserId) {
        UserListDialog userListDialog = UserListDialog.newInstance(currentUserId);
        userListDialog.setTargetFragment(RecordListFragment.this, REQUEST_CHOOSE_USER);
        userListDialog.show(getFragmentManager(), userListDialog.getClass().getName());
    }

    @Override
    public void showDeleteRecord(int recordId) {
        DeleteRecordDialog deleteRecordDialog = DeleteRecordDialog.newInstance(recordId);
        deleteRecordDialog.setTargetFragment(RecordListFragment.this, REQUEST_DELETE_RECORD);
        deleteRecordDialog.show(getFragmentManager(), deleteRecordDialog.getClass().getName());
    }

    @Override
    public void notifyItemRemoved(int position) {
        RecordListAdapter adapter = (RecordListAdapter) mRecyclerView.getAdapter();
        if (adapter != null) {
            adapter.notifyItemRemoved(position);
        }
    }

    private void setViewAnimatorDisplayedChild(int position) {
        if (mViewAnimator.getDisplayedChild() != position) {
            mViewAnimator.setDisplayedChild(position);
        }
    }
}
