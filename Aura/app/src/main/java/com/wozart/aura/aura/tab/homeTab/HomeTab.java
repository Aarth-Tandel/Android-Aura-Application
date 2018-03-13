package com.wozart.aura.aura.tab.homeTab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wozart.aura.aura.MainActivity;
import com.wozart.aura.R;
import com.wozart.aura.aura.sqlLite.device.DeviceDbHelper;
import com.wozart.aura.aura.sqlLite.device.DeviceDbOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wozart on 29/12/17.
 */

public class HomeTab extends Fragment {
    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private List<Rooms> roomsList;

    private DeviceDbOperation db = new DeviceDbOperation();
    private SQLiteDatabase mDb;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        roomsList = new ArrayList<>();
        adapter = new RoomAdapter(getActivity(), roomsList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new HomeTab.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getActivity()).nsdDiscovery();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);

            }
        });

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mRefresh, new IntentFilter("refreshHomeTab"));

        DeviceDbHelper dbHelper = new DeviceDbHelper(getActivity());
        mDb = dbHelper.getWritableDatabase();

        ArrayList<String> rooms = db.GetRooms(mDb, "Home");
        prepareRooms(rooms);

        return rootView;
    }


    /**
     * Adding few albums for testing
     */
    private void prepareRooms(ArrayList<String> rooms) {

        roomsList.clear();
        int[] covers = new int[]{
                R.drawable.album1,
                R.drawable.album2,
                R.drawable.album3,
                R.drawable.album4,
                R.drawable.album5,
                R.drawable.album6,
                R.drawable.album7
        };

        for (String x : rooms) {
            Rooms a = new Rooms(x, 0, covers[2]);
            roomsList.add(a);
        }

        adapter.notifyDataSetChanged();
    }

    private BroadcastReceiver mRefresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String home = intent.getStringExtra("home");
            if (home != null) {
                ArrayList<String> rooms = db.GetRooms(mDb, home);
                prepareRooms(rooms);
            } else {
                ArrayList<String> rooms = db.GetRooms(mDb, "Home");
                prepareRooms(rooms);
            }
        }
    };

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
