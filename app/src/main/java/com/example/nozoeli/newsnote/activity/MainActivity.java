package com.example.nozoeli.newsnote.activity;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.nozoeli.newsnote.R;
import com.example.nozoeli.newsnote.bean.NewsListBean;
import com.example.nozoeli.newsnote.fragment.NewsFragment;
import com.example.nozoeli.newsnote.fragment.NoteFragment;
import com.example.nozoeli.newsnote.util.UtilTools;

import org.litepal.tablemanager.Connector;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager manager = getSupportFragmentManager();
    public static NewsListBean data[] = new NewsListBean[5];
    private NewsFragment newsInstance;
    private NoteFragment noteInstance;
    private Toolbar toolbar;
    private LinearLayout status;
    private boolean isConnected = false;

    public static ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDataSet();
        initUI();
        SQLiteDatabase db = Connector.getDatabase();
        newsInstance = NewsFragment.newInstance();
        noteInstance = NoteFragment.newInstance();

        manager.beginTransaction()
                .add(R.id.main_container, newsInstance)
                .show(newsInstance)
                .commit();
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.main);

        status = (LinearLayout) findViewById(R.id.main_status);
        status.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                UtilTools.getBarHeight(getResources())));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private void initDataSet() {

        for (int i = 0; i < 5; i++) {
            data[i] = new NewsListBean();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera && noteInstance.isAdded()) {
            manager.beginTransaction()
                    .remove(noteInstance)
                    .show(newsInstance)
                    .commit();

        } else if (id == R.id.nav_gallery && !noteInstance.isAdded()) {
            getSupportActionBar().setTitle("");
                manager.beginTransaction()
                        .hide(newsInstance)
                        .add(R.id.main_container, noteInstance)
                        .commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
