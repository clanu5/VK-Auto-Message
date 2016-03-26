package com.qwert2603.vkautomessage.record_list;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.delete_record.DeleteRecordDialog;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.record_details.RecordActivity;
import com.qwert2603.vkautomessage.user_list.UserListDialog;

import java.util.List;

public class RecordListFragment extends Fragment implements RecordListView {
    // TODO: 26.03.2016  сделать базовый фрагмент для работы с presenter'ом

    public static RecordListFragment newInstance() {
        return new RecordListFragment();
    }

    private static final int POSITION_RECYCLER_VIEW = 0;
    private static final int POSITION_LOADING_TEXT_VIEW = 1;
    private static final int POSITION_ERROR_TEXT_VIEW = 2;
    private static final int POSITION_EMPTY_TEXT_VIEW = 3;

    private static final int REQUEST_CHOOSE_USER = 1;
    private static final int REQUEST_DELETE_RECORD = 2;

    private RecordListPresenter mRecordListPresenter;

    private ViewAnimator mViewAnimator;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mRecordListPresenter = new RecordListPresenter();
        mRecordListPresenter.bindView(this);
    }

    @Override
    public void onDestroy() {
        mRecordListPresenter.unbindView();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);

        mViewAnimator = (ViewAnimator) view.findViewById(R.id.view_animator);
        mRecyclerView = (RecyclerView) mViewAnimator.getChildAt(POSITION_RECYCLER_VIEW);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mViewAnimator.getChildAt(POSITION_ERROR_TEXT_VIEW).setOnClickListener(v -> mRecordListPresenter.onReload());
        ((TextView) mViewAnimator.getChildAt(POSITION_EMPTY_TEXT_VIEW)).setText(R.string.empty_records_list);

        FloatingActionButton newRecordFAB = (FloatingActionButton) view.findViewById(R.id.new_record_fab);
        newRecordFAB.setOnClickListener(v -> mRecordListPresenter.onNewRecordClicked());

        mRecordListPresenter.onViewReady();

        return view;
    }

    @Override
    public void onDestroyView() {
        mRecordListPresenter.onViewNotReady();
        super.onDestroyView();
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
            mRecyclerView.setAdapter(new RecordListAdapter(list, mRecordListPresenter));
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

    private void setViewAnimatorDisplayedChild(int position) {
        if (mViewAnimator.getDisplayedChild() != position) {
            mViewAnimator.setDisplayedChild(position);
        }
    }
}
