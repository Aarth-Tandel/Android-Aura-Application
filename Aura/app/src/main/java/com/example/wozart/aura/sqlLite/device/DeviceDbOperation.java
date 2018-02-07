package com.example.wozart.aura.sqlLite.device;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.wozart.amazonaws.models.nosql.DevicesTableDO;
import com.example.wozart.aura.activities.customization.CustomizationDevices;

import java.util.ArrayList;
import java.util.List;

import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.DEVICE_NAME;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.HOME_NAME;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.LOAD_1;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.LOAD_2;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.LOAD_3;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.LOAD_4;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.ROOM_NAME;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.TABLE_NAME;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.THING_NAME;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.UIUD;
import static com.example.wozart.aura.utilities.Constant.CHECK_DEVICES;
import static com.example.wozart.aura.utilities.Constant.CRUD_ROOM;
import static com.example.wozart.aura.utilities.Constant.DELETE_DEVICE;
import static com.example.wozart.aura.utilities.Constant.GET_ALL_DEVICES;
import static com.example.wozart.aura.utilities.Constant.GET_ALL_HOME;
import static com.example.wozart.aura.utilities.Constant.GET_DEVICES_FOR_THING;
import static com.example.wozart.aura.utilities.Constant.GET_DEVICES_IN_ROOM;
import static com.example.wozart.aura.utilities.Constant.GET_LOADS;
import static com.example.wozart.aura.utilities.Constant.GET_ROOMS;
import static com.example.wozart.aura.utilities.Constant.GET_ROOM_FOR_DEVICE;
import static com.example.wozart.aura.utilities.Constant.GET_THING_NAME;
import static com.example.wozart.aura.utilities.Constant.GET_UIUD;
import static com.example.wozart.aura.utilities.Constant.INSERT_DEVICES;
import static com.example.wozart.aura.utilities.Constant.INSERT_INITIAL_DATA;
import static com.example.wozart.aura.utilities.Constant.INSERT_ROOMS;
import static com.example.wozart.aura.utilities.Constant.UPDATE_DEVICE;
import static com.example.wozart.aura.utilities.Constant.UPDATE_LOAD1_NAME;
import static com.example.wozart.aura.utilities.Constant.UPDATE_LOAD2_NAME;
import static com.example.wozart.aura.utilities.Constant.UPDATE_LOAD3_NAME;
import static com.example.wozart.aura.utilities.Constant.UPDATE_LOAD4_NAME;
import static com.example.wozart.aura.utilities.Constant.UPDATE_THING_NAME;

