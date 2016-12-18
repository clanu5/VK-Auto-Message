package com.qwert2603.vkautomessage.record_list;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.base.list.ListFragment;
import com.qwert2603.vkautomessage.base.navigation.ToolbarIconState;
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
    private static final String prevIconStateKey = "prevIconState";

    public static RecordListFragment newInstance(int userId, @ToolbarIconState int prevIconState) {
        RecordListFragment recordListFragment = new RecordListFragment();
        Bundle args = new Bundle();
        args.putInt(userIdKey, userId);
        args.putInt(prevIconStateKey, prevIconState);
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
    protected int getToolbarContentRes() {
        return R.layout.toolbar_title;
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

        // TODO: 03.12.2016 toolbar должен состоять из отдельных:
        // * круглая ава пользователя-получателя
        // * кол-во записей (14/26) -- todo AnimatedIntegerView
        // * имя друга (android:ellipsize="marquee")
        // каждая часть должна иметь свое transitionName

        // TODO: 12.12.2016 фильтрация активных и неактивных записей

        // TODO: 13.12.2016 в альбомной ориентации -- 2 столбца

        // TODO: 18.12.2016 ???
        mToolbarTitleTextView.setTextColor(getResources().getColor(R.color.user_name));

        mNewRecordFAB.setOnClickListener(v -> mRecordListPresenter.onNewRecordClicked());

        mRecyclerItemAnimator.setEnterOrigin(RecyclerItemAnimator.EnterOrigin.LEFT);

        mRecordListAdapter.setRecordEnableChangedCallback((position, enabled) -> mRecordListPresenter.onRecordEnableChanged(position, enabled));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAppBarLayout.setVisibility(View.INVISIBLE);

        int duration = getResources().getInteger(R.integer.transition_duration);
        TransitionUtils.setSharedElementTransitionsDuration(getActivity(), duration);

        Slide slideContent = new Slide(Gravity.START);
        slideContent.excludeTarget(android.R.id.navigationBarBackground, true);
        slideContent.excludeTarget(mToolbarIconImageView, true);
        slideContent.excludeTarget(mViewAnimator, false);
        slideContent.excludeTarget(mRecyclerView, false);

        Slide slideFab = new Slide(Gravity.END);
        slideFab.addTarget(mNewRecordFAB);
        TransitionSet transitionSet = new TransitionSet()
                .addTransition(slideFab)
                .addTransition(slideContent)
                .setDuration(duration);
        getActivity().getWindow().setExitTransition(transitionSet);
        getActivity().getWindow().setReenterTransition(transitionSet);

        @ToolbarIconState int prevIconState = getArguments().getInt(prevIconStateKey);

        Slide slideFabEnter = new Slide(Gravity.END);
        slideFabEnter.addTarget(mNewRecordFAB);
        slideFabEnter.addListener(new TransitionUtils.TransitionListenerAdapter() {
            @Override
            public void onTransitionStart(Transition transition) {
                mAppBarLayout.setVisibility(View.VISIBLE);
                setToolbarIconState(prevIconState, true);
                setToolbarIconState(R.attr.state_back_arrow, false);
            }
        });
        TransitionSet transitionSetEnter = new TransitionSet()
                .addTransition(slideFabEnter)
                .addTransition(slideContent)
                .setDuration(duration);
        getActivity().getWindow().setEnterTransition(transitionSetEnter);

        Slide slideFabReturn = new Slide(Gravity.END);
        slideFabReturn.addTarget(mNewRecordFAB);
        slideFabReturn.addListener(new TransitionUtils.TransitionListenerAdapter() {
            @Override
            public void onTransitionStart(Transition transition) {
                setToolbarIconState(prevIconState, false);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                mAppBarLayout.setVisibility(View.INVISIBLE);
            }
        });
        TransitionSet transitionSetReturn = new TransitionSet()
                .addTransition(slideFabReturn)
                .addTransition(slideContent)
                .setDuration(duration);
        getActivity().getWindow().setReturnTransition(transitionSetReturn);
    }

    @Override
    public void showUserName(String userName) {
        mToolbarTitleTextView.setText(userName);
    }

    @Override
    public void moveToDetailsForItem(Record record/*, boolean withSetPressed*/) {
        ActivityOptions activityOptions = null;
        RecordListAdapter.RecordViewHolder viewHolder =
                (RecordListAdapter.RecordViewHolder) mRecyclerView.findViewHolderForItemId(record.getId());
        if (viewHolder != null) {
            // TODO: 16.12.2016 viewHolder.itemView.setPressed(withSetPressed);

            TextView messageTextView = viewHolder.mMessageTextView;
            TextView timeTextView = viewHolder.mTimeTextView;
            TextView periodTextView = viewHolder.mRepeatInfoTextView;
            CheckBox enableCheckBox = viewHolder.mEnableCheckBox;
            activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                    Pair.create(messageTextView, messageTextView.getTransitionName()),
                    Pair.create(timeTextView, timeTextView.getTransitionName()),
                    Pair.create(periodTextView, periodTextView.getTransitionName()),
                    Pair.create(enableCheckBox, enableCheckBox.getTransitionName()),
                    Pair.create(mToolbarTitleTextView, mToolbarTitleTextView.getTransitionName()),
                    Pair.create(mToolbarIconImageView, mToolbarIconImageView.getTransitionName())
            );
        }
        Intent intent = new Intent(getActivity(), RecordActivity.class);
        // TODO: 16.12.2016 ??? intent.putExtra(RecordActivity.EXTRA_ITEM, record);
        intent.putExtra(RecordActivity.EXTRA_ITEM_ID, record.getId());

        ActivityOptions finalActivityOptions = activityOptions;
        startActivityForResult(intent, REQUEST_DETAILS_FOT_ITEM, finalActivityOptions != null ? finalActivityOptions.toBundle() : null);
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
        Intent intent = new Intent();
        intent.putExtra(BaseActivity.EXTRA_ITEM_ID, getArguments().getInt(userIdKey));
        getActivity().setResult(Activity.RESULT_OK, intent);
        super.performBackPressed();
    }
}
