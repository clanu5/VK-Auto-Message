package com.qwert2603.vkautomessage.record_list;

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
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.model.entity.Record;
import com.qwert2603.vkautomessage.record_details.RecordActivity;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.List;

public class RecordListFragment extends Fragment implements RecordListView {

    public static RecordListFragment newInstance() {
        return new RecordListFragment();
    }

    private static final int POSITION_RECYCLER_VIEW = 0;
    private static final int POSITION_LOADING_TEXT_VIEW = 1;
    private static final int POSITION_ERROR_TEXT_VIEW = 2;
    private static final int POSITION_EMPTY_TEXT_VIEW = 3;

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

        FloatingActionButton newRecordFAB = (FloatingActionButton) view.findViewById(R.id.new_record_fab);
        newRecordFAB.setOnClickListener(v -> mRecordListPresenter.onNewRecordClicked());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecordListPresenter.onViewReady();
    }

    @Override
    public void onPause() {
        mRecordListPresenter.onViewNotReady();
        super.onPause();
    }

    @Override
    public void showLoading() {
        mViewAnimator.setDisplayedChild(POSITION_LOADING_TEXT_VIEW);
    }

    @Override
    public void showError() {
        mViewAnimator.setDisplayedChild(POSITION_ERROR_TEXT_VIEW);
    }

    @Override
    public void showEmpty() {
        mViewAnimator.setDisplayedChild(POSITION_EMPTY_TEXT_VIEW);
    }

    @Override
    public void showList(List<Record> list) {
        LogUtils.d("showList " + list);
        mViewAnimator.setDisplayedChild(POSITION_RECYCLER_VIEW);
        mRecyclerView.setAdapter(new RecordListAdapter(list, mRecordListPresenter));
    }

    @Override
    public void moveToRecordDetails(int recordId) {
        Intent intent = new Intent(getActivity(), RecordActivity.class);
        intent.putExtra(RecordActivity.EXTRA_RECORD_ID, recordId);
        getActivity().startActivity(intent);
    }
}
