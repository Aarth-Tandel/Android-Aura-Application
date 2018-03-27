package com.wozart.aura.aura.tab.favouriteTab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wozart.aura.aura.MainActivity;
import com.wozart.aura.R;
import com.wozart.aura.aura.sqlLite.favourite.FavouriteDbHelper;
import com.wozart.aura.aura.sqlLite.favourite.FavouriteDbOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wozart on 29/12/17.
 */

public class FavouriteTab extends Fragment {

    private RecyclerView FAV_recyclerView;
    private FavouritesAdapter FAV_adapter;
    private List<Favourites> favouriteList;

    private FavouriteDbOperation favouriteDb = new FavouriteDbOperation();
    private SQLiteDatabase mFavouriteDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.favourite_fragment, container, false);

        FAV_recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        favouriteList = new ArrayList<>();
        FAV_adapter = new FavouritesAdapter(getActivity(), favouriteList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        FAV_recyclerView.setLayoutManager(mLayoutManager);
        FAV_recyclerView.addItemDecoration(new FavouriteTab.GridSpacingItemDecoration(2, dpToPx(10), true));
        FAV_recyclerView.setItemAnimator(new DefaultItemAnimator());
        FAV_recyclerView.setAdapter(FAV_adapter);

        FavouriteDbHelper dbFavouriteHelper = new FavouriteDbHelper(getActivity());
        mFavouriteDb = dbFavouriteHelper.getWritableDatabase();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mRefresh, new IntentFilter("refreshFavTab"));

        ArrayList<Favourites> favouriteLoads = favouriteDb.getFavouriteDevice(mFavouriteDb, MainActivity.SELECTED_HOME);
        prepareRooms(favouriteLoads);
        return rootView;
    }

    /**
     * Adding few albums for testing
     */
    private void prepareRooms(ArrayList<Favourites> favouriteLoads) {

        favouriteList.clear();
        int[] covers = new int[]{
                R.drawable.album1,
                R.drawable.album2,
                R.drawable.album3,
                R.drawable.album4,
                R.drawable.album5,
                R.drawable.album6,
                R.drawable.album7
        };
        for(Favourites x : favouriteLoads){
            Favourites fav = new Favourites(x.getName(), x.getHome(), x.getDevice(), x.getRoom(), covers[2]);
            favouriteList.add(fav);
        }
        FAV_adapter.notifyDataSetChanged();
    }

    private BroadcastReceiver mRefresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String home = intent.getStringExtra("home");
            if(home != null){
                ArrayList<Favourites> rooms = favouriteDb.getFavouriteDevice(mFavouriteDb, home);
                prepareRooms(rooms);
            } else {
                ArrayList<Favourites> rooms = favouriteDb.getFavouriteDevice(mFavouriteDb, "Home");
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
