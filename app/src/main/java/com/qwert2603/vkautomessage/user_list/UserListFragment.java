package com.qwert2603.vkautomessage.user_list;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
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
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseFragment;
import com.qwert2603.vkautomessage.choose_user.ChooseUserDialog;
import com.qwert2603.vkautomessage.delete_user.DeleteUserDialog;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.navigation.ToolbarHolder;
import com.qwert2603.vkautomessage.record_list.RecordListActivity;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;
import com.qwert2603.vkautomessage.recycler.SimpleOnItemTouchHelperCallback;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListFragment extends BaseFragment<UserListPresenter> implements UserListView {

    public static UserListFragment newInstance() {
        return new UserListFragment();
    }

    private static final int POSITION_EMPTY_VIEW = 0;
    private static final int POSITION_LOADING_TEXT_VIEW = 1;
    private static final int POSITION_ERROR_TEXT_VIEW = 2;
    private static final int POSITION_EMPTY_TEXT_VIEW = 3;

    private static final int REQUEST_CHOOSE_USER = 1;
    private static final int REQUEST_DELETE_USER = 2;
    private static final int REQUEST_RECORDS_FOR_USER = 3;

    @BindView(R.id.view_animator)
    ViewAnimator mViewAnimator;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.choose_user_fab)
    FloatingActionButton mChooseUserFAB;

    @Inject
    UserListPresenter mUserListPresenter;

    @Inject
    UserListAdapter mUserListAdapter;

    @NonNull
    @Override
    protected UserListPresenter getPresenter() {
        return mUserListPresenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(UserListFragment.this);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        ButterKnife.bind(UserListFragment.this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mUserListAdapter);
        mRecyclerView.setItemAnimator(new RecyclerItemAnimator());
        LogUtils.d("UserListFragment mRecyclerView.setItemAnimator(new RecyclerItemAnimator());");

        mUserListAdapter.setClickCallback(mUserListPresenter::onUserAtPositionClicked);
        mUserListAdapter.setLongClickCallback(mUserListPresenter::onUserAtPositionLongClicked);
        mUserListAdapter.setItemSwipeDismissCallback(position -> {
            LogUtils.d("UserListFragment setItemSwipeDismissCallback" + position);

            // чтобы элемент вернулся в свое исходное положение после swipe.
            mUserListAdapter.notifyItemChanged(position);

            mUserListPresenter.onUserDismissed(position);
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleOnItemTouchHelperCallback(mUserListAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mViewAnimator.getChildAt(POSITION_ERROR_TEXT_VIEW).setOnClickListener(v -> mUserListPresenter.onReload());

        mChooseUserFAB.setOnClickListener(v -> mUserListPresenter.onChooseUserClicked());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserListPresenter.onNeedToReloadUserList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mUserListPresenter.onReadyToAnimateIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CHOOSE_USER:
                if (resultCode == Activity.RESULT_OK) {
                    int userId = data.getIntExtra(ChooseUserDialog.EXTRA_SELECTED_USER_ID, 0);
                    mUserListPresenter.onUserChosen(userId);
                }
                break;
            case REQUEST_DELETE_USER:
                int deletingUserId = data.getIntExtra(DeleteUserDialog.EXTRA_USER_TO_DELETE_ID, 0);
                if (resultCode == Activity.RESULT_OK) {
                    mUserListPresenter.onUserDeleteClicked(deletingUserId);
                } else {
                    mUserListPresenter.onUserDeleteCanceled(deletingUserId);
                }
                break;
            case REQUEST_RECORDS_FOR_USER:
                mUserListPresenter.onReturnFromRecordsForUser();
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
    public void showList(List<User> list, boolean animate) {
        setViewAnimatorDisplayedChild(POSITION_EMPTY_VIEW);
        mUserListAdapter.setModelList(list, animate);
    }

    @Override
    public void moveToRecordsForUser(int userId) {
        LogUtils.d("UserListFragment moveToRecordsForUser " + userId);
        ActivityOptions activityOptions = null;
        UserListAdapter.UserViewHolder viewHolder =
                (UserListAdapter.UserViewHolder) mRecyclerView.findViewHolderForItemId(userId);
        if (viewHolder != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // TODO: 26.11.2016 делать фон синим как тулбар во время TransitionAnimation
            View itemView = viewHolder.itemView;
            activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                    Pair.create(itemView, itemView.getTransitionName()));
        }
        Intent intent = new Intent(getActivity(), RecordListActivity.class);
        intent.putExtra(RecordListActivity.EXTRA_USER_ID, userId);
        startActivityForResult(intent, REQUEST_RECORDS_FOR_USER, activityOptions != null ? activityOptions.toBundle() : null);
    }

    @Override
    public void showChooseUser() {
        ChooseUserDialog userListDialog = ChooseUserDialog.newInstance();
        userListDialog.setTargetFragment(UserListFragment.this, REQUEST_CHOOSE_USER);
        userListDialog.show(getFragmentManager(), userListDialog.getClass().getName());
    }

    @Override
    public void showDeleteUser(int userId) {
        DeleteUserDialog deleteUserDialog = DeleteUserDialog.newInstance(userId);
        deleteUserDialog.setTargetFragment(UserListFragment.this, REQUEST_DELETE_USER);
        deleteUserDialog.show(getFragmentManager(), deleteUserDialog.getClass().getName());
    }

    @Override
    public void notifyItemRemoved(int position) {
        mUserListAdapter.notifyItemRemoved(position);
    }

    @Override
    public void notifyItemInserted(int position) {
        mUserListAdapter.notifyItemInserted(position);
    }

    @Override
    public void showUserSelected(int position) {
        mUserListAdapter.setSelectedItemPosition(position);
    }

    @Override
    public void animateIn(boolean withLargeDelay) {
        LogUtils.d("UserListFragment animateIn " + withLargeDelay);

        ImageView toolbarIcon = ((ToolbarHolder) getActivity()).getToolbarIcon();
        TextView toolbarTitle = ((ToolbarHolder) getActivity()).getToolbarTitle();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(toolbarIcon, "translationY", 0);
        objectAnimator.setStartDelay(withLargeDelay ? 400 : 100);
        objectAnimator.setDuration(200);

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(toolbarTitle, "translationY", 0);
        objectAnimator1.setStartDelay(withLargeDelay ? 500 : 200);
        objectAnimator1.setDuration(200);
        objectAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mUserListPresenter.onReadyAnimateList();
            }
        });

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(mChooseUserFAB, "translationY", 0);
        objectAnimator2.setStartDelay(withLargeDelay ? 1600 : 300);
        objectAnimator2.setDuration(300);
        objectAnimator2.setInterpolator(new OvershootInterpolator());

        objectAnimator.start();
        objectAnimator1.start();
        objectAnimator2.start();
    }

    @Override
    public void animateOut(int userId) {
        LogUtils.d("UserListFragment animateOut " + userId);

        int fabBottomMargin = ((ViewGroup.MarginLayoutParams) mChooseUserFAB.getLayoutParams()).bottomMargin;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mChooseUserFAB, "translationY", mChooseUserFAB.getHeight() + fabBottomMargin);
        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new OvershootInterpolator());

        Toolbar toolbar = ((ToolbarHolder) getActivity()).getToolbar();
        ImageView toolbarIcon = ((ToolbarHolder) getActivity()).getToolbarIcon();
        TextView toolbarTitle = ((ToolbarHolder) getActivity()).getToolbarTitle();

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(toolbarIcon, "translationY", -1 * toolbar.getHeight());
        objectAnimator1.setDuration(200);

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(toolbarTitle, "translationY", -1 * toolbar.getHeight());
        objectAnimator2.setStartDelay(100);
        objectAnimator2.setDuration(200);
        objectAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                LogUtils.d("mUserListPresenter.onAnimateOutFinished(userId);" + userId);
                mUserListPresenter.onAnimateOutFinished(userId);
            }
        });

        objectAnimator.start();
        objectAnimator1.start();
        objectAnimator2.start();
    }

    @Override
    public void prepareForIn() {
        LogUtils.d("prepareForIn");
        Toolbar toolbar = ((ToolbarHolder) getActivity()).getToolbar();
        ImageView toolbarIcon = ((ToolbarHolder) getActivity()).getToolbarIcon();
        TextView toolbarTitle = ((ToolbarHolder) getActivity()).getToolbarTitle();

        toolbarIcon.setTranslationY(-1 * toolbar.getHeight());
        toolbarTitle.setTranslationY(-1 * toolbar.getHeight());

        int fabBottomMargin = ((ViewGroup.MarginLayoutParams) mChooseUserFAB.getLayoutParams()).bottomMargin;
        LogUtils.d("mChooseUserFAB.getHeight() == " + mChooseUserFAB.getHeight());
        mChooseUserFAB.setTranslationY(mChooseUserFAB.getHeight() + fabBottomMargin);
    }

    private void setViewAnimatorDisplayedChild(int position) {
        mRecyclerView.setVisibility(position == POSITION_EMPTY_VIEW ? View.VISIBLE : View.GONE);
        if (mViewAnimator.getDisplayedChild() != position) {
            mViewAnimator.setDisplayedChild(position);
        }
    }
}
