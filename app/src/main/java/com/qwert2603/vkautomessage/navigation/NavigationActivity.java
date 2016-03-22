package com.qwert2603.vkautomessage.navigation;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.login.MainActivity;

public abstract class NavigationActivity extends AppCompatActivity implements DrawerView {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private boolean mIsNavigationButtonVisible;

    private DrawerPresenter mDrawerPresenter;

    protected abstract boolean isNavigationButtonVisible();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerPresenter = new DrawerPresenter();
        mDrawerPresenter.bindView(this);
        mDrawerPresenter.onViewReady();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            mDrawerLayout.closeDrawers();
            switch (item.getItemId()) {
                case R.id.settings:
                    mDrawerPresenter.onSettingsClicked();
                    return true;
                case R.id.log_out:
                    mDrawerPresenter.onLogOutClicked();
                    return true;
            }
            return false;
        });

        mIsNavigationButtonVisible = isNavigationButtonVisible();

        if (mIsNavigationButtonVisible) {
            mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
            mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
            mToolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat.START));
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
        mDrawerPresenter.onViewNotReady();
        mDrawerPresenter.unbindView();
        super.onDestroy();
    }

    @Override
    public void showSettings() {
        Snackbar.make(mDrawerLayout, "SNACK", Snackbar.LENGTH_SHORT).show();
        // TODO: 18.03.2016
    }

    @Override
    public void showLogOut() {
        Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
