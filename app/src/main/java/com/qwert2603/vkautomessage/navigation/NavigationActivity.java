package com.qwert2603.vkautomessage.navigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.login.MainActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class NavigationActivity extends AppCompatActivity implements NavigationView {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    android.support.design.widget.NavigationView mNavigationView;

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private boolean mIsNavigationButtonVisible;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private ImageView mUserPhotoImageView;
    private TextView mUserNameTextView;

    @Inject
    NavigationPresenter mNavigationPresenter;

    protected abstract boolean isNavigationButtonVisible();

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        VkAutoMessageApplication.getAppComponent().inject(NavigationActivity.this);
        ButterKnife.bind(NavigationActivity.this, NavigationActivity.this);

        setSupportActionBar(mToolbar);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(item -> {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                switch (item.getItemId()) {
                    /*case R.id.settings:
                    // todo добавить этот пункт меню
                        mNavigationPresenter.onSettingsClicked();
                        return true;*/
                    case R.id.log_out:
                        mNavigationPresenter.onLogOutClicked();
                        return true;
                }
                return false;
            });
        }

        mIsNavigationButtonVisible = isNavigationButtonVisible();

        if (mIsNavigationButtonVisible) {
            mActionBarDrawerToggle = new ActionBarDrawerToggle(NavigationActivity.this, mDrawerLayout, R.string.open, R.string.close);
            mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
            if (mToolbar != null) {
                mToolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat.START));
            }
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
        mNavigationPresenter.onViewReady();
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
        mNavigationPresenter.onViewNotReady();
        mNavigationPresenter.unbindView();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showSettings() {
        Snackbar.make(mDrawerLayout, "SNACK", Snackbar.LENGTH_SHORT).show();
        // TODO: 18.03.2016 запускать Активити с настройками
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
}
