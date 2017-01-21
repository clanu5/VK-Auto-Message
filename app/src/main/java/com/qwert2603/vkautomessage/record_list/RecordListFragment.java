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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.qwert2603.vkautomessage.avatar_view.AvatarView;
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
import com.qwert2603.vkautomessage.avatar_view.RoundedTransformation;
import com.qwert2603.vkautomessage.util.TransitionUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

    @BindView(R.id.toolbar_user)
    LinearLayout mToolbarUserLinearLayout;

    @BindView(R.id.user_name_text_view)
    protected TextView mUserNameTextView;

    @BindView(R.id.avatar_view)
    AvatarView mAvatarView;

    @BindView(R.id.records_count_layout)
    LinearLayout mRecordsCountLinearLayout;

    @BindView(R.id.records_count_text_view)
    CounterIntegerView mRecordsCountView;

    @BindView(R.id.enabled_records_count_text_view)
    CounterIntegerView mEnabledRecordsCountView;

    @BindView(R.id.new_record_fab)
    FloatingActionButton mNewRecordFAB;

    @Inject
    RecordListPresenter mRecordListPresenter;

    @Inject
    RecordListAdapter mRecordListAdapter;

    private EpicenterTransition mTransitionContent;

    private Slide mSlideToolbarTop;

    private Target mPicassoTarget;

    private boolean mEnterAnimationMenuItemPlayed = false;

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

        mPicassoTarget = new AvatarView.PicassoTarget(mAvatarView);

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
        mRecyclerItemAnimator.setAddDuration(500);

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
        transitionContent.addTarget(mRecyclerView);
        mTransitionContent = (EpicenterTransition) transitionContent;

        mSlideToolbarTop = new Slide(Gravity.TOP);
        mSlideToolbarTop.addTarget(mRecordsCountLinearLayout);
        mSlideToolbarTop.addTarget(mUserNameTextView);
        mSlideToolbarTop.addTarget(mToolbarIconImageView);

        int duration = getResources().getInteger(R.integer.transition_duration);
        TransitionSet transitionSet = new TransitionSet()
                .addTransition(transitionFab)
                .addTransition(mSlideToolbarTop)
                .addTransition(transitionContent)
                .setDuration(duration);

        getActivity().getWindow().setExitTransition(transitionSet);
        getActivity().getWindow().setReenterTransition(transitionSet);
        getActivity().getWindow().setEnterTransition(transitionSet);
        getActivity().getWindow().setReturnTransition(transitionSet);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Picasso.with(getActivity()).cancelRequest(mPicassoTarget);
    }

    @Override
    public void setUser(User user) {
        mRecordListAdapter.setUser(user);
    }

    @Override
    public void showLoadingUserInfo() {
        mUserNameTextView.setText("");
        mAvatarView.showInitials("");
        mRecordsCountView.setNothing();
        mEnabledRecordsCountView.setNothing();
    }

    @Override
    public void showUserName(String name) {
        mUserNameTextView.setText(name);
    }

    @Override
    public void showUserPhoto(String url, String initials) {
        mAvatarView.showInitials(initials);
        Picasso.with(getActivity())
                .load(url)
                .transform(new RoundedTransformation())
                .into(mPicassoTarget);
    }

    private boolean mRecordsCountEverShown = false;

    @Override
    public void showRecordsCount(int recordsCount, int enabledRecordsCount) {
        mRecordsCountView.setInteger(recordsCount, mRecordsCountEverShown);
        mEnabledRecordsCountView.setInteger(enabledRecordsCount, mRecordsCountEverShown);
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
            View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_filter_record_list, null);

            int filterState = mRecordListPresenter.getFilterState();

            CheckBox filter_enabled = (CheckBox) view.findViewById(R.id.filter_enabled);
            filter_enabled.setChecked((filterState & RecordListPresenter.FILTER_ENABLED) != 0);
            filter_enabled.setOnCheckedChangeListener((buttonView, isChecked) -> mRecordListPresenter.onFilterStateChanged(RecordListPresenter.FILTER_ENABLED, isChecked));

            CheckBox filter_disabled = (CheckBox) view.findViewById(R.id.filter_disabled);
            filter_disabled.setChecked((filterState & RecordListPresenter.FILTER_DISABLED) != 0);
            filter_disabled.setOnCheckedChangeListener((buttonView, isChecked) -> mRecordListPresenter.onFilterStateChanged(RecordListPresenter.FILTER_DISABLED, isChecked));

            CheckBox filter_periodically_by_hours = (CheckBox) view.findViewById(R.id.filter_periodically_by_hours);
            filter_periodically_by_hours.setChecked((filterState & RecordListPresenter.FILTER_PERIODICALLY_BY_HOURS) != 0);
            filter_periodically_by_hours.setOnCheckedChangeListener((buttonView, isChecked) -> mRecordListPresenter.onFilterStateChanged(RecordListPresenter.FILTER_PERIODICALLY_BY_HOURS, isChecked));

            CheckBox filter_days_in_week = (CheckBox) view.findViewById(R.id.filter_days_in_week);
            filter_days_in_week.setChecked((filterState & RecordListPresenter.FILTER_DAYS_IN_WEEK) != 0);
            filter_days_in_week.setOnCheckedChangeListener((buttonView, isChecked) -> mRecordListPresenter.onFilterStateChanged(RecordListPresenter.FILTER_DAYS_IN_WEEK, isChecked));

            CheckBox filter_day_in_year = (CheckBox) view.findViewById(R.id.filter_day_in_year);
            filter_day_in_year.setChecked((filterState & RecordListPresenter.FILTER_DAY_IN_YEAR) != 0);
            filter_day_in_year.setOnCheckedChangeListener((buttonView, isChecked) -> mRecordListPresenter.onFilterStateChanged(RecordListPresenter.FILTER_DAY_IN_YEAR, isChecked));

            view.findViewById(R.id.reset_filter).setOnClickListener(v1 -> {
                filter_enabled.setChecked(true);
                filter_disabled.setChecked(true);
                filter_periodically_by_hours.setChecked(true);
                filter_days_in_week.setChecked(true);
                filter_day_in_year.setChecked(true);
                mRecordListPresenter.onResetFilterClicked();
            });

            PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setElevation(8.0f);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            popupWindow.showAsDropDown(actionView);
        });

        if (!mEnterAnimationMenuItemPlayed) {
            mEnterAnimationMenuItemPlayed = true;
            AndroidUtils.setActionOnPreDraw(actionView, () -> {
                actionView.setTranslationY(-mToolbar.getHeight() * 1.5f);
                actionView.animate().setStartDelay(200).setDuration(400).translationY(0);
                mSlideToolbarTop.addTarget(actionView);
            });
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void moveToDetailsForItem(int itemId) {
        prepareRecyclerViewForTransition();

        RecordListAdapter.RecordViewHolder viewHolder = (RecordListAdapter.RecordViewHolder) mRecyclerView.findViewHolderForItemId(itemId);
        Rect rect = new Rect();
        if (viewHolder != null && AndroidUtils.isPortraitOrientation(getActivity())) {
            viewHolder.itemView.getGlobalVisibleRect(rect);
        } else {
            int widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
            rect.set(widthPixels / 2, 0, widthPixels / 2, 0);
        }
        mTransitionContent.setEpicenterRect(rect);

        Transition transition = ((Transition) mTransitionContent);
        transition.getTargets().clear();
        transition.addTarget(mRecyclerView);
        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
            transition.addTarget(mRecyclerView.getChildAt(i));
        }

        ActivityOptions activityOptions;

        if (viewHolder != null) {
            activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                    Pair.create(mAvatarView, mAvatarView.getTransitionName()),
                    Pair.create(viewHolder.mMessageTextView, viewHolder.mMessageTextView.getTransitionName())
            );
        } else {
            activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                    Pair.create(mAvatarView, mAvatarView.getTransitionName())
            );
        }

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

        Transition transition = ((Transition) mTransitionContent);
        transition.getTargets().clear();
        transition.addTarget(mRecyclerView);
        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
            transition.addTarget(mRecyclerView.getChildAt(i));
        }

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
}
