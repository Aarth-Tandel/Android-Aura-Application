package com.example.wozart.aura.activities.customization;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wozart.amazonaws.models.nosql.ThingTableDO;
import com.example.wozart.aura.MainActivity;
import com.example.wozart.aura.R;
import com.example.wozart.aura.model.AuraSwitch;
import com.example.wozart.aura.network.NsdClient;
import com.example.wozart.aura.network.TcpClient;
import com.example.wozart.aura.noSql.SqlOperationDeviceTable;
import com.example.wozart.aura.noSql.SqlOperationThingTable;
import com.example.wozart.aura.noSql.SqlOperationUserTable;
import com.example.wozart.aura.sqlLite.device.DeviceDbHelper;
import com.example.wozart.aura.sqlLite.device.DeviceDbOperation;
import com.example.wozart.aura.utilities.Constant;
import com.example.wozart.aura.utilities.DeviceUtils;
import com.example.wozart.aura.utilities.Encryption;
import com.example.wozart.aura.utilities.JsonUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by wozart on 29/12/17.
 */

public class CustomizationDeviceAdapter extends RecyclerView.Adapter<CustomizationDeviceAdapter.MyViewHolder> {
    private static final String LOG_TAG = CustomizationActivity.class.getSimpleName();

    private Context mContext;
    private List<CustomizationDevices> DeviceList;
    private DeviceUtils mDeviceUtils = new DeviceUtils();

    private DeviceDbOperation db = new DeviceDbOperation();
    private SQLiteDatabase mDb;
    private TcpClient mTcpClient;
    private NsdClient nsdClient;

    private Toast mtoast;
    private ThingTableDO KeysAndCertificates = new ThingTableDO();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDevice, textViewHome, textViewRoom;
        public ImageView thumbnail1, overflow;
        public Switch AwsConnectSwitch;

        public MyViewHolder(View view) {
            super(view);

            textViewDevice = (TextView) view.findViewById(R.id.tv_device);
            textViewHome = (TextView) view.findViewById(R.id.tv_home);
            textViewRoom = (TextView) view.findViewById(R.id.tv_room);
            thumbnail1 = (ImageView) view.findViewById(R.id.thumbnail1);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            AwsConnectSwitch = (Switch) view.findViewById(R.id.switch_aws_connect);
        }
    }


    public CustomizationDeviceAdapter(Context mContext, List<CustomizationDevices> roomsList) {
        this.mContext = mContext;
        this.DeviceList = roomsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customization_cards, parent, false);

        DeviceDbHelper dbHelper = new DeviceDbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        db.InsertBasicData(mDb);
        nsdClient = new NsdClient(mContext);
        nsdClient.initializeNsd();
        nsdDiscovery();

        return new MyViewHolder(itemView);
    }

    public void nsdDiscovery() {
        nsdClient.discoverServices();
        final Handler NsdDiscoveryHandler = new Handler();
        NsdDiscoveryHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (final NsdServiceInfo service : nsdClient.GetServiceInfo()) {
                    JsonUtils mJsonUtils = new JsonUtils();
                    String data = null;
                    try {
                        data = mJsonUtils.InitialData(convertIP());
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                    new ConnectTask(data, service.getHost().getHostAddress()).execute("");
                    Log.d(LOG_TAG, "Initial data: " + data + " to " + service.getServiceName());
                }
            }
        }, 1000);

        NsdDiscoveryHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nsdClient.stopDiscovery();
            }
        }, 1000);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CustomizationDevices device = DeviceList.get(position);
        device.setPosition(position);
        holder.textViewDevice.setText(device.getDevice());
        holder.textViewHome.setText(device.getHome());
        holder.textViewRoom.setText(device.getRoom());
        // loading rooms cover using Glide library
