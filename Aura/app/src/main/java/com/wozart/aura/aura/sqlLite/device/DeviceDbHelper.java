package com.wozart.aura.aura.sqlLite.device;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wozart on 29/12/17.
 */

public class DeviceDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "device.db";
    private static final int DATABASE_VERSION = 2;

    public DeviceDbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_DEVICE_TABLE = "CREATE TABLE " +
                DeviceContract.DeviceEntry.TABLE_NAME + " (" +
                DeviceContract.DeviceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DeviceContract.DeviceEntry.DEVICE_NAME + " TEXT, " +
                DeviceContract.DeviceEntry.LOAD + " TEXT, " +
                DeviceContract.DeviceEntry.HOME_NAME + " TEXT, " +
                DeviceContract.DeviceEntry.ROOM_NAME + " TEXT DEFAULT 'Hall', " +
                DeviceContract.DeviceEntry.THING_NAME + " TEXT, " +
                DeviceContract.DeviceEntry.UIUD + " TEXT, " +
                DeviceContract.DeviceEntry.ACCESS + " TEXT" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_DEVICE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DeviceContract.DeviceEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
