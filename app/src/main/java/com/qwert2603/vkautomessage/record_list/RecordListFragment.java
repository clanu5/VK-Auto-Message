package com.qwert2603.vkautomessage.record_list;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.transition.Slide;
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
    private static final String drawingStartYKey = "drawingStartY";

    public static RecordListFragment newInstance(int userId, int drawingStartY) {
        RecordListFragment recordListFragment = new RecordListFragment();
        Bundle args = new Bundle();
        args.putInt(userIdKey, userId);
        args.putInt(drawingStartYKey, drawingStartY);
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
        mToolbarTitleTextView.setTextColor(Color.BLACK);

        mContentRootView.setPivotY(getArguments().getInt(drawingStartYKey));

        mNewRecordFAB.setOnClickListener(v -> mRecordListPresenter.onNewRecordClicked());

        mRecyclerItemAnimator.setEnterOrigin(RecyclerItemAnimator.EnterOrigin.LEFT);

        mRecordListAdapter.setRecordEnableChangedCallback((position, enabled) -> mRecordListPresenter.onRecordEnableChanged(position, enabled));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Slide slideToolbarIcon = new Slide(Gravity.START);
        slideToolbarIcon.addTarget(mToolbarIconImageView);

        Slide slideFab = new Slide(Gravity.END);
        slideFab.addTarget(mNewRecordFAB);

        Slide slideContent = new Slide(Gravity.START);
        slideContent.removeTarget(mToolbarIconImageView);
        slideContent.removeTarget(mNewRecordFAB);

        TransitionSet transitionSet = new TransitionSet()
                .addTransition(slideToolbarIcon)
                .addTransition(slideFab)
                .addTransition(slideContent);

        int duration = getResources().getInteger(R.integer.transition_duration);

        getActivity().getWindow().setEnterTransition(transitionSet);
        getActivity().getWindow().setExitTransition(transitionSet);
        getActivity().getWindow().setReenterTransition(transitionSet);
        getActivity().getWindow().setReturnTransition(transitionSet);

        TransitionUtils.setSharedElementTransitionsDuration(getActivity(), duration);
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
            TextView messageTextView = viewHolder.mMessageTextView;
            TextView timeTextView = viewHolder.mTimeTextView;
            TextView periodTextView = viewHolder.mRepeatInfoTextView;
            CheckBox enableCheckBox = viewHolder.mEnableCheckBox;
            activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                    Pair.create(messageTextView, messageTextView.getTransitionName()),
                    Pair.create(timeTextView, timeTextView.getTransitionName()),
                    Pair.create(periodTextView, periodTextView.getTransitionName()),
                    Pair.create(enableCheckBox, enableCheckBox.getTransitionName()),
                    Pair.create(mToolbarTitleTextView, mToolbarTitleTextView.getTransitionName()));
        }
        Intent intent = new Intent(getActivity(), RecordActivity.class);
        // TODO: 16.12.2016 ??? intent.putExtra(RecordActivity.EXTRA_ITEM, record);
        intent.putExtra(RecordActivity.EXTRA_ITEM_ID, record.getId());

        if (viewHolder != null) {
            // TODO: 16.12.2016 viewHolder.itemView.setPressed(withSetPressed);

            int[] startingPoint = new int[2];
            viewHolder.itemView.getLocationOnScreen(startingPoint);
            startingPoint[0] += viewHolder.itemView.getWidth() / 2;
            startingPoint[1] -= mToolbar.getHeight();
            intent.putExtra(RecordActivity.EXTRA_DRAWING_START_X, startingPoint[0]);
            intent.putExtra(RecordActivity.EXTRA_DRAWING_START_Y, startingPoint[1]);
        }

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
