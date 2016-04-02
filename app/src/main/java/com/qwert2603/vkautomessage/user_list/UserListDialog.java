package com.qwert2603.vkautomessage.user_list;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseDialog;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.List;

public class UserListDialog extends BaseDialog<UserListPresenter> implements UserListView {

    private static final String selectedUserIdKey = "selectedUserId";
    public static final String EXTRA_SELECTED_USER_ID = "com.qwert2603.vkautomessage.EXTRA_SELECTED_USER_ID";

    public static UserListDialog newInstance(int selectedUserId) {
        UserListDialog userListDialog = new UserListDialog();
        Bundle args = new Bundle();
        args.putInt(selectedUserIdKey, selectedUserId);
        userListDialog.setArguments(args);
        return userListDialog;
    }

    private static final int POSITION_REFRESH_LAYOUT = 0;
    private static final int POSITION_LOADING_TEXT_VIEW = 1;
    private static final int POSITION_ERROR_TEXT_VIEW = 2;
    private static final int POSITION_EMPTY_TEXT_VIEW = 3;

    private SwipeRefreshLayout mRefreshLayout;
    private ViewAnimator mViewAnimator;
    private RecyclerView mRecyclerView;

    @NonNull
    @Override
    protected UserListPresenter createPresenter() {
        return new UserListPresenter(getArguments().getInt(selectedUserIdKey));
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO: 31.03.2016 добавить поиск по списку друзей.
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_user_list, null);
        mViewAnimator = (ViewAnimator) view.findViewById(R.id.view_animator);

        mRefreshLayout = (SwipeRefreshLayout) mViewAnimator.getChildAt(POSITION_REFRESH_LAYOUT);
        mRefreshLayout.setOnRefreshListener(getPresenter()::onReload);
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mViewAnimator.getChildAt(POSITION_ERROR_TEXT_VIEW).setOnClickListener(v -> getPresenter().onReload());

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.submit), (dialog, which) -> getPresenter().onSubmitClicked())
                .create();
    }

    @Override
    public void setRefreshingConfig(boolean enable, boolean refreshing) {
        mRefreshLayout.setEnabled(enable);
        mRefreshLayout.post(() -> mRefreshLayout.setRefreshing(refreshing));
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
    public void setSelectedItemPosition(int position) {
        UserListAdapter adapter = (UserListAdapter) mRecyclerView.getAdapter();
        if (adapter != null) {
            adapter.setSelectedItemPosition(position);
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
    public void showList(List<VKApiUserFull> list) {
        setViewAnimatorDisplayedChild(POSITION_REFRESH_LAYOUT);
        UserListAdapter adapter = (UserListAdapter) mRecyclerView.getAdapter();
        if (adapter != null && adapter.isShowingList(list)) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new UserListAdapter(list);
            adapter.setCallbacks(position -> getPresenter().onUserAtPositionClicked(position));
            mRecyclerView.setAdapter(adapter);

        }
    }

    private void setViewAnimatorDisplayedChild(int position) {
        if (mViewAnimator.getDisplayedChild() != position) {
            mViewAnimator.setDisplayedChild(position);
        }
    }

}
