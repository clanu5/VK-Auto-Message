package com.qwert2603.vkautomessage.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.entities.Record;
import com.qwert2603.vkautomessage.utils.RecordsUtils;

public class RecordsListActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CoordinatorLayout coordinatorLayout;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.log_out:
                        //todo
                        return true;
                }
                return false;
            }
        });
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }

        listView = (ListView) findViewById(R.id.records_list_view);
        listView.setAdapter(new RecordsAdapter(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(coordinatorLayout, "Qq", Snackbar.LENGTH_SHORT).show();
            }
        });


        FloatingActionButton newRecordFAB = (FloatingActionButton) findViewById(R.id.new_record_fab);
        newRecordFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private static class RecordsAdapter extends ArrayAdapter<Record> {
        private Activity activity;

        public RecordsAdapter(Activity activity) {
            super(activity, 0, RecordsUtils.getRecords());
            this.activity = activity;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = activity.getLayoutInflater().inflate(R.layout.list_item_record, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.userTextView = (TextView) convertView.findViewById(R.id.user_name_text_view);
                viewHolder.messageTextView = (TextView) convertView.findViewById(R.id.message_text_view);
                viewHolder.enabledSwitch = (CheckBox) convertView.findViewById(R.id.enable_check_box);
                convertView.setTag(viewHolder);
            }

            Record record = getItem(position);

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.userTextView.setText(record.getUserId() + "");//todo выводить имя
            viewHolder.messageTextView.setText(record.getMessage());  // todo обрезать длинное сообщение
            viewHolder.enabledSwitch.setChecked(record.isEnabled());

            return convertView;
        }

        private static class ViewHolder {
            public TextView userTextView;
            public CheckBox enabledSwitch;
            public TextView messageTextView;
        }
    }
}
