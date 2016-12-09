package com.qwert2603.vkautomessage.user_list;

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
import android.support.v7.widget.DividerItemDecoration;
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
import com.qwert2603.vkautomessage.base.navigation.ActivityInterface;
import com.qwert2603.vkautomessage.record_list.RecordListActivity;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;
import com.qwert2603.vkautomessage.util.AndroidUtils;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void moveToDetailsForItem(int userId, boolean withSetPressed) {
        ActivityOptions activityOptions = null;
        UserListAdapter.UserViewHolder viewHolder =
                (UserListAdapter.UserViewHolder) mRecyclerView.findViewHolderForItemId(userId);
        if (viewHolder != null && AndroidUtils.isLollipopOrHigher()) {
            activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                    Pair.create(viewHolder.mUsernameTextView, viewHolder.mUsernameTextView.getTransitionName()));
        }
        Intent intent = new Intent(getActivity(), RecordListActivity.class);
        intent.putExtra(RecordListActivity.EXTRA_ITEM_ID, userId);

        if (viewHolder != null) {
            viewHolder.itemView.setPressed(withSetPressed);

            int[] startingPoint = new int[2];
            viewHolder.itemView.getLocationOnScreen(startingPoint);
            startingPoint[1] -= ((ActivityInterface) getActivity()).getToolbar().getHeight();
            intent.putExtra(RecordListActivity.EXTRA_DRAWING_START_Y, startingPoint[1]);
        }

        startActivityForResult(intent, REQUEST_DETAILS_FOT_ITEM, activityOptions != null ? activityOptions.toBundle() : null);

        getActivity().overridePendingTransition(0, 0);
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
    protected Animator createEnterAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                Toolbar toolbar = ((ActivityInterface) getActivity()).getToolbar();
                ImageView toolbarIcon = ((ActivityInterface) getActivity()).getToolbarIcon();
                TextView toolbarTitle = ((ActivityInterface) getActivity()).getToolbarTitle();

                toolbarIcon.setTranslationY(-1 * toolbar.getHeight());
                toolbarTitle.setTranslationY(-1 * toolbar.getHeight());

                int fabBottomMargin = ((ViewGroup.MarginLayoutParams) mChooseUserFAB.getLayoutParams()).bottomMargin;
                mChooseUserFAB.setTranslationY(mChooseUserFAB.getHeight() + fabBottomMargin);
            }
        });
        return animatorSet;
    }

    @Override
    protected Animator createExitAnimator() {
        return new AnimatorSet();
    }

    @Override
    protected Animator createInAnimator(boolean withLargeDelay) {
        ImageView toolbarIcon = ((ActivityInterface) getActivity()).getToolbarIcon();
        TextView toolbarTitle = ((ActivityInterface) getActivity()).getToolbarTitle();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(toolbarIcon, "translationY", 0);
        objectAnimator.setStartDelay(withLargeDelay ? 300 : 50);
        objectAnimator.setDuration(300);

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(toolbarTitle, "translationY", 0);
        objectAnimator1.setStartDelay(withLargeDelay ? 100 : 100);
        objectAnimator1.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(objectAnimator).with(objectAnimator1);
        return animatorSet;
    }

    @Override
    protected Animator createOutAnimator() {
        int fabBottomMargin = ((ViewGroup.MarginLayoutParams) mChooseUserFAB.getLayoutParams()).bottomMargin;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mChooseUserFAB, "translationY", mChooseUserFAB.getHeight() + fabBottomMargin);
        objectAnimator.setDuration(300);
        objectAnimator.setInterpolator(new OvershootInterpolator());

        Toolbar toolbar = ((ActivityInterface) getActivity()).getToolbar();
        ImageView toolbarIcon = ((ActivityInterface) getActivity()).getToolbarIcon();
        TextView toolbarTitle = ((ActivityInterface) getActivity()).getToolbarTitle();

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
    public void animateInNewItemButton(int delay) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mChooseUserFAB, "translationY", 0);
        objectAnimator.setStartDelay(delay);
        objectAnimator.setDuration(300);
        objectAnimator.setInterpolator(new OvershootInterpolator());
        objectAnimator.start();
    }
}
