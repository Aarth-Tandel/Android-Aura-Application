package com.example.wozart.aura.tab.favouriteTab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wozart.aura.R;
import com.example.wozart.aura.sqlLite.device.DeviceDbHelper;
import com.example.wozart.aura.sqlLite.device.DeviceDbOperation;
import com.example.wozart.aura.sqlLite.favourite.FavouriteDbHelper;
import com.example.wozart.aura.sqlLite.favourite.FavouriteDbOperation;

import java.util.List;

/**
 * Created by wozart on 29/12/17.
 */

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.MyViewHolder> {

    private Context mContext;
    private List<Favourites> favouriteList;

    private DeviceDbOperation db = new DeviceDbOperation();
    private SQLiteDatabase mDb;

    private FavouriteDbOperation favouriteDb = new FavouriteDbOperation();
    private SQLiteDatabase mFavouriteDb;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, room;
        private ImageView thumbnail1, overflow;

        private MyViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            room = (TextView) view.findViewById(R.id.tv_state);
            overflow = (ImageView) view.findViewById(R.id.overflow);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, title.getText() + " Selected", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public FavouritesAdapter(Context mContext, List<Favourites> List) {
        this.mContext = mContext;
        this.favouriteList = List;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.load_card, parent, false);

        DeviceDbHelper dbHelper = new DeviceDbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
        db.InsertBasicData(mDb);

        FavouriteDbHelper dbFavouriteHelper = new FavouriteDbHelper(mContext);
        mFavouriteDb = dbFavouriteHelper.getWritableDatabase();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Favourites load = favouriteList.get(position);
        holder.title.setText(load.getName());
        //holder.count.setText(rooms.getNumOfDevices() + " Devices");
        holder.room.setText(load.getRoom());
        // loading rooms cover using Glide library
        //Glide.with(mContext).load(load.getThumbnail()).into(holder.thumbnail1);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, load, position);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, Favourites load, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_load, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(load, position));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private Favourites favourite;
        private int Position;

        private MyMenuItemClickListener(Favourites load, int position) {
            favourite = load;
            Position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.action_play_next:
                    deleteItem(Position);

                    return true;
                default:
            }
            return false;
        }

        private void deleteItem(int position) {
            favouriteList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, favouriteList.size());
            favouriteDb.removeFavourite(mFavouriteDb, favourite.getDevice(), favourite.getName());
        }

        private void editBoxPopUp(final String previousDevice) {
            final Boolean[] flag = {true};
            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            final EditText input = new EditText(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alert.setView(input);
            alert.setMessage("Change name of " + previousDevice);
            alert.setTitle("Edit Room");
            alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // what ever you want to do with No option.
                }
            });
            alert.show();
        }
    }

    @Override
    public int getItemCount() {
        return favouriteList.size();
    }
}
