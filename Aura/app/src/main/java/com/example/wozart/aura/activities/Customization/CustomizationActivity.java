package com.example.wozart.aura.activities.customization;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.wozart.aura.R;
import com.example.wozart.aura.noSql.SqlOperationDeviceTable;
import com.example.wozart.aura.noSql.SqlOperationUserTable;
import com.example.wozart.aura.sqlLite.device.DeviceDbHelper;
import com.example.wozart.aura.sqlLite.device.DeviceDbOperation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/***************************************************************************
 * File Name : CustomizationActivity
 * Author : Aarth Tandel
 * Date of Creation : 29/12/17
 * Description : Room, home and AWS customization for device
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class CustomizationActivity extends AppCompatActivity {

    private CustomizationDeviceAdapter adapter;
    private List<CustomizationDevices> DeviceList;

    private DeviceDbOperation db = new DeviceDbOperation();
    private SQLiteDatabase mDb;
    private SqlOperationDeviceTable sqlOperationDeviceTable = new SqlOperationDeviceTable();
    private SqlOperationUserTable sqlOperationUserTable = new SqlOperationUserTable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customization);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        DeviceList = new ArrayList<>();
        adapter = new CustomizationDeviceAdapter(this, DeviceList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new CustomizationActivity.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        List<CustomizationDevices> devices;
        DeviceDbHelper dbHelper = new DeviceDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        devices = db.GetAllDevices(mDb);
        prepareLoad(devices);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deviceSharingUIUD();
            }
        });
    }

    /**
     * Getting device access
     */
    public void deviceSharingUIUD() {
        AlertDialog.Builder alert = new AlertDialog.Builder(CustomizationActivity.this);
        final EditText input = new EditText(CustomizationActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alert.setView(input);
        alert.setMessage("Enter the shared id:");
        alert.setTitle("Shared Device");
        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                if (input.getText().toString().trim().length() == 17) {
                    updateSharedDevices(input.getText().toString().trim());
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

    public boolean updateSharedDevices(final String uiud) {
        final String deviceId = uiud.substring(0, Math.min(uiud.length(), 12));
        final String deviceName = deviceId.substring(deviceId.length() - 6);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isInternetWorking()) {
                    String thing = sqlOperationDeviceTable.insertSlave(deviceId);
                    sqlOperationUserTable.updateUserDevices(deviceName);
                    db.AddDevice(mDb, "Hall", "Home", deviceName, uiud,thing);
                } else {
                    //TODO toast
                }
            }
        }).start();

        return false;
    }


    /**
     * Adding few albums for testing
     */
    private void prepareLoad(List<CustomizationDevices> devices) {

        DeviceList.clear();
        int[] covers = new int[]{
                R.drawable.album1,
                R.drawable.album2,
                R.drawable.album3,
                R.drawable.album4,
                R.drawable.album5,
                R.drawable.album6,
                R.drawable.album7
        };

        for (CustomizationDevices device : devices) {
            if (device.getDevice() != null) {
                if (device.getThing() != null)
                    device.setAws(1);
                CustomizationDevices a = new CustomizationDevices(device.getHome(), device.getRoom(), device.getDevice(), device.getThing(), device.getAws(), device.getOnline());
                DeviceList.add(a);
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

    /**
     * Check internet connection
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
