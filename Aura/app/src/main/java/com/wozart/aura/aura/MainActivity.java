package com.wozart.aura.aura;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wozart.aura.amazonaws.models.nosql.DevicesTableDO;
import com.wozart.aura.R;
import com.wozart.aura.aura.activities.customization.CustomizationActivity;
import com.wozart.aura.aura.activities.sharing.SharingActivity;
import com.wozart.aura.aura.model.AuraSwitch.AuraSwitch;
import com.wozart.aura.aura.network.AwsPubSub;
import com.wozart.aura.aura.network.NsdClient;
import com.wozart.aura.aura.network.TcpClient;
import com.wozart.aura.aura.noSql.SqlOperationUserTable;
import com.wozart.aura.aura.sqlLite.device.DeviceDbHelper;
import com.wozart.aura.aura.sqlLite.device.DeviceDbOperation;
import com.wozart.aura.aura.utilities.Constant;
import com.wozart.aura.aura.utilities.DeviceUtils;
import com.wozart.aura.aura.utilities.Encryption;
import com.wozart.aura.aura.utilities.JsonUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static com.wozart.aura.aura.utilities.Constant.MAX_HOME;
import static com.wozart.aura.aura.utilities.Constant.NETWORK_SSID;

/***************************************************************************
 * File Name : MainActivity
 * Author : Aarth Tandel
 * Date of Creation : 29/12/17
 * Description : Dashboard and the main screen of the application. Contains
 *               3 parts, Home, Scenes and favourites
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private NavigationView NavigationView;
    private TcpClient mTcpClient;
    private NsdClient Nsd;
    private DeviceUtils mDeviceUtils;

    private Menu HomeMenu;
    private int HOME_ID = 1;
    public static String SELECTED_HOME;
    private static String ADD_NEW_DEVICE_TO = null;
    private AwsPubSub awsPubSub;
    boolean mBounded;
    private static String UIUD;

    private DeviceDbOperation db = new DeviceDbOperation();
    private SQLiteDatabase mDb;

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton AddDevice, ConfigureDevice, AddRooms;
    private Toast mtoast;

    private SqlOperationUserTable sqlOperationUserTable = new SqlOperationUserTable();

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

        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(
                mMessageReceiver, new IntentFilter("AwsShadow"));

        DeviceDbHelper dbHelper = new DeviceDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        db.InsertBasicData(mDb);

        initializeTabs();
        initializeFab();
        initializeDiscovery();
        updateUSerInfo();

    }

    private void initializeDiscovery() {
        DeviceDbHelper dbHelper = new DeviceDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        mDeviceUtils = new DeviceUtils();

        Nsd = new NsdClient(this);
        Nsd.initializeNsd();
        nsdDiscovery();
    }

    public void nsdDiscovery() {
        Nsd.discoverServices();
        final Handler NsdDiscoveryHandler = new Handler();
        NsdDiscoveryHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (final NsdServiceInfo service : Nsd.GetServiceInfo()) {
                    try {
                        JsonUtils mJsonUtils = new JsonUtils();
                        String device = service.getServiceName().substring(service.getServiceName().length() - 6, service.getServiceName().length());
                        String uiud = DeviceDbOperation.getUiud(mDb, device);
                        if (uiud == null) uiud = Constant.UNPAIRED;
                        new ConnectTask(mJsonUtils.InitialData(uiud), service.getHost().getHostAddress()).execute("");
                        Log.i(LOG_TAG, "Initial data: " + mJsonUtils.InitialData(uiud) + " to " + service.getServiceName() + " IP: " + service.getHost().getHostAddress());
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, 1000);

        NsdDiscoveryHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Nsd.stopDiscovery();
            }
        }, 1000);
    }

    private void initializeFab() {
        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        AddDevice = (FloatingActionButton) findViewById(R.id.add_device);
        ConfigureDevice = (FloatingActionButton) findViewById(R.id.configure_device);
        AddRooms = (FloatingActionButton) findViewById(R.id.add_room);

        AddRooms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addRooms();
            }
        });
        AddDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
        ConfigureDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openWebPage(Constant.URL);
            }
        });
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


    /**
     * Updates User name in UI, gets all the devices in cloud and populates in application
     */

    private void updateUSerInfo() {
        View headerView = NavigationView.getHeaderView(0);
        final ImageView userProfilePictureImageView = (ImageView) headerView.findViewById(R.id.imageView_user_image);
        final TextView userNameTextView = (TextView) headerView.findViewById(R.id.tv_username);
        final TextView userEmailTextView = (TextView) headerView.findViewById(R.id.tv_user_email);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        final String userName = prefs.getString("USERNAME", "defaultStringIfNothingFound");
        final String email = prefs.getString("EMAIL", "defaultStringIfNothingFound");
        String userProfilePicture = prefs.getString("PROFILE_PICTURE", "defaultStringIfNothingFound");
        final String userId = prefs.getString("ID", "NULL");
        if (!userId.equals("NULL"))
            Constant.IDENTITY_ID = userId;

        if (!userName.equals("defaultStringIfNothingFound"))
            Constant.USERNAME = userName;

        if (!userProfilePicture.equalsIgnoreCase("")) {
            Glide.with(this).load(userProfilePicture).into(userProfilePictureImageView);
        }

        userNameTextView.setText(userName);
        userEmailTextView.setText(email);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isInternetWorking()) {
                    //sqlOperationUserTable.sharedUser("aarth.996@gmail.com");
                    if (!userId.equals("NULL")) {
                        if (!sqlOperationUserTable.isUserAlreadyRegistered(userId)) {
                            sqlOperationUserTable.insertUser(userId, userName, email);
                        } else {
                            ArrayList<DevicesTableDO> devices = sqlOperationUserTable.getUserDevices(userId);
                            db.devicesFromAws(mDb, devices);
                        }
                    }
                }
            }
        }).start();
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
                    Snackbar.make(materialDesignFAM, "Only five Home can be added :(", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                return true;

            case R.id.home:
                item.setChecked(true);
                SELECTED_HOME = item.getTitle().toString();
                refreshHomeTab(SELECTED_HOME);
                refreshFavTab(SELECTED_HOME);
                return true;

            case 1:
                item.setChecked(true);
                SELECTED_HOME = item.getTitle().toString();
                refreshHomeTab(SELECTED_HOME);
                refreshFavTab(SELECTED_HOME);
                return true;

            case 2:
                item.setChecked(true);
                SELECTED_HOME = item.getTitle().toString();
                refreshHomeTab(SELECTED_HOME);
                refreshFavTab(SELECTED_HOME);
                return true;

            case 3:
                item.setChecked(true);
                SELECTED_HOME = item.getTitle().toString();
                refreshHomeTab(SELECTED_HOME);
                refreshFavTab(SELECTED_HOME);
                return true;

            case 4:
                item.setChecked(true);
                SELECTED_HOME = item.getTitle().toString();
                refreshHomeTab(SELECTED_HOME);
                refreshFavTab(SELECTED_HOME);
                return true;
        }

        return super.onOptionsItemSelected(item);
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
            Intent intent = new Intent(this, CustomizationActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(this, SharingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Dialogue modal pop-up to register various values
     */

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

    private void addDeviceToWhatRoomPopUp(final AuraSwitch deviceToPair) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Select room to add the device");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_device_to_home_dialog, null);
        dialog.setView(dialogView);
        ArrayList<String> rooms = db.GetRooms(mDb, SELECTED_HOME);
        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.radio_group);

        for (String x : rooms) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(x);
            radioGroup.addView(radioButton);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                for (int i = 0; i < rg.getChildCount(); i++) {
                    RadioButton btn = (RadioButton) rg.getChildAt(i);
                    if (btn.getId() == checkedId) {
                        ADD_NEW_DEVICE_TO = btn.getText().toString();
                        // do something with text
                        return;
                    }
                }
            }
        });

        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!(ADD_NEW_DEVICE_TO == null)) {
                    pairingPopUp(deviceToPair);
                }
            }
        });
        dialog.show();
    }

    private void pairingPopUp(final AuraSwitch deviceToPair) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alert.setView(input);
        alert.setMessage("Enter pairing pin of  " + deviceToPair.getName());
        alert.setTitle("Pin");
        alert.setPositiveButton("Pair", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                if (input.length() == 8) {
                    try {
                        String encryptedPin = Encryption.SHA256(input.getText().toString());
                        UIUD = Encryption.generateUIUD(deviceToPair);
                        String data = JsonUtils.PairingData(UIUD, encryptedPin);
                        new ConnectTask(data, deviceToPair.getIP()).execute("");
                    } catch (NoSuchAlgorithmException e) {
                        Log.e(LOG_TAG, "Failed Pairing: " + e);
                    }
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

    private void addRooms() {
        final Boolean[] flag = {true};
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alert.setView(input);
        alert.setMessage("Adding new room to " + SELECTED_HOME);
        alert.setTitle("Loads");
        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                for (String x : db.GetRooms(mDb, SELECTED_HOME)) {
                    if (input.getText().toString().equals(x)) {
                        flag[0] = false;
                    }
                }
                if (flag[0] && !input.getText().toString().toLowerCase().equals("hall")) {
                    db.InsertRoom(mDb, SELECTED_HOME, input.getText().toString().trim());
                    refreshHomeTab(SELECTED_HOME);
                    Snackbar.make(materialDesignFAM, "Room added", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    Snackbar.make(materialDesignFAM, "Room with same name already exists", Snackbar.LENGTH_LONG)
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

    /**
     * AWS IoT Subscribe to shadow broadcast receiver
     */

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, AwsPubSub.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            awsPubSub = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            AwsPubSub.LocalAwsBinder mLocalBinder = (AwsPubSub.LocalAwsBinder) service;
            awsPubSub = mLocalBinder.getServerInstance();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String shadow = intent.getStringExtra("data");
            String segments[] = shadow.split("/");
            if (shadow.equals("Connected")) {
                final ArrayList<String> things = db.GetThingName(mDb);
                if (awsPubSub == null) {
                    try {
                        Thread.sleep(1000);
                        for (String x : things) {
                            awsPubSub.AwsGet(x);
                            awsPubSub.AwsGetPublish(x);
                            awsPubSub.AwsSubscribe(x);
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                String device = db.GetDeviceForThing(mDb, segments[1]);
                mDeviceUtils.CloudDevices(JsonUtils.DeserializeAwsData(segments[0]), segments[1], device);
            }
        }
    };

    /**
     * Send data to the device TCP
     */

    private class ConnectTask extends AsyncTask<String, String, TcpClient> {

        private String data = null;
        private String ip = null;

        private ConnectTask(String message, String address) {
            super();
            data = message;
            ip = address;
        }

        @Override
        protected TcpClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    publishProgress(message);
                }
            });
            mTcpClient.run(data, ip);

            return null;
        }

        protected void onProgressUpdate(String... message) {
            if (message[0].equals(Constant.SERVER_NOT_REACHABLE)) {
                if (mtoast != null)
                    mtoast = null;
                Context context = getApplicationContext();
                CharSequence text = "Device Offline";
                int duration = Toast.LENGTH_SHORT;

                mtoast = Toast.makeText(context, text, duration);
                mtoast.show();
            } else {
                String decryptedData = Encryption.denryptMessage(message[0]);
                Log.i(LOG_TAG, "Data Received : " + decryptedData);
                JsonUtils mJsonUtils = new JsonUtils();
                AuraSwitch dummyDevice = mJsonUtils.DeserializeTcp(decryptedData);

                switch (dummyDevice.getType()) {
                    case 1:
                        if (dummyDevice.getDiscovery() == 0) {
                            dummyDevice.setIP(Nsd.GetIP(dummyDevice.getName()));
                            Snackbar.make(findViewById(R.id.mCordinateLayout), "New device " + dummyDevice.getName(), Snackbar.LENGTH_INDEFINITE)
                                    .setAction("ADD", new ConfigureListener(dummyDevice)).show();
                            return;
                        }

//                        if (dummyDevice.getError() == 1) {
//                            Snackbar.make(findViewById(R.id.mCordinateLayout), "Unauthorized access to " + dummyDevice.getName(), Snackbar.LENGTH_INDEFINITE).setAction("DISMISS", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                }
//                            }).show();
//                            return;
//                        }

                        if (dummyDevice.getError() == 0) {
                            for (NsdServiceInfo x : Nsd.GetAllServices()) {
                                //Find the match in services found and data received
                                if (x.getServiceName().contains(dummyDevice.getName())) {
                                    mDeviceUtils.RegisterDevice(dummyDevice, x.getHost().getHostAddress(), DeviceDbOperation.getUiud(mDb, dummyDevice.getName()));
                                }
                            }
                            return;
                        }
                        return;

                    case 2:
                        if (dummyDevice.getError() == 0) {
                            if (mtoast != null)
                                mtoast = null;
                            Context context = getApplicationContext();
                            CharSequence text = "Device Paired Successfully";
                            int duration = Toast.LENGTH_SHORT;
                            mtoast = Toast.makeText(context, text, duration);
                            mtoast.show();

                            db.AddDevice(mDb, ADD_NEW_DEVICE_TO, SELECTED_HOME, dummyDevice, UIUD);
                            for (NsdServiceInfo x : Nsd.GetAllServices()) {
                                //Find the match in services found and data received
                                if (x.getServiceName().contains(dummyDevice.getName())) {
                                    mDeviceUtils.RegisterDevice(dummyDevice, x.getHost().getHostAddress(), UIUD);
                                }
                            }
                            return;
                        }

                        if (dummyDevice.getError() == 1) {
                            if (mtoast != null)
                                mtoast = null;
                            Context context = getApplicationContext();
                            CharSequence text = "Wrong Device Pin";
                            int duration = Toast.LENGTH_SHORT;
                            mtoast = Toast.makeText(context, text, duration);
                            mtoast.show();
                            return;
                        }
                        return;
                }
            }
        }

    }

    /**
     * Class for adding a new device during configuration
     */

    private class ConfigureListener implements View.OnClickListener {
        private AuraSwitch deviceToPair;

        private ConfigureListener(AuraSwitch device) {
            deviceToPair = device;
        }

        @Override
        public void onClick(View v) {
            addDeviceToWhatRoomPopUp(deviceToPair);
        }
    }

    /**
     * Opening configuration web page for Aura Switch
     */

    private void openWebPage(String url) {

        WifiConfiguration mWifiConfig = new WifiConfiguration();
        mWifiConfig.SSID = "\"" + NETWORK_SSID + "\"";
        mWifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        WifiManager mWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifi.addNetwork(mWifiConfig);

        WifiInfo mWifiInfo = mWifi.getConnectionInfo();
        mWifiInfo.getSSID();
        if (mWifiInfo.getSSID().contains(NETWORK_SSID)) {
            Uri webpage = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            Toast.makeText(this, "Please Connect to Aura Device", Toast.LENGTH_LONG).show();


        }
    }

    /**
     * Updating favourite fragment from main
     */

    public void refreshHomeTab(String home) {
        Intent intent = new Intent("refreshHomeTab");
        intent.putExtra("home", home);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void refreshFavTab(String home) {
        Intent intent = new Intent("refreshFavTab");
        intent.putExtra("home", home);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public String GetSelectedHome() {
        return SELECTED_HOME;
    }

    /**
     * Method to check internet connection
     */


    public boolean isInternetWorking() {
        boolean success = false;
        try {
            URL url = new URL("https://google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            success = connection.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }
}
