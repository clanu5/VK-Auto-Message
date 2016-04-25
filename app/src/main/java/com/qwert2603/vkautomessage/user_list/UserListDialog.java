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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseDialog;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

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
    private static final int POSITION_NOTHING_FOUND_TEXT_VIEW = 4;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.view_animator)
    ViewAnimator mViewAnimator;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.search_edit_text)
    EditText mSearchEditText;

    @Inject
    UserListPresenter mUserListPresenter;

    @NonNull
    @Override
    protected UserListPresenter getPresenter() {
        return mUserListPresenter;
    }

    @Override
    protected void setPresenter(@NonNull UserListPresenter presenter) {
        mUserListPresenter = presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(UserListDialog.this);
        mUserListPresenter.setSelectedUserId(getArguments().getInt(selectedUserIdKey));
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_user_list, null);

        ButterKnife.bind(UserListDialog.this, view);

        mRefreshLayout.setOnRefreshListener(mUserListPresenter::onReload);
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mViewAnimator.getChildAt(POSITION_ERROR_TEXT_VIEW).setOnClickListener(v -> mUserListPresenter.onReload());

        mSearchEditText.setText(mUserListPresenter.getCurrentQuery());
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUserListPresenter.onSearchQueryChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.submit), (dialog, which) -> mUserListPresenter.onSubmitClicked())
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
    public void showNothingFound() {
        setViewAnimatorDisplayedChild(POSITION_NOTHING_FOUND_TEXT_VIEW);
    }

    @Override
    public void showListWithSelectedItem(List<VKApiUserFull> list, int selectedPosition) {
        setViewAnimatorDisplayedChild(POSITION_REFRESH_LAYOUT);
        UserListAdapter adapter = (UserListAdapter) mRecyclerView.getAdapter();
        if (adapter != null && adapter.isShowingList(list)) {
            adapter.notifyDataSetChanged();
            adapter.setSelectedItemPosition(selectedPosition);
        } else {
            adapter = new UserListAdapter(list, selectedPosition);
            adapter.setClickCallbacks(mUserListPresenter::onUserAtPositionClicked);
            mRecyclerView.setAdapter(adapter);
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
        showListWithSelectedItem(list, -1);
    }

    private void setViewAnimatorDisplayedChild(int position) {
        if (mViewAnimator.getDisplayedChild() != position) {
            mViewAnimator.setDisplayedChild(position);
        }
    }

}