//        Glide.with(mContext).load(rooms.getThumbnail()).into(holder.thumbnail1);
//        Glide.with(mContext).load(rooms.getThumbnail()).into(holder.thumbnail2);
//        Glide.with(mContext).load(rooms.getThumbnail()).into(holder.thumbnail3);
//        Glide.with(mContext).load(rooms.getThumbnail()).into(holder.thumbnail4);

        if (device.getAws() == 1)
            holder.AwsConnectSwitch.setChecked(true);
        else
            holder.AwsConnectSwitch.setChecked(false);

        holder.AwsConnectSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.AwsConnectSwitch.isChecked())
                    getKeys(device.getDevice());
                else
                    Toast.makeText(mContext, device.getDevice() + " Selected", Toast.LENGTH_SHORT).show();
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, holder.textViewDevice.getText().toString(), position);
            }
        });
    }

    /**
     * Transferring Keys to Device
     */
    private void getKeys(final String device) {
        new ConnectTask("{\"type\":8, \"set\":1}", mDeviceUtils.GetIP(device)).execute("");
        final SqlOperationThingTable keysAvailable = new SqlOperationThingTable();
        Runnable runnable = new Runnable() {
            public void run() {
                KeysAndCertificates = keysAvailable.searchAvailableDevices();
                KeysAndCertificates.getThing();
                sendCertificate(device);
                sendKeys(device);
            }
        };
        Thread getAvailableDevices = new Thread(runnable);
        getAvailableDevices.start();
    }

    private void sendCertificate(final String device) {
        final JsonUtils jsonUtils = new JsonUtils();
        final String[] nameRegion = {null};
        Runnable runnable = new Runnable() {
            public void run() {
                nameRegion[0] = jsonUtils.AwsRegionThing(KeysAndCertificates.getRegion(), KeysAndCertificates.getThing());
                new ConnectTask(nameRegion[0], mDeviceUtils.GetIP(device)).execute("");
                ArrayList<String> data = jsonUtils.Certificates(KeysAndCertificates.getCertificate());
                sendTcpKeys(data, "Certificate", device);
                Log.d(LOG_TAG, "Send Certificate Keys" + data);

            }
        };
        Thread sendCertificates = new Thread(runnable);
        sendCertificates.start();
    }

    private void sendKeys(final String device) {
        final JsonUtils jsonUtils = new JsonUtils();
        final String[] nameRegion = {null};
        Runnable runnable = new Runnable() {
            public void run() {
                nameRegion[0] = jsonUtils.AwsRegionThing(KeysAndCertificates.getRegion(), KeysAndCertificates.getThing());
                ArrayList<String> data = jsonUtils.PrivateKeys(KeysAndCertificates.getPrivateKey());
                sendTcpKeys(data, "PrivateKey", device);
                Log.d(LOG_TAG, "Send Private Keys" + data);

            }
        };
        Thread sendCertificates = new Thread(runnable);
        sendCertificates.start();
    }

    private void sendTcpKeys(ArrayList<String> data, String whatData, String device) {
        for (String key : data) {
            try {
                new ConnectTask(key, mDeviceUtils.GetIP(device)).execute("");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (whatData.equals("PrivateKey")) {
            new ConnectTask("{\"type\":8, \"set\":0}", mDeviceUtils.GetIP(device)).execute("");
        }
    }

    /**
     * Sending and receiving messages to TCP client
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

            JsonUtils mJsonUtils = new JsonUtils();
            final AuraSwitch dummyDevice = mJsonUtils.DeserializeTcp(message[0]);

            if (dummyDevice.getType() == 1 && dummyDevice.getCode().equals(Encryption.MAC(mContext))) {
                for (NsdServiceInfo x : nsdClient.GetAllServices()) {
                    //Find the match in services found and data received
                    if (x.getServiceName().contains(dummyDevice.getName())) {
                        mDeviceUtils.RegisterDevice(dummyDevice, x.getHost().getHostAddress());
                        updateAwsState(dummyDevice);
                    }
                }
            }

            if (dummyDevice.getType() == 8 && dummyDevice.getError() == 0) {
                final SqlOperationDeviceTable sqlOperationDeviceTable = new SqlOperationDeviceTable();
                Runnable runnable = new Runnable() {
                    public void run() {
                        sqlOperationDeviceTable.newUserDevice(Constant.IDENTITY_ID, dummyDevice.getName(), db.GetDeviceRoom(mDb, dummyDevice.getName()));
                    }
                };
                Thread getAvailableDevices = new Thread(runnable);
                getAvailableDevices.start();
                Toast.makeText(mContext, "Synced with AWS", Toast.LENGTH_SHORT).show();
                //updateAwsState(dummyDevice.getName());
            } else if (dummyDevice.getType() == 8 && dummyDevice.getError() == 1) {
                Toast.makeText(mContext, "Cannot receive all Packages, please try again ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateAwsState(AuraSwitch device) {
        for (CustomizationDevices x : DeviceList) {
            if (device.getName().equals(x.getDevice())) {
                x.setAws(device.getAWSConfiguration());
                notifyItemChanged(x.getPosition(), x);
            }
        }
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, String device, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_rooms, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(device, position));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private String DeviceSelected;
        private int Position;

        private MyMenuItemClickListener(String device, int position) {
            DeviceSelected = device;
            Position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    editBoxPopUp(DeviceSelected);
                    return true;

                case R.id.action_delete:
                default:
            }
            return false;
        }

        private void editBoxPopUp(final String deviceSelected) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle("Customization Device");
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.customization_edit_dialog, null);
            dialog.setView(dialogView);

            final Spinner homeSpinner = (Spinner) dialogView.findViewById(R.id.spinner_home);
            ArrayList<String> homes = db.GetAllHome(mDb);
            ArrayAdapter<String> adapterHome = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, homes);
            homeSpinner.setAdapter(adapterHome);

            final Spinner roomSpinner = (Spinner) dialogView.findViewById(R.id.spinner_room);
            homeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ArrayList<String> rooms = db.GetRooms(mDb, homeSpinner.getSelectedItem().toString());
                    ArrayAdapter<String> adapterRoom = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, rooms);
                    roomSpinner.setAdapter(adapterRoom);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (homeSpinner.getSelectedItem().toString() != null && roomSpinner.getSelectedItem().toString() != null) {
                        db.UpdateRoomAndHome(mDb, homeSpinner.getSelectedItem().toString(), roomSpinner.getSelectedItem().toString(), deviceSelected);
                        for (CustomizationDevices x : DeviceList) {
                            if (deviceSelected.equals(x.getDevice())) {
                                x.setHome(homeSpinner.getSelectedItem().toString());
                                x.setRoom(roomSpinner.getSelectedItem().toString());
                                notifyItemChanged(Position, x);
                            }
                        }
                    } else {
                        Toast.makeText(mContext, "Please make appropriate selection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
        }
    }

    private String convertIP() {
        WifiManager mWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifi.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    @Override
    public int getItemCount() {
        return DeviceList.size();
    }
}
