package com.qwert2603.vkautomessage.record_list;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.base.list.ListFragment;
import com.qwert2603.vkautomessage.delete_record.DeleteRecordDialog;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.navigation.ToolbarHolder;
import com.qwert2603.vkautomessage.record_details.RecordActivity;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;

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
    protected int getLayoutRes() {
        return R.layout.fragment_record_list;
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

        mNewRecordFAB.setOnClickListener(v -> mRecordListPresenter.onNewRecordClicked());

        mRecyclerItemAnimator.setEnterOrigin(RecyclerItemAnimator.EnterOrigin.LEFT);

        return view;
    }

    @Override
    public void showUserName(String userName) {
        ((ToolbarHolder) getActivity()).setToolbarTitle(userName);
    }

    @Override
    public void moveToDetailsForItem(int id) {
        ActivityOptions activityOptions = null;
        RecordListAdapter.RecordViewHolder viewHolder =
                (RecordListAdapter.RecordViewHolder) mRecyclerView.findViewHolderForItemId(id);
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
        intent.putExtra(RecordActivity.EXTRA_RECORD_ID, id);
        startActivityForResult(intent, REQUEST_DETAILS_FOT_ITEM, activityOptions != null ? activityOptions.toBundle() : null);
    }

    @Override
    public void askDeleteItem(int recordId) {
        DeleteRecordDialog deleteRecordDialog = DeleteRecordDialog.newInstance(recordId);
        deleteRecordDialog.setTargetFragment(RecordListFragment.this, REQUEST_DELETE_ITEM);
        deleteRecordDialog.show(getFragmentManager(), deleteRecordDialog.getClass().getName());
    }

    @Override
    public void scrollListToTop() {
        super.scrollListToTop();
        ObjectAnimator.ofFloat(mNewRecordFAB, "translationX", 0).start();
    }

    @Override
    public void showDontWriteToDeveloper() {
        Toast.makeText(getActivity(), R.string.toast_i_told_you, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Animator createInAnimator(boolean withLargeDelay) {
        ImageView toolbarIcon = ((ToolbarHolder) getActivity()).getToolbarIcon();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(toolbarIcon, "translationX", 0);
        objectAnimator.setStartDelay(withLargeDelay ? 400 : 100);
        objectAnimator.setDuration(400);

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(mNewRecordFAB, "translationX", 0);
        objectAnimator1.setStartDelay(withLargeDelay ? 1000 : 100);
        objectAnimator1.setDuration(400);
        objectAnimator1.setInterpolator(new OvershootInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(objectAnimator).with(objectAnimator1);
        return animatorSet;
    }


    @Override
    protected Animator createOutAnimator() {
        ImageView toolbarIcon = ((ToolbarHolder) getActivity()).getToolbarIcon();

        int toolbarIconLeftMargin = ((ViewGroup.MarginLayoutParams) toolbarIcon.getLayoutParams()).leftMargin;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(toolbarIcon, "translationX", -1 * (toolbarIcon.getWidth() + toolbarIconLeftMargin));
        objectAnimator.setDuration(300);

        int fabRightMargin = ((ViewGroup.MarginLayoutParams) mNewRecordFAB.getLayoutParams()).rightMargin;
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(mNewRecordFAB, "translationX", mNewRecordFAB.getWidth() + fabRightMargin);
        objectAnimator1.setDuration(400);
        objectAnimator1.setInterpolator(new OvershootInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(objectAnimator).with(objectAnimator1);
        return animatorSet;
    }

    @Override
    public void prepareForIn() {
        ImageView toolbarIcon = ((ToolbarHolder) getActivity()).getToolbarIcon();
        int toolbarIconLeftMargin = ((ViewGroup.MarginLayoutParams) toolbarIcon.getLayoutParams()).leftMargin;
        toolbarIcon.setTranslationX(-1 * (toolbarIcon.getWidth() + toolbarIconLeftMargin));

        int fabRightMargin = ((ViewGroup.MarginLayoutParams) mNewRecordFAB.getLayoutParams()).rightMargin;
        mNewRecordFAB.setTranslationX(mNewRecordFAB.getWidth() + fabRightMargin);
    }

}
