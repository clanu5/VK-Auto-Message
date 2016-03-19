package com.qwert2603.vkautomessage.record_list;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.navigation.NavigationActivity;
import com.qwert2603.vkautomessage.record_details.RecordActivity;
import com.qwert2603.vkautomessage.model.entity.Record;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecordListFragment extends Fragment implements RecordListView {

    public static RecordListFragment newInstance() {
        return new RecordListFragment();
    }

    private static final int POSITION_RECYCLER_VIEW = 0;
    private static final int POSITION_LOADING_TEXT_VIEW = 1;
    private static final int POSITION_ERROR_TEXT_VIEW = 2;
    private static final int POSITION_EMPTY_TEXT_VIEW = 3;

    private RecordListPresenter mRecordListPresenter;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.view_animator)
    ViewAnimator mViewAnimator;

    @Bind(R.id.new_record_fab)
    FloatingActionButton mNewRecordFAB;

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
        ButterKnife.bind(this, view);

        ((NavigationActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(v -> ((NavigationActivity) getActivity()).getDrawerPresenter().onNavigationClicked());

        mRecyclerView = (RecyclerView) mViewAnimator.getChildAt(POSITION_RECYCLER_VIEW);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mNewRecordFAB.setOnClickListener(v -> mRecordListPresenter.onNewRecordClicked());

        mViewAnimator.getChildAt(POSITION_ERROR_TEXT_VIEW).setOnClickListener(v -> mRecordListPresenter.onReload());

        mRecordListPresenter.onViewReady();

        return view;
    }

    @Override
    public void onDestroyView() {
        mRecordListPresenter.onViewNotReady();
        super.onDestroyView();
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
