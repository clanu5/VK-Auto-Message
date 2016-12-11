package com.qwert2603.vkautomessage.base.navigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
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
import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.BaseFragment;
import com.qwert2603.vkautomessage.errors_show.ErrorsShowDialog;
import com.qwert2603.vkautomessage.login.MainActivity;
import com.qwert2603.vkautomessage.util.LogUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public abstract class NavigationFragment<P extends NavigationPresenter> extends BaseFragment<P> implements NavigationView {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    android.support.design.widget.NavigationView mNavigationView;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.toolbar_frame_layout)
    FrameLayout mToolbarFrameLayout;

    protected ImageView mToolbarIconImageView;

    @BindView(R.id.toolbar_title_text_view)
    protected TextView mToolbarTitleTextView;

    @BindView(R.id.coordinator)
    CoordinatorLayout mCoordinatorLayout;

    private ImageView mUserPhotoImageView;
    private TextView mUserNameTextView;

    @LayoutRes
    private int mActionContentRes = 0;

    @Inject
    RxBus mRxBus;

    private Subscription mRxBusSubscription = Subscriptions.unsubscribed();

    protected abstract boolean isNavigationButtonVisible();

    @LayoutRes
    protected abstract int getToolbarContentRes();

    @LayoutRes
    protected abstract int getScreenContentRes();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRxBusSubscription = mRxBus.toObservable()
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
                    getPresenter().onDrawerSlide(mNavigationView.getWidth(), 1.0f);
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
                    getPresenter().onLogOutClicked();
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
                getPresenter().onDrawerSlide(drawerView.getWidth(), slideOffset);
            }
        });

        mToolbar.setNavigationOnClickListener(v -> {
            if (mActionContentRes != 0) {
                stopActionMode();
                getPresenter().onActionModeCancelled();
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

        mUserPhotoImageView = (ImageView) headerNavigationView.findViewById(R.id.user_photo_image_view);
        mUserNameTextView = (TextView) headerNavigationView.findViewById(R.id.user_name_text_view);

        ActionBar supportActionBar = ((BaseActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            if (isNavigationButtonVisible()) {
                mToolbar.setNavigationIcon(R.drawable.icon_burger);
            } else {
                mToolbar.setNavigationIcon(R.drawable.icon_arrow);
            }
        }

        int size = mToolbar.getChildCount();
        for (int i = 0; i < size; i++) {
            View child = mToolbar.getChildAt(i);
            if (child instanceof ImageButton) {
                ImageButton btn = (ImageButton) child;
                if (btn.getDrawable() == mToolbar.getNavigationIcon()) {
                    mToolbarIconImageView = btn;
                    break;
                }
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
    public void showLogOut() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void showUserName(String userName) {
        mUserNameTextView.setText(userName);
    }

    @Override
    public ImageView getUserPhotoImageView() {
        return mUserPhotoImageView;
    }

    @Override
    public void showLoading() {
        mUserNameTextView.setText(R.string.loading);
        mUserPhotoImageView.setImageBitmap(null);
    }

    @Override
    public void setContentTranslationX(float translationX) {
        mCoordinatorLayout.setTranslationX(translationX);
    }

    protected View startActionMode(@LayoutRes int actionContentRes) {
        mActionContentRes = actionContentRes;
        View view = getActivity().getLayoutInflater().inflate(actionContentRes, null);
        mToolbarFrameLayout.addView(view);
        mToolbarIconImageView.setImageState(new int[]{R.attr.state_close}, true);
        return view;
    }

    protected void onActionModeRestored(View view) {
    }

    protected void stopActionMode() {
        mActionContentRes = 0;
        mToolbarFrameLayout.removeViewAt(1);
        mToolbarIconImageView.setImageState(new int[]{-R.attr.state_close}, true);
    }

    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (mActionContentRes != 0) {
            stopActionMode();
            getPresenter().onActionModeCancelled();
            return;
        }
        getActivity().overridePendingTransition(0, 0);
        getPresenter().onBackPressed();
    }

    @Override
    public void performBackPressed() {
        ((BaseActivity) getActivity()).performOnBackPressed();
    }
}
