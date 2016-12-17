package com.qwert2603.vkautomessage.record_list;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Pair;
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
import com.qwert2603.vkautomessage.util.AndroidUtils;

import java.util.List;

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

        mContentRootView.setPivotY(getArguments().getInt(drawingStartYKey));

        mNewRecordFAB.setOnClickListener(v -> mRecordListPresenter.onNewRecordClicked());

        mRecyclerItemAnimator.setEnterOrigin(RecyclerItemAnimator.EnterOrigin.LEFT);

        mRecordListAdapter.setRecordEnableChangedCallback((position, enabled) -> mRecordListPresenter.onRecordEnableChanged(position, enabled));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            int toolbarIconLeftMargin = ((ViewGroup.MarginLayoutParams) mToolbarIconImageView.getLayoutParams()).leftMargin;
            mToolbarIconImageView.setTranslationX(-1 * (mToolbarIconImageView.getWidth() + toolbarIconLeftMargin));

            int fabRightMargin = ((ViewGroup.MarginLayoutParams) mNewRecordFAB.getLayoutParams()).rightMargin;
            mNewRecordFAB.setTranslationX(mNewRecordFAB.getWidth() + fabRightMargin);

            animateIn();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_DETAILS_FOT_ITEM:
                animateIn();
                animateInNewItemButton(0);
                break;
        }
    }

    @Override
    public void showUserName(String userName) {
        mToolbarTitleTextView.setText(userName);
    }

    @Override
    protected void onFirstContentShow(@Nullable List<Record> list) {
        super.onFirstContentShow(list);
        animateInNewItemButton(mRecyclerItemAnimator.getEnterDelayPerScreen());
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
        AndroidUtils.runOnUI(() -> startActivityForResult(intent, REQUEST_DETAILS_FOT_ITEM, finalActivityOptions != null ? finalActivityOptions.toBundle() : null), 400);

        animateOut();
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

//    private void animateEnter() {
//        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mContentRootView, "scaleY", 0.1f, 1);
//        objectAnimator.setDuration(300);
//        objectAnimator.setInterpolator(new AccelerateInterpolator());
//        objectAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//                mViewAnimator.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mViewAnimator.setVisibility(View.VISIBLE);
//            }
//        });
//    }
//
//    private void animateExit() {
//        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mContentRootView, "scaleY", 0);
//        scaleY.setDuration(300);
//        scaleY.setInterpolator(new AccelerateInterpolator());
//        scaleY.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                mRecyclerView.setVisibility(View.INVISIBLE);
//                mNewRecordFAB.setVisibility(View.INVISIBLE);
//            }
//        });
//
//        ObjectAnimator alpha = ObjectAnimator.ofFloat(mContentRootView, "alpha", 1, 0);
//        alpha.setDuration(100);
//        alpha.setStartDelay(200);
//
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.play(scaleY).with(alpha);
//    }

    private void animateIn() {
        mToolbarIconImageView.animate().translationX(0).setStartDelay(50).setDuration(300);
    }

    private void animateOut() {
        int toolbarIconLeftMargin = ((ViewGroup.MarginLayoutParams) mToolbarIconImageView.getLayoutParams()).leftMargin;
        mToolbarIconImageView.animate().translationX(-1 * (mToolbarIconImageView.getWidth() + toolbarIconLeftMargin)).setStartDelay(0).setDuration(300);

        int fabRightMargin = ((ViewGroup.MarginLayoutParams) mNewRecordFAB.getLayoutParams()).rightMargin;
        mNewRecordFAB.animate().translationX(mNewRecordFAB.getWidth() + fabRightMargin).setStartDelay(0).setDuration(300);
    }

    public void animateInNewItemButton(int delay) {
        mNewRecordFAB.animate().translationX(0).setStartDelay(delay).setDuration(300);
    }

    @Override
    protected void performBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(BaseActivity.EXTRA_ITEM_ID, getArguments().getInt(userIdKey));
        getActivity().setResult(Activity.RESULT_OK, intent);
        animateOut();
        super.performBackPressed();
    }
}
