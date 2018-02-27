package com.example.wozart.aura.activities.sharing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.example.wozart.amazonaws.models.nosql.UserTableDO;
import com.example.wozart.aura.R;
import com.example.wozart.aura.noSql.SqlOperationUserTable;
import com.example.wozart.aura.sqlLite.device.DeviceDbHelper;
import com.example.wozart.aura.sqlLite.device.DeviceDbOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SharingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SharingAdapter adapter;
    private List<SharingModel> homeList;

    private DeviceDbOperation db = new DeviceDbOperation();
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing2);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        homeList = new ArrayList<>();
        adapter = new SharingAdapter(this, homeList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SharingActivity.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        DeviceDbHelper dbHelper = new DeviceDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        ArrayList<String> homes = db.GetAllHome(mDb);
        prepareHome(homes);
        checkUserInvites();
    }

    public void checkUserInvites() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final SqlOperationUserTable sqlOperationUserTable = new SqlOperationUserTable();
                UserTableDO user = sqlOperationUserTable.getUser();
                String name = null, access = null;
                for (final Map<String, String> x : user.getSharedAccess()) {
                    for (String key : x.keySet()) {
                        if (key.equals("Name")) name = x.get(key);
                        if (key.equals("Access")) access = x.get(key);

                        if (access != null && name != null) {
                            if (access.equals("invite")) {
                                Snackbar.make(findViewById(R.id.mCordinateLayout), name + "'s invite", Snackbar.LENGTH_INDEFINITE).setAction("ACCEPT", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sqlOperationUserTable.transferSharedDevices(x, SharingActivity.this);
                                    }
                                }).show();
                            }
                        }
                    }
                }
            }
        }).start();

    }

    /**
     * Adding few albums for testing
     */
    private void prepareHome(ArrayList<String> homes) {

        homeList.clear();
        int[] covers = new int[]{
                R.drawable.album1,
                R.drawable.album2,
                R.drawable.album3,
                R.drawable.album4,
                R.drawable.album5,
                R.drawable.album6,
                R.drawable.album7
        };

        for (String x : homes) {
            SharingModel a = new SharingModel(x, 0, covers[2]);
            homeList.add(a);
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
