package com.qwert2603.vkautomessage.navigation;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.login.MainActivity;

public abstract class NavigationActivity extends AppCompatActivity implements NavigationView {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private boolean mIsNavigationButtonVisible;

    private ImageView mUserPhotoImageView;
    private TextView mUserNameTextView;

    private NavigationPresenter mNavigationPresenter;

    protected abstract boolean isNavigationButtonVisible();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        android.support.design.widget.NavigationView navigationView = (android.support.design.widget.NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            mDrawerLayout.closeDrawers();
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

        mIsNavigationButtonVisible = isNavigationButtonVisible();

        if (mIsNavigationButtonVisible) {
            mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
            mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
            toolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat.START));
        }

        // TODO: 26.03.2016 показывать в них фото и имя пользователя
        mUserPhotoImageView = (ImageView) findViewById(R.id.user_photo_image_view);
        mUserNameTextView = (TextView) findViewById(R.id.user_name_text_view);

        mNavigationPresenter = new NavigationPresenter();
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
    public void showSettings() {
        Snackbar.make(mDrawerLayout, "SNACK", Snackbar.LENGTH_SHORT).show();
        // TODO: 18.03.2016 запускать Активити с настройками
    }

    @Override
    public void showLogOut() {
        // TODO: 28.03.2016 очищать стек активити
        Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showUserName(String userName) {
        //mUserNameTextView.setText(userName);
    }

    @Override
    public void showUserPhoto(Bitmap photo) {
        //mUserPhotoImageView.setImageBitmap(photo);
    }
}
