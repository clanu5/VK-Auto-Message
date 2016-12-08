package com.qwert2603.vkautomessage.base.navigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.RxBus;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.errors_show.ErrorsShowDialog;
import com.qwert2603.vkautomessage.login.MainActivity;
import com.qwert2603.vkautomessage.util.LogUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public abstract class NavigationActivity extends AppCompatActivity implements NavigationView, ActivityInterface {

    public static final String EXTRA_ITEM_ID = "com.qwert2603.vkautomessage.EXTRA_ITEM_ID";

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    android.support.design.widget.NavigationView mNavigationView;

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private boolean mIsNavigationButtonVisible;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbar_title_text_view)
    TextView mToolbarTitleTextView;

    @BindView(R.id.fragment_container)
    protected View mFragmentContainer;

    @BindView(R.id.coordinator)
    CoordinatorLayout mCoordinatorLayout;

    private ImageView mUserPhotoImageView;
    private TextView mUserNameTextView;

    @Inject
    NavigationPresenter mNavigationPresenter;

    @Inject
    RxBus mRxBus;

    private Subscription mRxBusSubscription = Subscriptions.unsubscribed();

    private OnBackPressedListener mOnBackPressedListener;

    protected abstract boolean isNavigationButtonVisible();

    protected abstract Fragment createFragment();

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        VkAutoMessageApplication.getAppComponent().inject(NavigationActivity.this);
        ButterKnife.bind(NavigationActivity.this, NavigationActivity.this);

        setSupportActionBar(mToolbar);

        mRxBusSubscription = mRxBus.toObservable()
                .filter(event -> event.mEvent == RxBus.Event.EVENT_MODE_SHOW_ERRORS_CHANGED)
                .subscribe(event -> {
                    if (event.mObject instanceof Boolean) {
                        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.show_errors);
                        menuItem.setVisible((Boolean) event.mObject);
                    }
                }, LogUtils::e);

        mNavigationView.setNavigationItemSelectedListener(item -> {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            switch (item.getItemId()) {
                case R.id.log_out:
                    mNavigationPresenter.onLogOutClicked();
                    return true;
                case R.id.show_errors:
                    ErrorsShowDialog.newInstance().show(getSupportFragmentManager(), "");
                    return true;
            }
            return false;
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                mNavigationPresenter.onDrawerSlide(drawerView.getWidth(), slideOffset);
            }
        });

        mIsNavigationButtonVisible = isNavigationButtonVisible();

        if (mIsNavigationButtonVisible) {
            mActionBarDrawerToggle = new ActionBarDrawerToggle(NavigationActivity.this, mDrawerLayout, R.string.open, R.string.close);
            mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

            mToolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat.START));

            mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            mActionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
        }

        View headerNavigationView = getLayoutInflater().inflate(R.layout.header_navigation, null);
        if (mNavigationView != null) {
            mNavigationView.addHeaderView(headerNavigationView);
        }

        mUserPhotoImageView = (ImageView) headerNavigationView.findViewById(R.id.user_photo_image_view);
        mUserNameTextView = (TextView) headerNavigationView.findViewById(R.id.user_name_text_view);

        mNavigationPresenter.bindView(this);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commitAllowingStateLoss();
        }

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            if (!mIsNavigationButtonVisible) {
                mToolbar.setNavigationIcon(R.drawable.back_arrow);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mIsNavigationButtonVisible) {
            mActionBarDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mIsNavigationButtonVisible) {
            mActionBarDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    protected void onDestroy() {
        mRxBusSubscription.unsubscribe();
        mNavigationPresenter.unbindView();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavigationPresenter.onViewReady();
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mDrawerLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                    mNavigationPresenter.onDrawerSlide(mNavigationView.getWidth(), 1.0f);
                    return true;
                }
            });
        }
    }

    @Override
    protected void onPause() {
        mNavigationPresenter.onViewNotReady();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (mOnBackPressedListener != null) {
                overridePendingTransition(0, 0);
                mOnBackPressedListener.onBackPressed();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mIsNavigationButtonVisible) {
                    onBackPressed();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showLogOut() {
        Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public TextView getToolbarTitle() {
        return mToolbarTitleTextView;
    }

    @Override
    public ImageView getToolbarIcon() {
        int size = mToolbar.getChildCount();
        for (int i = 0; i < size; i++) {
            View child = mToolbar.getChildAt(i);
            if (child instanceof ImageButton) {
                ImageButton btn = (ImageButton) child;
                if (btn.getDrawable() == mToolbar.getNavigationIcon()) {
                    return btn;
                }
            }
        }
        return null;
    }

    @Override
    public void setToolbarTitle(String title) {
        mToolbarTitleTextView.setText(title);
    }

    @Override
    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        mOnBackPressedListener = onBackPressedListener;
    }

    @Override
    public void performOnBackPressed() {
        super.onBackPressed();
    }
}
