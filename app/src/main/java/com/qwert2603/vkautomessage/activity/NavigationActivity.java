package com.qwert2603.vkautomessage.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.presenter.DrawerPresenter;
import com.qwert2603.vkautomessage.util.VkApiUtils;
import com.qwert2603.vkautomessage.view.DrawerView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NavigationActivity extends AppCompatActivity implements DrawerView {

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.navigation_view)
    NavigationView mNavigationView;

    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private DrawerPresenter mDrawerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        ButterKnife.bind(this);

        mDrawerPresenter = new DrawerPresenter();
        mDrawerPresenter.bindView(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        mNavigationView.setNavigationItemSelectedListener(item -> {
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
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        mDrawerPresenter.unbindView();
        super.onDestroy();
    }

    public DrawerPresenter getDrawerPresenter() {
        return mDrawerPresenter;
    }

    @Override
    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void showSettings() {
        Snackbar.make(mDrawerLayout, "SNACK", Snackbar.LENGTH_SHORT).show();
        // TODO: 18.03.2016
    }

    @Override
    public void logOut() {
        VkApiUtils.logOut(this);
    }
}
