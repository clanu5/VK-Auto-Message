package com.qwert2603.vkautomessage.user_list;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.base.list.ListFragment;
import com.qwert2603.vkautomessage.choose_user.ChooseUserDialog;
import com.qwert2603.vkautomessage.delete_user.DeleteUserDialog;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.navigation.ToolbarHolder;
import com.qwert2603.vkautomessage.record_list.RecordListActivity;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;

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
    protected int getLayoutRes() {
        return R.layout.fragment_user_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(UserListFragment.this);
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(UserListFragment.this, view);

        mChooseUserFAB.setOnClickListener(v -> mUserListPresenter.onChooseUserClicked());

        RecyclerItemAnimator recyclerItemAnimator = new RecyclerItemAnimator();
        mRecyclerView.setItemAnimator(recyclerItemAnimator);

        return view;
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
        }
    }

    @Override
    public void moveToDetailsForItem(int userId) {
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
        startActivityForResult(intent, REQUEST_DETAILS_FOT_ITEM, activityOptions != null ? activityOptions.toBundle() : null);
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

    @Override
    public void scrollListToTop() {
        super.scrollListToTop();
        ObjectAnimator.ofFloat(mChooseUserFAB, "translationY", 0).start();
    }

    @Override
    protected Animator createInAnimator(boolean withLargeDelay) {
        ImageView toolbarIcon = ((ToolbarHolder) getActivity()).getToolbarIcon();
        TextView toolbarTitle = ((ToolbarHolder) getActivity()).getToolbarTitle();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(toolbarIcon, "translationY", 0);
        objectAnimator.setStartDelay(withLargeDelay ? 400 : 100);
        objectAnimator.setDuration(400);

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(toolbarTitle, "translationY", 0);
        objectAnimator1.setStartDelay(withLargeDelay ? 100 : 100);
        objectAnimator1.setDuration(400);

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(mChooseUserFAB, "translationY", 0);
        objectAnimator2.setStartDelay(withLargeDelay ? 1000 : 100);
        objectAnimator2.setDuration(400);
        objectAnimator2.setInterpolator(new OvershootInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(objectAnimator).with(objectAnimator1).with(objectAnimator2);
        return animatorSet;
    }

    @Override
    protected Animator createOutAnimator() {
        int fabBottomMargin = ((ViewGroup.MarginLayoutParams) mChooseUserFAB.getLayoutParams()).bottomMargin;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mChooseUserFAB, "translationY", mChooseUserFAB.getHeight() + fabBottomMargin);
        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new OvershootInterpolator());

        Toolbar toolbar = ((ToolbarHolder) getActivity()).getToolbar();
        ImageView toolbarIcon = ((ToolbarHolder) getActivity()).getToolbarIcon();
        TextView toolbarTitle = ((ToolbarHolder) getActivity()).getToolbarTitle();

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(toolbarTitle, "translationY", -1 * toolbar.getHeight());
        objectAnimator1.setDuration(200);

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(toolbarIcon, "translationY", -1 * toolbar.getHeight());
        objectAnimator2.setStartDelay(100);
        objectAnimator2.setDuration(200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(objectAnimator).with(objectAnimator1).with(objectAnimator2);
        return animatorSet;
    }

    @Override
    public void prepareForIn() {
        Toolbar toolbar = ((ToolbarHolder) getActivity()).getToolbar();
        ImageView toolbarIcon = ((ToolbarHolder) getActivity()).getToolbarIcon();
        TextView toolbarTitle = ((ToolbarHolder) getActivity()).getToolbarTitle();

        toolbarIcon.setTranslationY(-1 * toolbar.getHeight());
        toolbarTitle.setTranslationY(-1 * toolbar.getHeight());

        int fabBottomMargin = ((ViewGroup.MarginLayoutParams) mChooseUserFAB.getLayoutParams()).bottomMargin;
        mChooseUserFAB.setTranslationY(mChooseUserFAB.getHeight() + fabBottomMargin);
    }

}
