package com.wozart.aura.aura.sqlLite.device;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wozart.aura.R;

/**
 * Created by wozart on 29/12/17.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>{
    private Context mContext;
    private Cursor mCursor;

    public DeviceAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        mCursor = cursor;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.content_main, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {

        if(!mCursor.moveToPosition(position))
            return;

        String name = mCursor.getString(mCursor.getColumnIndex(DeviceContract.DeviceEntry.DEVICE_NAME));
        holder.nameTextView.setText(name);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    /**
     * Inner class to hold the views needed to display a single item in the recycler-view
     */
    class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            // nameTextView = (TextView) itemView.findViewById(R.id.tv_devices);
        }

    }
}
