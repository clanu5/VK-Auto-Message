package com.qwert2603.vkautomessage.base.navigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.BaseFragment;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.errors_show.ErrorsShowDialog;
import com.qwert2603.vkautomessage.login.MainActivity;
import com.qwert2603.vkautomessage.util.LogUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public abstract class NavigationFragment<P extends BasePresenter> extends BaseFragment<P> implements NavigationView {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    android.support.design.widget.NavigationView mNavigationView;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.app_bar_layout)
    protected AppBarLayout mAppBarLayout;

    @BindView(R.id.toolbar_frame_layout)
    FrameLayout mToolbarFrameLayout;

    protected ImageView mToolbarIconImageView;

    @BindView(R.id.toolbar_title_text_view)
    protected TextView mToolbarTitleTextView;

    @BindView(R.id.coordinator)
    CoordinatorLayout mCoordinatorLayout;

    private ImageView mMyselfPhotoImageView;
    private TextView mMyselfNameTextView;

    @LayoutRes
    private int mActionContentRes = 0;

    @ToolbarIconState
    private int mIconState;

    /**
     * Потому что Dagger не может инжектить в NavigationPresenter, который generic.
     */
    public static final class InjectionsHolder {
        @Inject
        RxBus mRxBus;

        @Inject
        NavigationPresenter mNavigationPresenter;

        InjectionsHolder() {
            VkAutoMessageApplication.getAppComponent().inject(NavigationFragment.InjectionsHolder.this);
        }
    }

    private InjectionsHolder mInjectionsHolder;

    private Subscription mRxBusSubscription = Subscriptions.unsubscribed();

    protected abstract boolean isNavigationButtonVisible();

    @LayoutRes
    protected abstract int getToolbarContentRes();

    @LayoutRes
    protected abstract int getScreenContentRes();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInjectionsHolder = new InjectionsHolder();

        mRxBusSubscription = mInjectionsHolder.mRxBus.toObservable()
                .filter(event -> event.mEvent == RxBus.Event.EVENT_MODE_SHOW_ERRORS_CHANGED)
                .subscribe(event -> {
                    if (event.mObject instanceof Boolean) {
                        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.show_errors);
                        menuItem.setVisible((Boolean) event.mObject);
                    }
                }, LogUtils::e);
    }

    @Override
    public void onDestroy() {
        mRxBusSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mDrawerLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                    NavigationFragment.this.onDrawerSlide(mNavigationView.getWidth(), 1.0f);
                    return true;
                }
            });
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        inflater.inflate(getToolbarContentRes(), (ViewGroup) view.findViewById(R.id.toolbar_frame_layout), true);
        inflater.inflate(getScreenContentRes(), (ViewGroup) view.findViewById(R.id.coordinator), true);

        ButterKnife.bind(NavigationFragment.this, view);

        ((BaseActivity) getActivity()).setSupportActionBar(mToolbar);

        mNavigationView.setNavigationItemSelectedListener(item -> {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            switch (item.getItemId()) {
                case R.id.log_out:
                    mInjectionsHolder.mNavigationPresenter.onLogOutClicked();
                    return true;
                case R.id.show_errors:
                    ErrorsShowDialog.newInstance().show(getFragmentManager(), "");
                    return true;
            }
            return false;
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                NavigationFragment.this.onDrawerSlide(drawerView.getWidth(), slideOffset);
            }
        });

        mToolbar.setNavigationOnClickListener(v -> {
            if (mActionContentRes != 0) {
                stopActionMode();
                onActionModeCancelled();
                return;
            }
            if (isNavigationButtonVisible()) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            } else {
                onBackPressed();
            }
        });

        View headerNavigationView = inflater.inflate(R.layout.header_navigation, null);
        mNavigationView.addHeaderView(headerNavigationView);

        mMyselfPhotoImageView = (ImageView) headerNavigationView.findViewById(R.id.user_photo_image_view);
        mMyselfNameTextView = (TextView) headerNavigationView.findViewById(R.id.user_name_text_view);

        ActionBar supportActionBar = ((BaseActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.toolbar_icon);

            int size = mToolbar.getChildCount();
            for (int i = 0; i < size; i++) {
                View child = mToolbar.getChildAt(i);
                if (child instanceof ImageButton) {
                    ImageButton btn = (ImageButton) child;
                    if (btn.getDrawable() == mToolbar.getNavigationIcon()) {
                        mToolbarIconImageView = btn;
                        mToolbarIconImageView.setTransitionName("mToolbarIconImageView");
                        break;
                    }
                }
            }

            if (isNavigationButtonVisible()) {
                setToolbarIconState(R.attr.state_burger, true);
            } else {
                setToolbarIconState(R.attr.state_back_arrow, true);
            }
        }

        if (mActionContentRes != 0) {
            View actionModeView = startActionMode(mActionContentRes);
            mToolbarIconImageView.jumpDrawablesToCurrentState();
            onActionModeRestored(actionModeView);
        }

        return view;
    }

    @Override
    public void performLogOut() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        // TODO: 16.12.2016 ??? intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void showMyselfName(String userName) {
        mMyselfNameTextView.setText(userName);
    }

    @Override
    public ImageView getMyselfPhotoImageView() {
        return mMyselfPhotoImageView;
    }

    @Override
    public void showLoadingMyself() {
        mMyselfNameTextView.setText(R.string.loading);
        mMyselfPhotoImageView.setImageBitmap(null);
    }

    protected View startActionMode(@LayoutRes int actionContentRes) {
        mActionContentRes = actionContentRes;
        View view = getActivity().getLayoutInflater().inflate(actionContentRes, null);
        mToolbarFrameLayout.addView(view);
        setToolbarIconState(R.attr.state_close, false);

        mToolbarFrameLayout.getChildAt(0).setVisibility(View.INVISIBLE);
        // TODO: 19.12.2016 animate color change (TransitionManager.beginDelayedTransition();)
        mToolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.actionMode));
        return view;
    }

    protected void onActionModeRestored(View view) {
    }

    protected void onActionModeCancelled() {
    }

    protected void stopActionMode() {
        mActionContentRes = 0;
        mToolbarFrameLayout.removeViewAt(1);
        if (isNavigationButtonVisible()) {
            setToolbarIconState(R.attr.state_burger, false);
        } else {
            setToolbarIconState(R.attr.state_back_arrow, false);
        }

        mToolbarFrameLayout.getChildAt(0).setVisibility(View.VISIBLE);
        mToolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
    }

    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (mActionContentRes != 0) {
            stopActionMode();
            onActionModeCancelled();
            return;
        }
        performBackPressed();
    }

    protected void performBackPressed() {
        ((BaseActivity) getActivity()).performOnBackPressed();
    }

    protected void setToolbarIconState(@ToolbarIconState int state, boolean withJump) {
        mIconState = state;
        int[] newState = new int[ToolbarIconState.STATES.length];
        for (int i = 0; i < ToolbarIconState.STATES.length; i++) {
            if (state == ToolbarIconState.STATES[i]) {
                newState[i] = ToolbarIconState.STATES[i];
            } else {
                newState[i] = -1 * ToolbarIconState.STATES[i];
            }
        }
        mToolbarIconImageView.setImageState(newState, true);
        if (withJump) {
            mToolbarIconImageView.jumpDrawablesToCurrentState();
        }
    }

    @ToolbarIconState
    @SuppressWarnings("unused")
    protected int getIconState() {
        return mIconState;
    }

    private void onDrawerSlide(int width, float slideOffset) {
        mCoordinatorLayout.setTranslationX(width * slideOffset / 2);
    }
}
