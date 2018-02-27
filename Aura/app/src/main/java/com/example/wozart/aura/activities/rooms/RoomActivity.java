package com.example.wozart.aura.activities.rooms;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.example.wozart.aura.R;
import com.example.wozart.aura.model.AuraSwitch;
import com.example.wozart.aura.network.AwsPubSub;
import com.example.wozart.aura.network.NsdClient;
import com.example.wozart.aura.network.TcpClient;
import com.example.wozart.aura.sqlLite.device.DeviceDbHelper;
import com.example.wozart.aura.sqlLite.device.DeviceDbOperation;
import com.example.wozart.aura.utilities.Constant;
import com.example.wozart.aura.utilities.DeviceUtils;
import com.example.wozart.aura.utilities.Encryption;
import com.example.wozart.aura.utilities.JsonUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/***************************************************************************
 * File Name : RoomActivity
 * Author : Aarth Tandel
 * Date of Creation : 29/12/17
 * Description : Handles device and state management of different aura switches
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class RoomActivity extends AppCompatActivity {

    private static final String LOG_TAG = RoomActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private LoadAdapter adapter;
    private List<Loads> LoadList;
    private Toast mtoast;
    String RoomSelected, HomeSelected;

    private DeviceDbOperation db = new DeviceDbOperation();
    private SQLiteDatabase mDb;

    private AwsPubSub awsPubSub;
    boolean mBounded;

    private NsdClient Nsd;
    private TcpClient mTcpClient;
    private String IP;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Intent intent = getIntent();
        RoomSelected = intent.getStringExtra("room");
        HomeSelected = intent.getStringExtra("home");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        LoadList = new ArrayList<>();
        adapter = new LoadAdapter(this, LoadList, RoomSelected);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new RoomActivity.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    NsdDiscovery();
                }catch (Exception e){
                    Log.e(LOG_TAG, "Error: " + e);
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        ArrayList<String> devices;
        DeviceDbHelper dbHelper = new DeviceDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        devices = db.GetDevicesInRoom(mDb, RoomSelected, HomeSelected);
        prepareLoad(devices);

        Nsd = new NsdClient(this);
        Nsd.initializeNsd();
        NsdDiscovery();
    }

    /**
     * NSD discovery of devices
     */

    private void NsdDiscovery() {
        Nsd.discoverServices();
        final Handler NsdDiscoveryHandler = new Handler();
        NsdDiscoveryHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (final NsdServiceInfo service : Nsd.GetServiceInfo()) {
                    try {
                        JsonUtils mJsonUtils = new JsonUtils();
                        String device = service.getServiceName().substring(service.getServiceName().length() - 6, service.getServiceName().length());
                        String uiud = DeviceDbOperation.getUiud(mDb,device);
                        if(uiud == null) uiud = Constant.UNPAIRED;
                        new ConnectTask(mJsonUtils.InitialData(uiud), service.getHost().getHostAddress()).execute("");
                        Log.d(LOG_TAG, "Initial data: " + mJsonUtils.InitialData(uiud) + " to " + service.getServiceName());
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
                JsonUtils mJsonUtils = new JsonUtils();
                AuraSwitch dummyDevice = mJsonUtils.DeserializeTcp(message[0]);
                DeviceUtils mDeviceUtils = new DeviceUtils();

                if (dummyDevice.getType() == 1 && dummyDevice.getUiud().equals(DeviceDbOperation.getUiud(mDb,dummyDevice.getName()))) {
                    for (NsdServiceInfo x : Nsd.GetAllServices()) {
                        //Find the match in services found and data received
                        if (x.getServiceName().contains(dummyDevice.getName())) {
                            mDeviceUtils.RegisterDevice(dummyDevice, x.getHost().getHostAddress(), dummyDevice.getUiud());
                        }
                    }
                }
            }
        }

    }

    /**
     * Calling the method of service
     */

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, AwsPubSub.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    ;

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

    public void PusblishDataToShadow(String thing, String data) {
        awsPubSub.AWSPublish(thing, data);
    }

    /**
     * Adding few albums for testing
     */
    private void prepareLoad(ArrayList<String> devices) {

        LoadList.clear();
        int[] covers = new int[]{
                R.drawable.album1,
                R.drawable.album2,
                R.drawable.album3,
                R.drawable.album4,
                R.drawable.album5,
                R.drawable.album6,
                R.drawable.album7
        };

        for (String deviceName : devices) {
            int i = 0;
            if (deviceName != null) {
                ArrayList<String> loads;
                loads = db.GetLoads(mDb, deviceName);

                for (String loadName : loads) {
                    Loads a = new Loads(loadName, deviceName, null, covers[2], i);
                    LoadList.add(a);
                    i++;
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        private GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
