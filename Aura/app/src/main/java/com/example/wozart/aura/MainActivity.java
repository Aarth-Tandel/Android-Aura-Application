package com.example.wozart.aura;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.example.wozart.aura.network.AwsPubSub;
import com.example.wozart.aura.noSql.SqlOperationUserTable;
import com.example.wozart.aura.sqlLite.device.DeviceDbHelper;
import com.example.wozart.aura.sqlLite.device.DeviceDbOperation;

import static com.example.wozart.aura.utilities.Constant.MAX_HOME;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView NavigationView;
    private ImageView userProfilePicture;
    public static PinpointManager pinpointManager;

    private Menu HomeMenu;
    private int HOME_ID = 1;
    public static String SelectedHome;

    private DeviceDbOperation db = new DeviceDbOperation();
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        NavigationView = (NavigationView) findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateUSerInfo();
        awsAnalytics();

        DeviceDbHelper dbHelper = new DeviceDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        db.InsertBasicData(mDb);

        initializeTabs();
    }

    private void initializeTabs() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Favourites"));
        tabLayout.addTab(tabLayout.newTab().setText("Rooms"));
        tabLayout.addTab(tabLayout.newTab().setText("Scenes"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(1);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void awsAnalytics() {
        PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                getApplicationContext(),
                AWSMobileClient.getInstance().getCredentialsProvider(),
                AWSMobileClient.getInstance().getConfiguration());

        pinpointManager = new PinpointManager(pinpointConfig);

        // Start a session with Pinpoint
        pinpointManager.getSessionClient().startSession();

        // Stop the session and submit the default app started event
        pinpointManager.getSessionClient().stopSession();
        pinpointManager.getAnalyticsClient().submitEvents();
    }

    private void updateUSerInfo(){
        View headerView = NavigationView.getHeaderView(0);
        final ImageView userProfilePictureImageView = (ImageView) headerView.findViewById(R.id.imageView_user_image);
        final TextView userNameTextView = (TextView) headerView.findViewById(R.id.tv_username);
        final TextView userEmailTextView = (TextView) headerView.findViewById(R.id.tv_user_email);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String userName = prefs.getString("USERNAME", "defaultStringIfNothingFound");
        String email = prefs.getString("EMAIL", "defaultStringIfNothingFound");
        String userProfilePicture = prefs.getString("PROFILE_PICTURE", "defaultStringIfNothingFound");

        if( !userProfilePicture.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(userProfilePicture, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            userProfilePictureImageView.setImageBitmap(bitmap);
        }

        userNameTextView.setText(userName);
        userEmailTextView.setText(email);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.HomeMenu = menu;
        for (String home : db.GetAllHome(mDb)) {
            if (home != null)
                createMenuItem(home);
        }
        onOptionsItemSelected(menu.findItem(R.id.home));
        return true;
    }

    private void createMenuItem(String name) {
        if (!name.equals("Home")) {
            HomeMenu.add(R.id.gp_home, HOME_ID, Menu.NONE, name);
            HomeMenu.setGroupCheckable(R.id.gp_home, true, true);
            HOME_ID++;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int count = HomeMenu.size();

        switch (item.getItemId()) {
            case R.id.add_home:
                if (count <= MAX_HOME) {
                    addHomeDialog();
                } else {
//                    Snackbar.make(materialDesignFAM, "Only five Home can be added :(", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                }
                return true;

            case R.id.home:
                item.setChecked(true);
                SelectedHome = item.getTitle().toString();
//                refreshHomeTab(SelectedHome);
//                refreshFavTab(SelectedHome);
                return true;

            case 1:
                item.setChecked(true);
                SelectedHome = item.getTitle().toString();
//                refreshHomeTab(SelectedHome);
//                refreshFavTab(SelectedHome);
                return true;

            case 2:
                item.setChecked(true);
                SelectedHome = item.getTitle().toString();
//                refreshHomeTab(SelectedHome);
//                refreshFavTab(SelectedHome);
                return true;

            case 3:
                item.setChecked(true);
                SelectedHome = item.getTitle().toString();
//                refreshHomeTab(SelectedHome);
//                refreshFavTab(SelectedHome);
                return true;

            case 4:
                item.setChecked(true);
                SelectedHome = item.getTitle().toString();
//                refreshHomeTab(SelectedHome);
//                refreshFavTab(SelectedHome);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addHomeDialog() {
        final Boolean[] flag = {true};
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alert.setView(input);
        alert.setMessage("Add a new home");
        alert.setTitle("Homes");
        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                for (String x : db.GetAllHome(mDb)) {
                    if (input.getText().toString().equals(x)) {
                        flag[0] = false;
                    }
                }
                if (flag[0]) {
                    createMenuItem(input.getText().toString());
                    db.InsertHome(mDb, input.getText().toString());
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Home added", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Room with same name already exists", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });
        alert.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
