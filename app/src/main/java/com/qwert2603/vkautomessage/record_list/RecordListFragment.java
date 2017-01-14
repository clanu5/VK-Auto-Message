package com.qwert2603.vkautomessage.record_list;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.base.list.ListFragment;
import com.qwert2603.vkautomessage.delete_record.DeleteRecordDialog;
import com.qwert2603.vkautomessage.integer_view.anim_integer_view.CounterIntegerView;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.record_details.RecordActivity;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;
import com.qwert2603.vkautomessage.transition.EpicenterExplode;
import com.qwert2603.vkautomessage.transition.EpicenterSlide;
import com.qwert2603.vkautomessage.transition.EpicenterTransition;
import com.qwert2603.vkautomessage.util.AndroidUtils;
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
    CounterIntegerView mRecordsCountTextView;

    @BindView(R.id.enabled_records_count_text_view)
    CounterIntegerView mEnabledRecordsCountTextView;

    @BindView(R.id.new_record_fab)
    FloatingActionButton mNewRecordFAB;

    @Inject
    RecordListPresenter mRecordListPresenter;

    @Inject
    RecordListAdapter mRecordListAdapter;

    private EpicenterTransition mTransitionContent;

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
        setHasOptionsMenu(true);
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(RecordListFragment.this, view);

        // TODO: 12.12.2016 фильтрация активных и неактивных записей
        // TODO: 26.12.2016 filter records by repeat type

        // to allow marquee scrolling.
        mUserNameTextView.setSelected(true);
        mUserNameTextView.setHorizontallyScrolling(true);

        mNewRecordFAB.setOnClickListener(v -> mRecordListPresenter.onNewRecordClicked());

        if (AndroidUtils.isPortraitOrientation(getActivity())) {
            mRecyclerItemAnimator.setEnterOrigin(RecyclerItemAnimator.EnterOrigin.LEFT);
        } else {
            mRecyclerItemAnimator.setEnterOrigin(RecyclerItemAnimator.EnterOrigin.LEFT_OR_RIGHT);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2) {
                @Override
                protected int getExtraLayoutSpace(RecyclerView.State state) {
                    return 300;
                }
            });
            ((GridLayoutManager) mRecyclerView.getLayoutManager()).setInitialPrefetchItemCount(8);
        }

        TransitionUtils.setSharedElementTransitions(getActivity(), R.transition.shared_element);

        Transition transitionContent;
        Transition transitionFab;
        if (AndroidUtils.isPortraitOrientation(getActivity())) {
            transitionContent = new EpicenterSlide(Gravity.START);
            transitionFab = new Slide(Gravity.END);
            transitionFab.addTarget(mNewRecordFAB);
        } else {
            transitionContent = new EpicenterExplode();
            transitionFab = new Explode();
            transitionFab.addTarget(mNewRecordFAB);
        }
        transitionContent.excludeTarget(android.R.id.navigationBarBackground, true);
        transitionContent.excludeTarget(android.R.id.statusBarBackground, true);
        transitionContent.excludeTarget(mToolbarIconImageView, true);
        transitionContent.excludeTarget(mRecordsCountLinearLayout, true);
        transitionContent.excludeTarget(mUserNameTextView, true);
        transitionContent.excludeTarget(mNewRecordFAB, true);
        for (int i = 0; i < mViewAnimator.getChildCount(); i++) {
            transitionContent.excludeTarget(mViewAnimator.getChildAt(i), true);
        }
        mTransitionContent = (EpicenterTransition) transitionContent;

        Slide slideRecordsCount = new Slide(Gravity.END);
        slideRecordsCount.addTarget(mRecordsCountLinearLayout);

        Slide slideUserName = new Slide(Gravity.END);
        slideUserName.addTarget(mUserNameTextView);

        int duration = getResources().getInteger(R.integer.transition_duration);
        TransitionSet transitionSet = new TransitionSet()
                .addTransition(transitionFab)
                .addTransition(slideRecordsCount)
                .addTransition(slideUserName)
                .addTransition(transitionContent)
                .setDuration(duration);

        getActivity().getWindow().setExitTransition(transitionSet);
        getActivity().getWindow().setReenterTransition(transitionSet);
        getActivity().getWindow().setEnterTransition(transitionSet);
        getActivity().getWindow().setReturnTransition(transitionSet);

        return view;
    }

    @Override
    public void setUser(User user) {
        mRecordListAdapter.setUser(user);
    }

    @Override
    public void showUserName(String name) {
        mUserNameTextView.setText(name);
    }

    public ImageView getUserPhotoImageView() {
        return mUserPhotoImageView;
    }

    private boolean mRecordsCountEverShown = false;

    @Override
    public void showRecordsCount(int recordsCount, int enabledRecordsCount) {
        mRecordsCountTextView.setInteger(recordsCount, mRecordsCountEverShown);
        mEnabledRecordsCountTextView.setInteger(enabledRecordsCount, mRecordsCountEverShown);
        if (!mRecordsCountEverShown) {
            mRecordsCountEverShown = true;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.record_list, menu);
        menu.findItem(R.id.filter).setActionView(R.layout.menu_item_filter);

        View actionView = menu.findItem(R.id.filter).getActionView();
        actionView.setOnClickListener(v -> {
            PopupWindow popupWindow = new PopupWindow(getActivity().getLayoutInflater().inflate(R.layout.dialog_filter_record_list, null),
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setElevation(8.0f);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            popupWindow.showAsDropDown(actionView);
        });

    }

    @Override
    protected void moveToDetailsForItem(int itemId) {
        prepareRecyclerViewForTransition();

        RecordListAdapter.RecordViewHolder viewHolder = (RecordListAdapter.RecordViewHolder) mRecyclerView.findViewHolderForItemId(itemId);
        Rect rect = new Rect();
        if (AndroidUtils.isPortraitOrientation(getActivity())) {
            viewHolder.itemView.getGlobalVisibleRect(rect);
        } else {
            int widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
            rect.set(widthPixels / 2, 0, widthPixels / 2, 0);
        }
        mTransitionContent.setEpicenterRect(rect);

        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                Pair.create(mUserPhotoImageView, mUserPhotoImageView.getTransitionName()),
                Pair.create(viewHolder.mMessageTextView, viewHolder.mMessageTextView.getTransitionName())
                // TODO: 23.12.2016 animate icon image and make ripple effect
                //Pair.create(mToolbarIconImageView, mToolbarIconImageView.getTransitionName())
        );

        Intent intent = new Intent(getActivity(), RecordActivity.class);
        intent.putExtra(RecordActivity.EXTRA_ITEM_ID, itemId);
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
        int widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
        mTransitionContent.setEpicenterRect(new Rect(widthPixels / 2, 0, widthPixels / 2, 0));
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
