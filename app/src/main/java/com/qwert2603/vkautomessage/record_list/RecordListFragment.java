package com.qwert2603.vkautomessage.record_list;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.view.animation.AccelerateInterpolator;
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
import com.qwert2603.vkautomessage.navigation.ActivityInterface;
import com.qwert2603.vkautomessage.navigation.NavigationActivity;
import com.qwert2603.vkautomessage.record_details.RecordActivity;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;
import com.qwert2603.vkautomessage.util.AndroidUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordListFragment extends ListFragment<Record> implements RecordListView {

    private static final String userIdKey = "userId";
    private static final String drawingStartYKey = "drawingStart";

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

        // TODO: 03.12.2016 toolbar должен состоять из отдельных:
        // * круглая ава пользователя-получателя
        // * кол-во записей (14/26) -- todo AnimatedIntegerView
        // * имя друга (android:ellipsize="marquee")
        // каждая часть должна иметь свое transitionName

        mRootView.setPivotY(getArguments().getInt(drawingStartYKey));

        mNewRecordFAB.setOnClickListener(v -> mRecordListPresenter.onNewRecordClicked());

        mRecyclerItemAnimator.setEnterOrigin(RecyclerItemAnimator.EnterOrigin.LEFT);

        mRecordListAdapter.setRecordEnableChangedCallback((position, enabled) -> mRecordListPresenter.onRecordEnableChanged(position, enabled));

        return view;
    }

    @Override
    public void showUserName(String userName) {
        ((ActivityInterface) getActivity()).setToolbarTitle(userName);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void moveToDetailsForItem(int id) {
        ActivityOptions activityOptions = null;
        RecordListAdapter.RecordViewHolder viewHolder =
                (RecordListAdapter.RecordViewHolder) mRecyclerView.findViewHolderForItemId(id);
        if (viewHolder != null && AndroidUtils.isLollipopOrHigher()) {
            TextView messageTextView = viewHolder.mMessageTextView;
            TextView timeTextView = viewHolder.mTimeTextView;
            TextView periodTextView = viewHolder.mRepeatInfoTextView;
            CheckBox enableCheckBox = viewHolder.mEnableCheckBox;
            View toolbarTitle = ((ActivityInterface) getActivity()).getToolbarTitle();
            activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                    Pair.create(messageTextView, messageTextView.getTransitionName()),
                    Pair.create(timeTextView, timeTextView.getTransitionName()),
                    Pair.create(periodTextView, periodTextView.getTransitionName()),
                    Pair.create(enableCheckBox, enableCheckBox.getTransitionName()),
                    Pair.create(toolbarTitle, toolbarTitle.getTransitionName()));
        }
        Intent intent = new Intent(getActivity(), RecordActivity.class);
        intent.putExtra(RecordActivity.EXTRA_ITEM_ID, id);
        startActivityForResult(intent, REQUEST_DETAILS_FOT_ITEM, activityOptions != null ? activityOptions.toBundle() : null);

        getActivity().overridePendingTransition(0, 0);
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
    protected Animator createEnterAnimator() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mRootView, "scaleY", 0.1f, 1);
        objectAnimator.setDuration(400);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                ImageView toolbarIcon = ((ActivityInterface) getActivity()).getToolbarIcon();
                int toolbarIconLeftMargin = ((ViewGroup.MarginLayoutParams) toolbarIcon.getLayoutParams()).leftMargin;
                toolbarIcon.setTranslationX(-1 * (toolbarIcon.getWidth() + toolbarIconLeftMargin));

                if (!AndroidUtils.isLollipopOrHigher()) {
                    TextView toolbarTitle = ((ActivityInterface) getActivity()).getToolbarTitle();
                    int toolbarTitleRightMargin = ((ViewGroup.MarginLayoutParams) toolbarTitle.getLayoutParams()).rightMargin;
                    toolbarTitle.setTranslationX(toolbarTitle.getWidth() + toolbarTitleRightMargin);
                }

                int fabRightMargin = ((ViewGroup.MarginLayoutParams) mNewRecordFAB.getLayoutParams()).rightMargin;
                mNewRecordFAB.setTranslationX(mNewRecordFAB.getWidth() + fabRightMargin);

                mViewAnimator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mViewAnimator.setVisibility(View.VISIBLE);
            }
        });
        return objectAnimator;
    }

    @Override
    protected Animator createExitAnimator() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mRootView, "scaleY", 0);
        objectAnimator.setDuration(400);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mRecyclerView.setVisibility(View.INVISIBLE);
            }
        });
        return objectAnimator;
    }

    @Override
    protected Animator createInAnimator(boolean withLargeDelay) {
        ImageView toolbarIcon = ((ActivityInterface) getActivity()).getToolbarIcon();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(toolbarIcon, "translationX", 0);
        objectAnimator.setStartDelay(withLargeDelay ? 400 : 200);
        objectAnimator.setDuration(400);

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(mNewRecordFAB, "translationX", 0);
        objectAnimator2.setStartDelay(withLargeDelay ? 1000 : 200);
        objectAnimator2.setDuration(400);
        objectAnimator2.setInterpolator(new OvershootInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();

        if (!AndroidUtils.isLollipopOrHigher()) {
            TextView toolbarTitle = ((ActivityInterface) getActivity()).getToolbarTitle();
            ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(toolbarTitle, "translationX", 0);
            objectAnimator1.setStartDelay(withLargeDelay ? 400 : 400);
            objectAnimator1.setDuration(500);
            animatorSet.play(objectAnimator1).with(objectAnimator).with(objectAnimator2);
        } else {
            animatorSet.play(objectAnimator).with(objectAnimator2);
        }

        return animatorSet;
    }

    @Override
    protected Animator createOutAnimator() {
        ImageView toolbarIcon = ((ActivityInterface) getActivity()).getToolbarIcon();
        int toolbarIconLeftMargin = ((ViewGroup.MarginLayoutParams) toolbarIcon.getLayoutParams()).leftMargin;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(toolbarIcon, "translationX", -1 * (toolbarIcon.getWidth() + toolbarIconLeftMargin));
        objectAnimator.setDuration(300);

        int fabRightMargin = ((ViewGroup.MarginLayoutParams) mNewRecordFAB.getLayoutParams()).rightMargin;
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(mNewRecordFAB, "translationX", mNewRecordFAB.getWidth() + fabRightMargin);
        objectAnimator2.setDuration(400);

        AnimatorSet animatorSet = new AnimatorSet();

        if (!AndroidUtils.isLollipopOrHigher()) {
            TextView toolbarTitle = ((ActivityInterface) getActivity()).getToolbarTitle();
            int toolbarTitleRightMargin = ((ViewGroup.MarginLayoutParams) toolbarTitle.getLayoutParams()).rightMargin;
            ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(toolbarTitle, "translationX", toolbarTitle.getWidth() + toolbarTitleRightMargin);
            objectAnimator1.setDuration(400);
            animatorSet.play(objectAnimator).with(objectAnimator1).with(objectAnimator2);
        } else {
            animatorSet.play(objectAnimator).with(objectAnimator2);
        }

        return animatorSet;
    }

    @Override
    public void performBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(NavigationActivity.EXTRA_ITEM_ID, getArguments().getInt(userIdKey));
        getActivity().setResult(Activity.RESULT_OK, intent);
        super.performBackPressed();
    }
}