/***************************************************************************
 * File Name : DeviceDbOperations
 * Author : Aarth Tandel
 * Date of Creation : 29/12/17
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class DeviceDbOperation {

    private static final String LOG_TAG = DeviceDbOperation.class.getSimpleName();

    public List<CustomizationDevices> GetAllDevices(SQLiteDatabase db) {
        List<CustomizationDevices> devices = new ArrayList<>();
        Cursor cursor = db.rawQuery(GET_ALL_DEVICES, null);
        while (cursor.moveToNext()) {
            CustomizationDevices dummyDevice = new CustomizationDevices();
            dummyDevice.setDevice(cursor.getString(1));
            dummyDevice.setHome(cursor.getString(6));
            dummyDevice.setRoom(cursor.getString(7));
            dummyDevice.setThing(cursor.getString(8));
            devices.add(dummyDevice);
        }
        cursor.close();
        return devices;
    }


    public ArrayList<String> GetAllHome(SQLiteDatabase db) {
        ArrayList<String> home = new ArrayList<>();
        Cursor cursor = db.rawQuery(GET_ALL_HOME, null);
        while (cursor.moveToNext()) {
            home.add(cursor.getString(0));
        }
        cursor.close();
        return home;
    }

    public void InsertBasicData(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(INSERT_INITIAL_DATA, null);
        if (cursor.getCount() == 0) {
            ContentValues value = new ContentValues();
            value.put(HOME_NAME, "Home");
            try {
                db.beginTransaction();
                db.insert(TABLE_NAME, null, value);

                db.setTransactionSuccessful();
            } catch (SQLException e) {
                //Too bad :(
            } finally {
                db.endTransaction();
                cursor.close();
            }
        }
    }

    public void InsertHome(SQLiteDatabase db, String home) {
        ContentValues value = new ContentValues();
        value.put(HOME_NAME, home);
        try {
            db.beginTransaction();
            db.insert(TABLE_NAME, null, value);

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            //Too bad :(
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<String> GetRooms(SQLiteDatabase db, String home) {
        String[] params = new String[]{home};
        Cursor cursor = db.rawQuery(GET_ROOMS, params);
        ArrayList<String> room = new ArrayList<>();
        while (cursor.moveToNext()) {
            room.add(cursor.getString(0));
        }
        cursor.close();
        return room;
    }

    public void InsertRoom(SQLiteDatabase db, String home, String room) {
        String x = "null";
        String[] params = new String[]{x};
        Cursor cursor = db.rawQuery(INSERT_ROOMS, params);
        if (cursor.getCount() == 0) {
            ContentValues value = new ContentValues();
            value.put(HOME_NAME, home);
            value.put(ROOM_NAME, room);

            try {
                db.beginTransaction();
                db.insert(TABLE_NAME, null, value);

                db.setTransactionSuccessful();
            } catch (SQLException e) {
                //Too bad :(
            } finally {
                db.endTransaction();

            }
        }
        cursor.close();
    }

    public void DeleteRoom(SQLiteDatabase db, String home, String room) {
        db.delete(TABLE_NAME, CRUD_ROOM, new String[]{home, room});
    }

    public void UpdateRoom(SQLiteDatabase db, String home, String previousRoom, String room) {
        ContentValues cv = new ContentValues();
        cv.put(ROOM_NAME, room);
        db.update(TABLE_NAME, cv, CRUD_ROOM, new String[]{home, previousRoom});
    }

    public void UpdateRoomAndHome(SQLiteDatabase db, String home, String room, String device) {
        ContentValues cv = new ContentValues();
        cv.put(ROOM_NAME, room);
        cv.put(HOME_NAME, home);
        db.update(TABLE_NAME, cv, UPDATE_DEVICE, new String[]{device});
    }

    public void TransferDeletedDevices(SQLiteDatabase db, String home, String room) {
        ContentValues cv = new ContentValues();
        cv.put(ROOM_NAME, "Hall");
        db.update(TABLE_NAME, cv, CRUD_ROOM, new String[]{home, room});
    }

    public ArrayList<String> GetDevicesInRoom(SQLiteDatabase db, String room, String home) {
        ArrayList<String> devices = new ArrayList<>();
        String[] params = new String[]{home, room};
        Cursor cursor = db.rawQuery(GET_DEVICES_IN_ROOM, params);
        while (cursor.moveToNext()) {
            devices.add(cursor.getString(0));
        }
        cursor.close();
        return devices;
    }

    public ArrayList<String> GetLoads(SQLiteDatabase db, String device) {
        String[] params = new String[]{device};
        Cursor cursor = db.rawQuery(GET_LOADS, params);
        ArrayList<String> loads = new ArrayList<>();
        while (cursor.moveToNext()) {
            loads.add(cursor.getString(0));
            loads.add(cursor.getString(1));
            loads.add(cursor.getString(2));
            loads.add(cursor.getString(3));

        }
        return loads;
    }

    public void AddDevice(SQLiteDatabase db, String room, String home, String device, String uiud, String thing) {

        ArrayList<String> devicesDuplicate = new ArrayList<>();
        Cursor cursor = db.rawQuery(INSERT_DEVICES, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(0) != null)
                devicesDuplicate.add(cursor.getString(0));
        }
        cursor.close();
        Boolean flag = true;

        if (devicesDuplicate.isEmpty()) {
            ContentValues cv = new ContentValues();
            cv.put(ROOM_NAME, room);
            cv.put(HOME_NAME, home);
            cv.put(DEVICE_NAME, device);
            cv.put(UIUD, uiud);
            cv.put(THING_NAME, thing);

            try {
                db.beginTransaction();
                db.insert(TABLE_NAME, null, cv);

                db.setTransactionSuccessful();
            } catch (SQLException e) {
                //Too bad :(
            } finally {
                db.endTransaction();
            }
            return;
        }

        for (String x : devicesDuplicate) {
            if (x.equals(device)) {
                updateAlreadyAddedDevice(db,device,uiud);
                flag = false;
            }
        }

        if (flag) {
            ContentValues cv = new ContentValues();
            cv.put(ROOM_NAME, room);
            cv.put(HOME_NAME, home);
            cv.put(DEVICE_NAME, device);
            cv.put(UIUD, uiud);
            cv.put(THING_NAME,thing);

            try {
                db.beginTransaction();
                db.insert(TABLE_NAME, null, cv);

                db.setTransactionSuccessful();
            } catch (SQLException e) {
                //Too bad :(
            } finally {
                db.endTransaction();

            }
        }
    }

    private void updateAlreadyAddedDevice(SQLiteDatabase db, String device, String uiud){
        ContentValues cv = new ContentValues();
        String thing = null;
        cv.put(UIUD, uiud);
        cv.put(THING_NAME, thing);
        db.update(TABLE_NAME, cv, UPDATE_THING_NAME, new String[]{device});
    }

    public void devicesFromAws(SQLiteDatabase db, ArrayList<DevicesTableDO> devices) {

        if(devices == null || devices.size() == 0) return;
        if (devices.get(0) == null) {
            Log.i(LOG_TAG, "No AWS devices");
            return;
        }
        for (DevicesTableDO x : devices) {
            boolean isThingAlreadyPresent;
            isThingAlreadyPresent = checkDevice(db, x.getDeviceId());
            if (!isThingAlreadyPresent) {
                return;
            } else {
                ContentValues value = new ContentValues();
                value.put(ROOM_NAME, "Hall");
                value.put(THING_NAME, x.getThing());
                value.put(DEVICE_NAME, x.getDeviceId());
                value.put(HOME_NAME, "Home");

                try {
                    db.beginTransaction();
                    db.insert(TABLE_NAME, null, value);

                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    //Too bad :(
                } finally {
                    db.endTransaction();
                }
            }
        }
    }

    private boolean checkDevice(SQLiteDatabase db, String device) {
        String[] params = new String[]{device};
        Cursor cursor = db.rawQuery(CHECK_DEVICES, params);
        if (cursor.getCount() == 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public void updateThing(SQLiteDatabase db, String device, String thing){
        ContentValues cv = new ContentValues();
        cv.put(THING_NAME, thing);
        db.update(TABLE_NAME, cv, UPDATE_THING_NAME, new String[]{device});
    }

    public ArrayList<String> GetThingName(SQLiteDatabase db) {
        ArrayList<String> devices = new ArrayList<>();
        Cursor cursor = db.rawQuery(GET_THING_NAME, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(0) != null)
                devices.add(cursor.getString(0));
        }
        cursor.close();
        return devices;
    }

    public String GetDeviceForThing(SQLiteDatabase db, String thing) {
        String devices = null;
        String[] params = new String[]{thing};
        Cursor cursor = db.rawQuery(GET_DEVICES_FOR_THING, params);
        while (cursor.moveToNext()) {
            if (cursor.getString(0) != null)
                devices = cursor.getString(0);
        }
        cursor.close();
        return devices;
    }

    public String GetDeviceRoom(SQLiteDatabase db, String device){
        String devices = null;
        String[] params = new String[]{device};
        Cursor cursor = db.rawQuery(GET_ROOM_FOR_DEVICE, params);
        while (cursor.moveToNext()) {
            if (cursor.getString(0) != null)
                devices = cursor.getString(0);
        }
        cursor.close();
        return devices;
    }

    public void updateLoadName(SQLiteDatabase db, String oldName, String home, String room, int loadNumber, String load) {
        String[] params = new String[]{home, room, oldName};
        ContentValues values = new ContentValues();
        switch (loadNumber) {
            case 0:
                values.put(LOAD_1, load);
                db.update(TABLE_NAME, values, UPDATE_LOAD1_NAME, params);
                break;
            case 1:
                values.put(LOAD_2, load);
                db.update(TABLE_NAME, values, UPDATE_LOAD2_NAME, params);
                break;
            case 2:
                values.put(LOAD_3, load);
                db.update(TABLE_NAME, values, UPDATE_LOAD3_NAME, params);
                break;
            case 3:
                values.put(LOAD_4, load);
                db.update(TABLE_NAME, values, UPDATE_LOAD4_NAME, params);
                break;
        }
    }

    public void removeDevice(SQLiteDatabase db, String device){
        String[] params = new String[]{device};
        db.delete(TABLE_NAME, DELETE_DEVICE, params);
    }

    public static String getUiud(SQLiteDatabase db, String device){
        String[] params = new String[]{device};
        Cursor cursor = db.rawQuery(GET_UIUD, params);
        String uiud = null;
        while (cursor.moveToNext()) {
            uiud = cursor.getString(0);
        }
        cursor.close();
        return uiud;
    }

}
