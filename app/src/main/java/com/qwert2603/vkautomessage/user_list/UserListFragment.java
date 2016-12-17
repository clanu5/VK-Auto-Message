package com.qwert2603.vkautomessage.user_list;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.base.list.ListFragment;
import com.qwert2603.vkautomessage.choose_user.ChooseUserDialog;
import com.qwert2603.vkautomessage.delete_user.DeleteUserDialog;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.record_list.RecordListActivity;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;
import com.qwert2603.vkautomessage.util.AndroidUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListFragment extends ListFragment<User> implements UserListView {

    public static UserListFragment newInstance() {
        return new UserListFragment();
    }

    private static final int REQUEST_CHOOSE_USER = 3;

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

    @NonNull
    @Override
    protected BaseRecyclerViewAdapter<User, ?, ?> getAdapter() {
        return mUserListAdapter;
    }

    @Override
    protected int getToolbarContentRes() {
        return R.layout.toolbar_title;
    }

    @Override
    protected int getScreenContentRes() {
        return R.layout.fragment_user_list;
    }

    @Override
    protected boolean isNavigationButtonVisible() {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(UserListFragment.this);
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("deprecation")
    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(UserListFragment.this, view);

        mChooseUserFAB.setOnClickListener(v -> mUserListPresenter.onChooseUserClicked());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRecyclerItemAnimator.setEnterOrigin(RecyclerItemAnimator.EnterOrigin.BOTTOM);
        mSimpleOnItemTouchHelperCallback.setBackColor(getResources().getColor(R.color.swipe_back_user_list));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            mToolbarIconImageView.setTranslationY(-1 * mToolbar.getHeight());
            mToolbarTitleTextView.setTranslationY(-1 * mToolbar.getHeight());

            int fabBottomMargin = ((ViewGroup.MarginLayoutParams) mChooseUserFAB.getLayoutParams()).bottomMargin;
            mChooseUserFAB.setTranslationY(mChooseUserFAB.getHeight() + fabBottomMargin);

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
            case REQUEST_CHOOSE_USER:
                if (resultCode == Activity.RESULT_OK) {
                    int userId = data.getIntExtra(ChooseUserDialog.EXTRA_SELECTED_USER_ID, 0);
                    mUserListPresenter.onUserChosen(userId);
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void moveToDetailsForItem(User user/*, boolean withSetPressed*/) {
        ActivityOptions activityOptions = null;
        UserListAdapter.UserViewHolder viewHolder =
                (UserListAdapter.UserViewHolder) mRecyclerView.findViewHolderForItemId(user.getId());
        if (viewHolder != null) {
            activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                    Pair.create(viewHolder.mUsernameTextView, viewHolder.mUsernameTextView.getTransitionName()));
        }
        Intent intent = new Intent(getActivity(), RecordListActivity.class);
        intent.putExtra(RecordListActivity.EXTRA_ITEM_ID, user.getId());

        if (viewHolder != null) {
            //todo viewHolder.itemView.setPressed(withSetPressed);

            int[] startingPoint = new int[2];
            viewHolder.itemView.getLocationOnScreen(startingPoint);
            startingPoint[1] -= mToolbar.getHeight();
            intent.putExtra(RecordListActivity.EXTRA_DRAWING_START_Y, startingPoint[1]);
        }

        ActivityOptions finalActivityOptions = activityOptions;
        AndroidUtils.runOnUI(() -> startActivityForResult(intent, REQUEST_DETAILS_FOT_ITEM, finalActivityOptions != null ? finalActivityOptions.toBundle() : null), 400);

        animateOut();
    }

    @Override
    protected void onFirstContentShow(@Nullable List<User> list) {
        super.onFirstContentShow(list);
        animateInNewItemButton(mRecyclerItemAnimator.getEnterDelayPerScreen());
    }

    @Override
    public void showChooseUser() {
        ChooseUserDialog userListDialog = ChooseUserDialog.newInstance();
        userListDialog.setTargetFragment(UserListFragment.this, REQUEST_CHOOSE_USER);
        userListDialog.show(getFragmentManager(), userListDialog.getClass().getName());
    }

    @Override
    public void askDeleteItem(int id) {
        DeleteUserDialog deleteUserDialog = DeleteUserDialog.newInstance(id);
        deleteUserDialog.setTargetFragment(UserListFragment.this, REQUEST_DELETE_ITEM);
        deleteUserDialog.show(getFragmentManager(), deleteUserDialog.getClass().getName());
    }

    private void animateIn() {
        mToolbarIconImageView.animate().translationY(0).setStartDelay(50).setDuration(300);
        mToolbarTitleTextView.animate().translationY(0).setStartDelay(100).setDuration(300);
    }

    private void animateOut() {
        int fabBottomMargin = ((ViewGroup.MarginLayoutParams) mChooseUserFAB.getLayoutParams()).bottomMargin;
        mChooseUserFAB.animate().translationY(mChooseUserFAB.getHeight() + fabBottomMargin).setStartDelay(0).setDuration(300);

        mToolbarTitleTextView.animate().translationY(-1 * mToolbar.getHeight()).setStartDelay(0).setDuration(200);
        mToolbarIconImageView.animate().translationY(-1 * mToolbar.getHeight()).setStartDelay(100).setDuration(200);
    }

    private void animateInNewItemButton(int delay) {
        mChooseUserFAB.animate().translationY(0).setStartDelay(delay).setDuration(300);
    }

    @Override
    protected void performBackPressed() {
        animateOut();
        super.performBackPressed();
    }
}
