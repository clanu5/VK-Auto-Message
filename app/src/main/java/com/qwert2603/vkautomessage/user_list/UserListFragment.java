package com.qwert2603.vkautomessage.user_list;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseFragment;
import com.qwert2603.vkautomessage.choose_user.ChooseUserDialog;
import com.qwert2603.vkautomessage.delete_user.DeleteUserDialog;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.record_list.RecordListActivity;
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

    private static final int POSITION_RECYCLER_VIEW = 0;
    private static final int POSITION_LOADING_TEXT_VIEW = 1;
    private static final int POSITION_ERROR_TEXT_VIEW = 2;
    private static final int POSITION_EMPTY_TEXT_VIEW = 3;

    private static final int REQUEST_CHOOSE_USER = 1;
    private static final int REQUEST_DELETE_USER = 2;

    @BindView(R.id.view_animator)
    ViewAnimator mViewAnimator;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.new_record_fab)
    FloatingActionButton mNewRecordFAB;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        ButterKnife.bind(UserListFragment.this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mUserListAdapter);

        mUserListAdapter.setClickCallback(mUserListPresenter::onUserAtPositionClicked);
        mUserListAdapter.setLongClickCallback(mUserListPresenter::onUserAtPositionLongClicked);
        mUserListAdapter.setItemSwipeDismissCallback(position -> {
            LogUtils.d("UserListFragment setItemSwipeDismissCallback" + position);
            mUserListAdapter.notifyItemChanged(position);
            mUserListPresenter.onUserDismissed(position);
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleOnItemTouchHelperCallback(mUserListAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mViewAnimator.getChildAt(POSITION_ERROR_TEXT_VIEW).setOnClickListener(v -> mUserListPresenter.onReload());

        mNewRecordFAB.setOnClickListener(v -> mUserListPresenter.onChooseUserClicked());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserListPresenter.onResume();
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
    public void showList(List<User> list) {
        setViewAnimatorDisplayedChild(POSITION_RECYCLER_VIEW);
        mUserListAdapter.setModelList(list);
    }

    @Override
    public void moveToRecordsForUser(int userId) {
        ActivityOptions activityOptions = null;
        UserListAdapter.UserViewHolder viewHolder =
                (UserListAdapter.UserViewHolder) mRecyclerView.findViewHolderForItemId(userId);
        if (viewHolder != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TextView usernameTextView = viewHolder.mUsernameTextView;
            activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                    Pair.create(usernameTextView, usernameTextView.getTransitionName()));
        }
        Intent intent = new Intent(getActivity(), RecordListActivity.class);
        intent.putExtra(RecordListActivity.EXTRA_USER_ID, userId);
        startActivity(intent, activityOptions != null ? activityOptions.toBundle() : null);
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

    private void setViewAnimatorDisplayedChild(int position) {
        if (mViewAnimator.getDisplayedChild() != position) {
            mViewAnimator.setDisplayedChild(position);
        }
    }
}
