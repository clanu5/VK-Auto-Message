package com.qwert2603.vkautomessage.user_list;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.List;

public class UserListDialog extends DialogFragment implements UserListView {

    private static final String selectedUserIdKey = "selectedUserId";
    public static final String EXTRA_SELECTED_USER_ID = "com.qwert2603.vkautomessage.EXTRA_SELECTED_USER_ID";

    public static UserListDialog newInstance(int selectedUserId) {
        UserListDialog userListDialog = new UserListDialog();
        Bundle args = new Bundle();
        args.putInt(selectedUserIdKey, selectedUserId);
        userListDialog.setArguments(args);
        return userListDialog;
    }

    private static final int POSITION_RECYCLER_VIEW = 0;
    @SuppressWarnings("unused")
    private static final int POSITION_LOADING_TEXT_VIEW = 1;
    private static final int POSITION_ERROR_TEXT_VIEW = 2;
    private static final int POSITION_EMPTY_TEXT_VIEW = 3;

    private UserListPresenter mUserListPresenter;

    private SwipeRefreshLayout mRefreshLayout;
    private ViewAnimator mViewAnimator;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mUserListPresenter = new UserListPresenter(getArguments().getInt(selectedUserIdKey));
        mUserListPresenter.bindView(this);
    }

    @Override
    public void onDestroy() {
        mUserListPresenter.unbindView();
        super.onDestroy();
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_user_list, null);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(() -> mUserListPresenter.onReload());
        mViewAnimator = (ViewAnimator) view.findViewById(R.id.view_animator);
        mRecyclerView = (RecyclerView) mViewAnimator.getChildAt(POSITION_RECYCLER_VIEW);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ((TextView) mViewAnimator.getChildAt(POSITION_EMPTY_TEXT_VIEW)).setText(R.string.no_friends);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.submit), (dialog, which) -> mUserListPresenter.onSubmitClicked())
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        mUserListPresenter.onViewReady();
    }

    @Override
    public void onStop() {
        mUserListPresenter.onViewNotReady();
        super.onStop();
    }

    @Override
    public void submitDode(int userId) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SELECTED_USER_ID, userId);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }

    @Override
    public void showCantWrite() {
        Toast.makeText(getActivity(), R.string.cant_write_text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        setRefreshLayoutRefreshing(true);
    }

    @Override
    public void showError() {
        setRefreshLayoutRefreshing(false);
        setViewAnimatorDisplayedChild(POSITION_ERROR_TEXT_VIEW);
    }

    @Override
    public void showEmpty() {
        setRefreshLayoutRefreshing(false);
        setViewAnimatorDisplayedChild(POSITION_EMPTY_TEXT_VIEW);
    }

    @Override
    public void showList(List<VKApiUserFull> list) {
        setRefreshLayoutRefreshing(false);
        setViewAnimatorDisplayedChild(POSITION_RECYCLER_VIEW);
        UserListAdapter adapter = (UserListAdapter) mRecyclerView.getAdapter();
        if (adapter != null && adapter.isShowingList(list)) {
            adapter.notifyDataSetChanged();
        } else {
            mRecyclerView.setAdapter(new UserListAdapter(list, mUserListPresenter));
        }
    }

    private void setViewAnimatorDisplayedChild(int position) {
        if (mViewAnimator.getDisplayedChild() != position) {
            mViewAnimator.setDisplayedChild(position);
        }
    }

    private void setRefreshLayoutRefreshing(boolean refreshing) {
        mRefreshLayout.post(() -> mRefreshLayout.setRefreshing(refreshing));
    }
}
