package com.example.wozart.aura.sqlLite.device;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wozart on 29/12/17.
 */

public class DeviceDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "device.db";
    private static final int DATABASE_VERSION = 1;

    public DeviceDbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_DEVICE_TABLE = "CREATE TABLE " +
                DeviceContract.DeviceEntry.TABLE_NAME + " (" +
                DeviceContract.DeviceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DeviceContract.DeviceEntry.DEVICE_NAME + " TEXT, " +
                DeviceContract.DeviceEntry.LOAD_1 + " TEXT DEFAULT 'Load 1', " +
                DeviceContract.DeviceEntry.LOAD_2 + " TEXT DEFAULT 'Load 2', " +
                DeviceContract.DeviceEntry.LOAD_3 + " TEXT DEFAULT 'Load 3', " +
                DeviceContract.DeviceEntry.LOAD_4 + " TEXT DEFAULT 'Load 4', " +
                DeviceContract.DeviceEntry.HOME_NAME + " TEXT, " +
                DeviceContract.DeviceEntry.ROOM_NAME + " TEXT DEFAULT 'Hall', " +
                DeviceContract.DeviceEntry.THING_NAME + " TEXT, " +
                DeviceContract.DeviceEntry.UIUD + " TEXT" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_DEVICE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + DeviceContract.DeviceEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
