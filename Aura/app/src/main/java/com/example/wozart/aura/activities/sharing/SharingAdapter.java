package com.example.wozart.aura.activities.sharing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.wozart.aura.MainActivity;
import com.example.wozart.aura.R;
import com.example.wozart.aura.noSql.SqlOperationUserTable;
import com.example.wozart.aura.sqlLite.device.DeviceDbHelper;
import com.example.wozart.aura.sqlLite.device.DeviceDbOperation;
import com.example.wozart.aura.tab.homeTab.Rooms;
import com.example.wozart.aura.utilities.Constant;

import java.util.List;

/**
 * Created by wozart on 15/02/18.
 */

public class SharingAdapter extends RecyclerView.Adapter<SharingAdapter.MyViewHolder> {

    private Context mContext;
    private List<SharingModel> homeList;
    MainActivity activity = new MainActivity();

    private DeviceDbOperation db = new DeviceDbOperation();
    private SQLiteDatabase mDb;
    private SqlOperationUserTable sqlOperationUserTable = new SqlOperationUserTable();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail1, thumbnail2, thumbnail3, thumbnail4, overflow;

        public MyViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            overflow = (ImageView) view.findViewById(R.id.overflow);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    public SharingAdapter(Context context, List<SharingModel> homeList) {
        this.mContext = context;
        this.homeList = homeList;
        DeviceDbHelper dbHelper = new DeviceDbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final SharingModel home = homeList.get(position);
        holder.title.setText(home.getName());
        //holder.count.setText(home.getNumberOfDevices());

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, home, position);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, SharingModel home, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_sharing, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(home, position));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private SharingModel homeSelected;
        private int Position;

        private MyMenuItemClickListener(SharingModel home, int position) {
            homeSelected = home;
            Position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_share_device:
                    getEmail(homeSelected.getName());
                    return true;

                case R.id.action_delete:
                    deleteItem(Position);
                    return true;
                default:
            }
            return false;
        }

        private void deleteItem(int position) {
            if (!homeSelected.equals("Hall")) {

            } else {
                Toast.makeText(mContext, "Cant delete default room", Toast.LENGTH_SHORT).show();
            }
        }

        private void updateCard(String roomName) {

            notifyItemChanged(Position);
        }

        private void editBoxPopUp(final String previousDevice) {

        }
    }

    /**
     * Sharing device
     */

    private void getEmail(final String home) {
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
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        sqlOperationUserTable.shareDevices(input.getText().toString().trim(), home);
                    }
                };
                thread.start();

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });
        alert.show();
    }

    @Override
    public int getItemCount() {
        return homeList.size();
    }
}
