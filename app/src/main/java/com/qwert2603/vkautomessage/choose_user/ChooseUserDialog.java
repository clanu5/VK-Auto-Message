package com.qwert2603.vkautomessage.choose_user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.qwert2603.vkautomessage.model.VkUser;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseUserDialog extends BaseDialog<ChooseUserPresenter> implements ChooseUserView {

    public static final String EXTRA_SELECTED_USER_ID = "com.qwert2603.vkautomessage.EXTRA_SELECTED_USER_ID";

    public static ChooseUserDialog newInstance() {
        return new ChooseUserDialog();
    }

    private static final int POSITION_EMPTY_VIEW = 0;
    private static final int POSITION_LOADING_TEXT_VIEW = 1;
    private static final int POSITION_ERROR_TEXT_VIEW = 2;
    private static final int POSITION_EMPTY_TEXT_VIEW = 3;
    private static final int POSITION_NOTHING_FOUND_TEXT_VIEW = 4;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.view_animator)
    ViewAnimator mViewAnimator;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.search_edit_text)
    EditText mSearchEditText;

    protected RecyclerItemAnimator mRecyclerItemAnimator;

    @Inject
    ChooseUserPresenter mChooseUserPresenter;

    @Inject
    ChooseUserAdapter mChooseUserAdapter;

    private boolean mSubmitResultSent = false;

    @NonNull
    @Override
    protected ChooseUserPresenter getPresenter() {
        return mChooseUserPresenter;
    }

    @Override
    protected void setPresenter(@NonNull ChooseUserPresenter presenter) {
        mChooseUserPresenter = presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(ChooseUserDialog.this);
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_choose_user, null);

        ButterKnife.bind(ChooseUserDialog.this, view);

        mRefreshLayout.setOnRefreshListener(mChooseUserPresenter::onReloadList);
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mChooseUserAdapter);

        mRecyclerItemAnimator = new RecyclerItemAnimator();
        mRecyclerItemAnimator.setEnterOrigin(RecyclerItemAnimator.EnterOrigin.LEFT_OR_RIGHT);
        mRecyclerItemAnimator.setAnimateEnterMode(RecyclerItemAnimator.AnimateEnterMode.ALL);
        mRecyclerItemAnimator.setAddDuration(500);
        mRecyclerView.setItemAnimator(mRecyclerItemAnimator);

        mChooseUserAdapter.setClickCallback(mChooseUserPresenter::onItemAtPositionClicked);
        mChooseUserAdapter.setLongClickCallback(mChooseUserPresenter::onItemAtPositionLongClicked);

        mViewAnimator.getChildAt(POSITION_ERROR_TEXT_VIEW).setOnClickListener(v -> mChooseUserPresenter.onReloadList());

        mSearchEditText.setText(mChooseUserPresenter.getCurrentQuery());
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mChooseUserPresenter.onSearchQueryChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(R.string.cancel, (dialog, which) -> mChooseUserPresenter.onCancelClicked())
                .create();
    }

    @Override
    public void onDestroyView() {
        mRecyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (!mSubmitResultSent) {
            getPresenter().onCancelClicked();
        }
        super.onDestroy();
    }

    @Override
    public void setRefreshingConfig(boolean enable, boolean refreshing) {
        mRefreshLayout.setEnabled(enable);
        mRefreshLayout.post(() -> mRefreshLayout.setRefreshing(refreshing));
    }

    @Override
    public void submitDode(int userId) {
        mSubmitResultSent = true;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SELECTED_USER_ID, userId);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        dismissAllowingStateLoss();
    }

    @Override
    public void submitCancel() {
        mSubmitResultSent = true;
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
    }

    @Override
    public void showCantWrite() {
        Toast.makeText(getActivity(), R.string.cant_write_text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setItemSelectionState(int position, boolean select) {
        LogUtils.e(new RuntimeException("Should not be called!"));
    }

    @Override
    public void selectAllItems() {
        LogUtils.e(new RuntimeException("Should not be called!"));
    }

    @Override
    public void unSelectAllItems() {
        LogUtils.e(new RuntimeException("Should not be called!"));
    }

    @Override
    public void startListSelectionMode() {
        LogUtils.e(new RuntimeException("Should not be called!"));
    }

    @Override
    public void stopListSelectionMode() {
        LogUtils.e(new RuntimeException("Should not be called!"));
    }

    @Override
    public void showSelectedItemsCount(int count) {
        LogUtils.e(new RuntimeException("Should not be called!"));
    }

    @Override
    public void showItemsDeleted(int count) {
        LogUtils.e(new RuntimeException("Should not be called!"));
    }

    @Override
    public void showDontWriteToDeveloper() {
        Toast.makeText(getActivity(), R.string.toast_dont_write_to_developer, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showGreatChoice() {
        Toast.makeText(getActivity(), R.string.toast_great_choice, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNothingFound() {
        mChooseUserAdapter.replaceModelList(new ArrayList<>());
        setViewAnimatorDisplayedChild(POSITION_NOTHING_FOUND_TEXT_VIEW);
    }

    @Override
    public void showLoading() {
        mChooseUserAdapter.replaceModelList(new ArrayList<>());
        setViewAnimatorDisplayedChild(POSITION_LOADING_TEXT_VIEW);
    }

    @Override
    public void showError() {
        mChooseUserAdapter.replaceModelList(new ArrayList<>());
        setViewAnimatorDisplayedChild(POSITION_ERROR_TEXT_VIEW);
    }

    @Override
    public void showEmpty() {
        mChooseUserAdapter.replaceModelList(new ArrayList<>());
        setViewAnimatorDisplayedChild(POSITION_EMPTY_TEXT_VIEW);
    }

    /**
     * Whether item listFromModel was show earlier.
     */
    private boolean mListEverShown = false;

    @Override
    public void showList(@NonNull List<VkUser> list) {
        if (!mListEverShown) {
            mListEverShown = true;
            mRecyclerItemAnimator.setDelayEnter(true);
        } else {
            mRecyclerItemAnimator.setDelayEnter(false);
        }
        setViewAnimatorDisplayedChild(POSITION_EMPTY_VIEW);
        mChooseUserAdapter.replaceModelList(list);
    }

    @Override
    public void updateItem(int position) {
        mChooseUserAdapter.notifyItemChanged(position);
    }

    @Override
    public void moveToDetailsForItem(int itemId, boolean newItem, int itemPosition) {
    }

    @Override
    public void askDeleteItem(int id) {
        mChooseUserPresenter.onItemDeleteCanceled(id);
    }

    @Override
    public void scrollToTop() {
        mRecyclerView.scrollToPosition(0);
    }

    private void setViewAnimatorDisplayedChild(int position) {
        if (mViewAnimator.getDisplayedChild() != position) {
            mViewAnimator.setDisplayedChild(position);
        }
        mViewAnimator.setVisibility(position != POSITION_EMPTY_VIEW ? View.VISIBLE : View.INVISIBLE);
//        mRecyclerView.setVisibility((position == POSITION_EMPTY_VIEW) ? View.VISIBLE : View.INVISIBLE);
    }

}
