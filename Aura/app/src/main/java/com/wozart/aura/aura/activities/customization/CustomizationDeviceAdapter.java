package com.wozart.aura.aura.activities.customization;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.nsd.NsdServiceInfo;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.wozart.aura.amazonaws.models.nosql.ThingTableDO;
import com.wozart.aura.R;
import com.wozart.aura.aura.model.AuraSwitch.AuraSwitch;
import com.wozart.aura.aura.network.AwsPubSub;
import com.wozart.aura.aura.network.NsdClient;
import com.wozart.aura.aura.network.TcpClient;
import com.wozart.aura.aura.noSql.SqlOperationDeviceTable;
import com.wozart.aura.aura.noSql.SqlOperationThingTable;
import com.wozart.aura.aura.noSql.SqlOperationUserTable;
import com.wozart.aura.aura.sqlLite.device.DeviceDbHelper;
import com.wozart.aura.aura.sqlLite.device.DeviceDbOperation;
import com.wozart.aura.aura.utilities.Constant;
import com.wozart.aura.aura.utilities.DeviceUtils;
import com.wozart.aura.aura.utilities.Encryption;
import com.wozart.aura.aura.utilities.JsonUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import static com.facebook.FacebookSdk.getApplicationContext;

/***************************************************************************
 * File Name : CustomizationDeviceAdapter
 * Author : Aarth Tandel
 * Date of Creation : 29/12/17
 * Description : Loads each device into appropriate card view
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * 30/01/18  Aarth Tandel - Sharing device
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * 30/01/18 Version 1.1
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class CustomizationDeviceAdapter extends RecyclerView.Adapter<CustomizationDeviceAdapter.MyViewHolder> {
    private static final String LOG_TAG = CustomizationActivity.class.getSimpleName();

    private Context mContext;
    private List<CustomizationDevices> DeviceList;
    private DeviceUtils mDeviceUtils = new DeviceUtils();

    private DeviceDbOperation db = new DeviceDbOperation();
    private SQLiteDatabase mDb;
    private TcpClient mTcpClient;
    private NsdClient nsdClient;
    private AwsPubSub awsPubSub;

    private Toast mtoast;
    private SqlOperationDeviceTable sqlOperationDeviceTable = new SqlOperationDeviceTable();
    private SqlOperationUserTable sqlOperationUserTable = new SqlOperationUserTable();
    private SqlOperationThingTable sqlOperationThingTable = new SqlOperationThingTable();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDevice, textViewHome, textViewRoom;
        private ImageView thumbnail1, overflow;
        private Switch AwsConnectSwitch;

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

        DeviceDbHelper dbHelper = new DeviceDbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        nsdClient = new NsdClient(mContext);
        nsdClient.initializeNsd();
        nsdDiscovery();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customization_cards, parent, false);
        return new MyViewHolder(itemView);
    }

    public void nsdDiscovery() {
        nsdClient.discoverServices();
        final Handler NsdDiscoveryHandler = new Handler();
        NsdDiscoveryHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (final NsdServiceInfo service : nsdClient.GetServiceInfo()) {
                    try {
                        JsonUtils mJsonUtils = new JsonUtils();
                        String device = service.getServiceName().substring(service.getServiceName().length() - 6, service.getServiceName().length());
                        String uiud = DeviceDbOperation.getUiud(mDb, device);
                        if (uiud == null) uiud = Constant.UNPAIRED;
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
                nsdClient.stopDiscovery();
            }
        }, 1000);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CustomizationDevices device = DeviceList.get(position);
        device.setPosition(position);
        holder.textViewDevice.setText(device.getDevice());
        holder.textViewHome.setText(device.getHome());
        holder.textViewRoom.setText(device.getRoom());

        if (device.getAws() == 1 || device.getThing() != null)
            holder.AwsConnectSwitch.setChecked(true);
        else
            holder.AwsConnectSwitch.setChecked(false);

        holder.AwsConnectSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.AwsConnectSwitch.isChecked())
                    getKeys(device);
                else
                    Toast.makeText(mContext, device.getDevice() + " Selected", Toast.LENGTH_SHORT).show();
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, holder.textViewDevice.getText().toString(), holder.getAdapterPosition());
            }
        });
    }

    /**
     * Transferring Keys to Device
     */
    private void getKeys(final CustomizationDevices device) {
        new ConnectTask("{\"type\":8, \"set\":1}", mDeviceUtils.GetIP(device.getDevice())).execute("");
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String uiud = DeviceDbOperation.getUiud(mDb, device.getDevice());
                    String deviceId = uiud.substring(0, Math.min(uiud.length(), 12));
                    sqlOperationUserTable.updateUserDevices(deviceId);
                    String thingAlreadyExists = sqlOperationDeviceTable.newUserDevice(deviceId, uiud, device.getHome(), device.getRoom(), db.GetLoads(mDb, device.getDevice()));
                    ThingTableDO thing;
                    if (thingAlreadyExists != null) {
                        thing = sqlOperationThingTable.thingDetails(thingAlreadyExists);
                    } else {
                        thing = sqlOperationThingTable.searchAvailableDevices();
                    }
                    sendCertificate(device.getDevice(), thing);
                    sendKeys(device.getDevice(), thing);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread getAvailableDevices = new Thread(runnable);
        getAvailableDevices.start();
    }

    private void sendCertificate(final String device, final ThingTableDO KeysAndCertificates) {
        final JsonUtils jsonUtils = new JsonUtils();
        final String[] nameRegion = {null};
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    nameRegion[0] = jsonUtils.AwsRegionThing(KeysAndCertificates.getRegion(), KeysAndCertificates.getThing());
                    new ConnectTask(nameRegion[0], mDeviceUtils.GetIP(device)).execute("");
                    ArrayList<String> data = jsonUtils.Certificates(KeysAndCertificates.getCertificate());
                    sendTcpKeys(data, "Certificate", device);
                    Log.d(LOG_TAG, "Send Certificate Keys" + data);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error: " + e);
                }
            }
        };
        Thread sendCertificates = new Thread(runnable);
        sendCertificates.start();
    }

    private void sendKeys(final String device, final ThingTableDO KeysAndCertificates) {
        final JsonUtils jsonUtils = new JsonUtils();
        final String[] nameRegion = {null};
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    nameRegion[0] = jsonUtils.AwsRegionThing(KeysAndCertificates.getRegion(), KeysAndCertificates.getThing());
                    ArrayList<String> data = jsonUtils.PrivateKeys(KeysAndCertificates.getPrivateKey());
                    sendTcpKeys(data, "PrivateKey", device);
                    Log.d(LOG_TAG, "Send Private Keys" + data);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error: " + e);
                }

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

            if (message[0].equals(Constant.SERVER_NOT_REACHABLE)) {
                View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
                final Snackbar snackBar = Snackbar.make(rootView, "This process will take about 1 minute", Snackbar.LENGTH_INDEFINITE);
                snackBar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackBar.dismiss();
                    }
                });
                snackBar.show();
            } else {
                String decryptedData = Encryption.denryptMessage(message[0]);
                JsonUtils mJsonUtils = new JsonUtils();
                final AuraSwitch dummyDevice = mJsonUtils.DeserializeTcp(decryptedData);
                Log.i(LOG_TAG, "Received Data: " + decryptedData);
                switch (dummyDevice.getType()) {
                    case 1:
                        if (dummyDevice.getAWSConfiguration() == 1) {
                            for (NsdServiceInfo x : nsdClient.GetAllServices()) {
                                //Find the match in services found and data received
                                if (x.getServiceName().contains(dummyDevice.getName())) {
                                    mDeviceUtils.RegisterDevice(dummyDevice, x.getHost().getHostAddress(), dummyDevice.getUiud());
                                    updateAwsState(dummyDevice);

                                }
                            }
                        }
                        return;

                    case 8:
                        if (dummyDevice.getError() == 0) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String uiud = DeviceDbOperation.getUiud(mDb, dummyDevice.getName());
                                    sqlOperationDeviceTable.updateThingForDevice(uiud.substring(0, Math.min(uiud.length(), 12)), dummyDevice.getThing());
                                    sqlOperationThingTable.updateAvailability(dummyDevice.getThing());
                                }
                            }).start();
                            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            alert.setMessage("Aura Switch " + dummyDevice.getName() + " connected to cloud successfully");
                            alert.setTitle("Successfully Synced");
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            });
                            alert.show();
                            return;
                        }

                        if (dummyDevice.getError() == 1) {
                            Toast.makeText(mContext, "Cannot receive all Packages, please try again ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    case 5:
                    case 6:
                    case 7:
                        int packetCount = Integer.bitCount(dummyDevice.getThingFlag()) + Integer.bitCount(dummyDevice.getRegionFlag()) + Integer.bitCount(dummyDevice.getKeysFlag()) +
                                Integer.bitCount(dummyDevice.getCertificateFlag());

                        if (packetCount == 2 || packetCount == 24 || packetCount == 8 || packetCount == 16) {
                            if (mtoast != null)
                                mtoast = null;
                            Context context = getApplicationContext();
                            CharSequence text = "Completed Percentage: " + packetCount * 2 + "%";
                            int duration = Toast.LENGTH_SHORT;

                            mtoast = Toast.makeText(context, text, duration);
                            mtoast.show();
                        }
                        return;
                }
            }
        }
    }

    private void updateAwsState(AuraSwitch device) {
        for (CustomizationDevices x : DeviceList) {
            if (device.getName().equals(x.getDevice())) {
                if (device.getAWSConfiguration() == 1) x.setAws(1);
                else x.setAws(0);
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
        inflater.inflate(R.menu.menu_device, popup.getMenu());
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
                    Runnable runnable = new Runnable() {
                        public void run() {
                            if (isInternetWorking()) {
                                String uiud = DeviceDbOperation.getUiud(mDb, DeviceSelected);
                                String deviceId = uiud.substring(0, Math.min(uiud.length(), 12));
                                db.removeDevice(mDb, DeviceSelected);
                                sqlOperationUserTable.deleteUserDevice(deviceId);
                                sqlOperationDeviceTable.deleteDevice(deviceId);
                                deleteDevice(DeviceSelected);
                            } else {
                                mtoast.makeText(mContext, "Internet connection is required", Toast.LENGTH_LONG).show();
                            }
                        }
                    };
                    Thread deleteDevice = new Thread(runnable);
                    deleteDevice.start();
                    return true;

                case R.id.action_share_device:
                    getEmail(DeviceSelected);
                    return true;

                default:
            }
            return false;
        }

        /**
         * Editing home and room for devices
         */
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

    /**
     * Deleting device from android and cloud.
     * NOTE : This method requires internet
     */
    private void deleteDevice(String device) {
        CustomizationDevices deleteDevice = new CustomizationDevices();
        for (CustomizationDevices x : DeviceList) {
            if (x.getDevice().equals(device)) {
                deleteDevice = x;
            }
        }
        final CustomizationDevices finalDeleteDevice = deleteDevice;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DeviceList.remove(finalDeleteDevice.getPosition());
                notifyItemRemoved(finalDeleteDevice.getPosition());
                notifyItemChanged(finalDeleteDevice.getPosition());

            }
        });
    }

    /**
     * Sharing device
     */

    private void getEmail(final String device) {
        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(mContext);
        final EditText input = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alert.setView(input);
        alert.setMessage("Enter the Email id to share with:");
        alert.setTitle("Share");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                shareDevice(input.getText().toString().trim(), device);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });
        alert.show();
    }

    private void shareDevice(String emailId, String device) {
        String uuid = DeviceDbOperation.getUiud(mDb, device);
        String body = String.format(Constant.EMAIL_BODY, device);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailId});
        i.putExtra(Intent.EXTRA_SUBJECT, Constant.EMAIL_SUBJECT + device);
        i.putExtra(Intent.EXTRA_TEXT, body + uuid);
        try {
            mContext.startActivity(Intent.createChooser(i, "Share device to"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Utility function
     */

    @Override
    public int getItemCount() {
        return DeviceList.size();
    }

    private boolean isInternetWorking() {
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
