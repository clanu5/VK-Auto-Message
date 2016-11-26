package com.qwert2603.vkautomessage.record_list;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseFragment;
import com.qwert2603.vkautomessage.delete_record.DeleteRecordDialog;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.navigation.ToolbarHolder;
import com.qwert2603.vkautomessage.record_details.RecordActivity;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;
import com.qwert2603.vkautomessage.recycler.SimpleOnItemTouchHelperCallback;

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

    private static final int POSITION_EMPTY_VIEW = 0;
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
        setHasOptionsMenu(true);
        // TODO: 25.11.2016 скроллинг на самый верх при нажатии на тулбар во всех списках
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);

        ButterKnife.bind(RecordListFragment.this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mRecordListAdapter);

        RecyclerItemAnimator recyclerItemAnimator = new RecyclerItemAnimator();
        recyclerItemAnimator.setEnterOrigin(RecyclerItemAnimator.EnterOrigin.LEFT);
        mRecyclerView.setItemAnimator(recyclerItemAnimator);

        mRecordListAdapter.setClickCallback(mRecordListPresenter::onRecordAtPositionClicked);
        mRecordListAdapter.setLongClickCallback(mRecordListPresenter::onRecordAtPositionLongClicked);
        mRecordListAdapter.setItemSwipeDismissCallback(position -> {
            // чтобы элемент вернулся в свое исходное положение после swipe.
            mRecordListAdapter.notifyItemChanged(position);

            mRecordListPresenter.onRecordDismissed(position);
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleOnItemTouchHelperCallback(mRecordListAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mRecordListPresenter.onCreateOptionsMenu();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_DELETE_RECORD:
                int recordId = data.getIntExtra(DeleteRecordDialog.EXTRA_RECORD_TO_DELETE_ID, 0);
                if (resultCode == Activity.RESULT_OK) {
                    mRecordListPresenter.onRecordDeleteClicked(recordId);
                } else {
                    mRecordListPresenter.onRecordDeleteCanceled(recordId);
                }
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
    public void showList(List<Record> list, boolean animate) {
        setViewAnimatorDisplayedChild(POSITION_EMPTY_VIEW);
        mRecordListAdapter.setModelList(list, animate);
    }

    @Override
    public void showUserName(String userName) {
        ((ToolbarHolder) getActivity()).setToolbarTitle(userName);
    }

    @Override
    public void moveToRecordDetails(int recordId) {
        ActivityOptions activityOptions = null;
        RecordListAdapter.RecordViewHolder viewHolder =
                (RecordListAdapter.RecordViewHolder) mRecyclerView.findViewHolderForItemId(recordId);
        if (viewHolder != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TextView messageTextView = viewHolder.mMessageTextView;
            TextView timeTextView = viewHolder.mTimeTextView;
            TextView periodTextView = viewHolder.mRepeatInfoTextView;
            CheckBox enableCheckBox = viewHolder.mEnableCheckBox;
            View toolbarTitle = ((ToolbarHolder) getActivity()).getToolbarTitle();
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

    @Override
    public void showRecordSelected(int position) {
        mRecordListAdapter.setSelectedItemPosition(position);
    }

    @Override
    public void prepareForIntroAnimation() {
        ImageView toolbarIcon = ((ToolbarHolder) getActivity()).getToolbarIcon();
        int toolbarIconLeftMargin = ((ViewGroup.MarginLayoutParams) toolbarIcon.getLayoutParams()).leftMargin;
        toolbarIcon.setTranslationX(-1 * (toolbarIcon.getWidth() + toolbarIconLeftMargin));

        int fabRightMargin = ((ViewGroup.MarginLayoutParams) mNewRecordFAB.getLayoutParams()).rightMargin;
        mNewRecordFAB.setTranslationX(mNewRecordFAB.getWidth() + fabRightMargin);
    }

    @Override
    public void runToolbarIntroAnimation() {
        ImageView toolbarIcon = ((ToolbarHolder) getActivity()).getToolbarIcon();

        toolbarIcon.animate()
                .setStartDelay(300)
                .setDuration(400)
                .translationX(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mRecordListPresenter.onToolbarIntroAnimationFinished();
                    }
                });
    }

    @Override
    public void runFABIntroAnimation() {
        mNewRecordFAB.animate()
                .translationX(0)
                .setDuration(500)
                .setStartDelay(900)
                .setInterpolator(new OvershootInterpolator());
    }

    private void setViewAnimatorDisplayedChild(int position) {
        mRecyclerView.setVisibility(position == POSITION_EMPTY_VIEW ? View.VISIBLE : View.GONE);
        if (mViewAnimator.getDisplayedChild() != position) {
            mViewAnimator.setDisplayedChild(position);
        }
    }
}
