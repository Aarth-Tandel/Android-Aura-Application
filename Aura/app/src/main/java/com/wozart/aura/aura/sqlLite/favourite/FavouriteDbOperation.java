package com.wozart.aura.aura.sqlLite.favourite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.wozart.aura.aura.tab.favouriteTab.Favourites;
import com.wozart.aura.aura.utilities.Constant;

import java.util.ArrayList;

import static com.wozart.aura.aura.utilities.Constant.CRUD_FAVOURITE;
import static com.wozart.aura.aura.utilities.Constant.UPDATE_LOAD_NAME;

/**
 * Created by wozart on 29/12/17.
 */

public class FavouriteDbOperation {
    public void insertFavourite(SQLiteDatabase db, String device, String load, String home, String room) {
        ContentValues values = new ContentValues();
        values.put(FavouriteContract.FavouriteEntry.DEVICE_NAME, device);
        values.put(FavouriteContract.FavouriteEntry.ROOM_NAME, room);
        values.put(FavouriteContract.FavouriteEntry.LOAD_NAME, load);
        values.put(FavouriteContract.FavouriteEntry.HOME_NAME, home);
        try {
            db.beginTransaction();
            db.insert(FavouriteContract.FavouriteEntry.TABLE_NAME, null, values);

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            //Too bad :(
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Favourites> getFavouriteDevice(SQLiteDatabase db, String home){
        if (home == null)
            home = "Home";
        String[] params = new String[]{home};
        ArrayList<Favourites> favourites = new ArrayList<>();
        Cursor cursor = db.rawQuery(Constant.GET_ALL_FAVOURITE, params);
        while(cursor.moveToNext()){
            Favourites fav = new Favourites();
            fav.setDevice(cursor.getString(1));
            fav.setName(cursor.getString(2));
            fav.setHome(cursor.getString(3));
            fav.setRoom(cursor.getString(4));
            favourites.add(fav);
        }
        cursor.close();
        return favourites;
    }

    public void removeFavourite(SQLiteDatabase db, String device, String load){
        String[] params = new String[]{device, load};
        db.delete(FavouriteContract.FavouriteEntry.TABLE_NAME, CRUD_FAVOURITE, params);
    }

    public void updateLoadName(SQLiteDatabase db, String device, String oldName, String load, String room, String home){
        String[] params = new String[]{home, room, device, oldName};
        ContentValues values = new ContentValues();
        values.put(FavouriteContract.FavouriteEntry.LOAD_NAME, load);
        db.update(FavouriteContract.FavouriteEntry.TABLE_NAME, values, UPDATE_LOAD_NAME, params);
    }
}
