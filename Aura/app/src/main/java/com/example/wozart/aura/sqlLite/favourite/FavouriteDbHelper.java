package com.example.wozart.aura.sqlLite.favourite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wozart on 29/12/17.
 */

public class FavouriteDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favourite.db";
    private static final int DATABASE_VERSION = 1;

    public FavouriteDbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_DEVICE_TABLE = "CREATE TABLE " +
                FavouriteContract.FavouriteEntry.TABLE_NAME + " (" +
                FavouriteContract.FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavouriteContract.FavouriteEntry.DEVICE_NAME + " TEXT, " +
                FavouriteContract.FavouriteEntry.LOAD_NAME + " TEXT, " +
                FavouriteContract.FavouriteEntry.HOME_NAME + " TEXT, " +
                FavouriteContract.FavouriteEntry.ROOM_NAME + " TEXT" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_DEVICE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + FavouriteContract.FavouriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
