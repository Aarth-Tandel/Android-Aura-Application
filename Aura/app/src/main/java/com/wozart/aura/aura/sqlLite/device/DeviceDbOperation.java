package com.wozart.aura.aura.sqlLite.device;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wozart.aura.amazonaws.models.nosql.DevicesTableDO;
import com.wozart.aura.aura.activities.customization.CustomizationDevices;
import com.wozart.aura.aura.activities.sharing.SharingAdapter;
import com.wozart.aura.aura.activities.sharing.SharingModel;
import com.wozart.aura.aura.utilities.Constant;

import java.util.ArrayList;
import java.util.List;

import static com.wozart.aura.aura.utilities.Constant.CHECK_DEVICES;
import static com.wozart.aura.aura.utilities.Constant.CRUD_ROOM;
import static com.wozart.aura.aura.utilities.Constant.DELETE_DEVICE;
import static com.wozart.aura.aura.utilities.Constant.DELETE_HOME;
import static com.wozart.aura.aura.utilities.Constant.GET_ALL_DEVICES;
import static com.wozart.aura.aura.utilities.Constant.GET_ALL_HOME;
import static com.wozart.aura.aura.utilities.Constant.GET_ALL_SHARED_DEVICES_FOR_HOME;
import static com.wozart.aura.aura.utilities.Constant.GET_DEVICES_FOR_THING;
import static com.wozart.aura.aura.utilities.Constant.GET_DEVICES_IN_ROOM;
import static com.wozart.aura.aura.utilities.Constant.GET_LOADS;
import static com.wozart.aura.aura.utilities.Constant.GET_ROOMS;
import static com.wozart.aura.aura.utilities.Constant.GET_ROOM_FOR_DEVICE;
import static com.wozart.aura.aura.utilities.Constant.GET_THING_NAME;
import static com.wozart.aura.aura.utilities.Constant.GET_UIUD;
import static com.wozart.aura.aura.utilities.Constant.INSERT_DEVICES;
import static com.wozart.aura.aura.utilities.Constant.INSERT_INITIAL_DATA;
import static com.wozart.aura.aura.utilities.Constant.INSERT_ROOMS;
import static com.wozart.aura.aura.utilities.Constant.UPDATE_DEVICE;
import static com.wozart.aura.aura.utilities.Constant.UPDATE_LOAD1_NAME;
import static com.wozart.aura.aura.utilities.Constant.UPDATE_LOAD2_NAME;
import static com.wozart.aura.aura.utilities.Constant.UPDATE_LOAD3_NAME;
import static com.wozart.aura.aura.utilities.Constant.UPDATE_LOAD4_NAME;
import static com.wozart.aura.aura.utilities.Constant.UPDATE_THING_NAME;

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

    public void getAll(SQLiteDatabase db) {
        List<CustomizationDevices> devices = new ArrayList<>();
        String access = null, uiud = null;
        Cursor cursor = db.rawQuery("Select * from " + DeviceContract.DeviceEntry.TABLE_NAME, null);
        while (cursor.moveToNext()) {
            CustomizationDevices dummyDevice = new CustomizationDevices();
            dummyDevice.setDevice(cursor.getString(1));
            dummyDevice.setHome(cursor.getString(2));
            dummyDevice.setRoom(cursor.getString(3));
            dummyDevice.setThing(cursor.getString(4));
            access = cursor.getString(5);
            uiud = cursor.getString(6);
            devices.add(dummyDevice);
        }
        cursor.close();
        return;
    }

    public void devicesFromAws(SQLiteDatabase db, ArrayList<DevicesTableDO> devices) {

        if (devices == null || devices.size() == 0) return;
        if (devices.get(0) == null) {
            Log.i(LOG_TAG, "No AWS devices");
            return;
        }
        for (DevicesTableDO x : devices) {
            boolean isThingAlreadyPresent;
            isThingAlreadyPresent = checkDevice(db, x.getName());
            if (!isThingAlreadyPresent) {
                return;
            } else {
                ContentValues value = new ContentValues();
                value.put(DeviceContract.DeviceEntry.ROOM_NAME, x.getRoom());
                value.put(DeviceContract.DeviceEntry.THING_NAME, x.getThing());
                value.put(DeviceContract.DeviceEntry.DEVICE_NAME, x.getName());
                value.put(DeviceContract.DeviceEntry.ACCESS, x.getMaster());
                if (x.getMaster().equals(Constant.IDENTITY_ID))
                    value.put(DeviceContract.DeviceEntry.HOME_NAME, x.getHome());
                else
                    value.put(DeviceContract.DeviceEntry.HOME_NAME, x.getHome() + "(Guest)");

                value.put(DeviceContract.DeviceEntry.UIUD, x.getUIUD());

                try {
                    db.beginTransaction();
                    db.insert(DeviceContract.DeviceEntry.TABLE_NAME, null, value);

                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    //Too bad :(
                } finally {
                    db.endTransaction();
                }
            }
        }
    }

    public void InsertBasicData(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(INSERT_INITIAL_DATA, null);
        if (cursor.getCount() == 0) {
            ContentValues value = new ContentValues();
            value.put(DeviceContract.DeviceEntry.HOME_NAME, "Home");
            try {
                db.beginTransaction();
                db.insert(DeviceContract.DeviceEntry.TABLE_NAME, null, value);

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
        value.put(DeviceContract.DeviceEntry.HOME_NAME, home);
        try {
            db.beginTransaction();
            db.insert(DeviceContract.DeviceEntry.TABLE_NAME, null, value);

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            //Too bad :(
        } finally {
            db.endTransaction();
        }
    }

    public void InsertRoom(SQLiteDatabase db, String home, String room) {
        String x = "null";
        String[] params = new String[]{x};
        Cursor cursor = db.rawQuery(INSERT_ROOMS, params);
        if (cursor.getCount() == 0) {
            ContentValues value = new ContentValues();
            value.put(DeviceContract.DeviceEntry.HOME_NAME, home);
            value.put(DeviceContract.DeviceEntry.ROOM_NAME, room);

            try {
                db.beginTransaction();
                db.insert(DeviceContract.DeviceEntry.TABLE_NAME, null, value);

                db.setTransactionSuccessful();
            } catch (SQLException e) {
                //Too bad :(
            } finally {
                db.endTransaction();

            }
        }
        cursor.close();
    }

    public void AddDevice(SQLiteDatabase db, String room, String home, String device, String uiud) {

        String thing = null, access = "master";
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
            cv.put(DeviceContract.DeviceEntry.ROOM_NAME, room);
            cv.put(DeviceContract.DeviceEntry.HOME_NAME, home);
            cv.put(DeviceContract.DeviceEntry.DEVICE_NAME, device);
            cv.put(DeviceContract.DeviceEntry.UIUD, uiud);
            cv.put(DeviceContract.DeviceEntry.THING_NAME, thing);
            cv.put(DeviceContract.DeviceEntry.ACCESS, access);

            try {
                db.beginTransaction();
                db.insert(DeviceContract.DeviceEntry.TABLE_NAME, null, cv);

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
                updateAlreadyAddedDevice(db, device, uiud);
                flag = false;
            }
        }

        if (flag) {
            ContentValues cv = new ContentValues();
            cv.put(DeviceContract.DeviceEntry.ROOM_NAME, room);
            cv.put(DeviceContract.DeviceEntry.HOME_NAME, home);
            cv.put(DeviceContract.DeviceEntry.DEVICE_NAME, device);
            cv.put(DeviceContract.DeviceEntry.UIUD, uiud);
            cv.put(DeviceContract.DeviceEntry.THING_NAME, thing);
            cv.put(DeviceContract.DeviceEntry.ACCESS, access);

            try {
                db.beginTransaction();
                db.insert(DeviceContract.DeviceEntry.TABLE_NAME, null, cv);

                db.setTransactionSuccessful();
            } catch (SQLException e) {
                //Too bad :(
            } finally {
                db.endTransaction();

            }
        }
    }

    public List<CustomizationDevices> GetAllDevices(SQLiteDatabase db) {
        List<CustomizationDevices> devices = new ArrayList<>();
        Cursor cursor = db.rawQuery(GET_ALL_DEVICES, null);
        while (cursor.moveToNext()) {
            CustomizationDevices dummyDevice = new CustomizationDevices();
            dummyDevice.setDevice(cursor.getString(0));
            dummyDevice.setHome(cursor.getString(1));
            dummyDevice.setRoom(cursor.getString(2));
            dummyDevice.setThing(cursor.getString(3));
            devices.add(dummyDevice);
        }
        cursor.close();
        return devices;
    }

    public List<CustomizationDevices> getAllUserMasterDevices(SQLiteDatabase db) {
        List<CustomizationDevices> devices = new ArrayList<>();
        Cursor cursor = db.rawQuery(GET_ALL_DEVICES, null);
        while (cursor.moveToNext()) {
            CustomizationDevices dummyDevice = new CustomizationDevices();
            if (cursor.getString(0) != null) {
                if (!cursor.getString(1).contains("Guest")) {
                    dummyDevice.setDevice(cursor.getString(0));
                    dummyDevice.setHome(cursor.getString(1));
                    dummyDevice.setRoom(cursor.getString(2));
                    dummyDevice.setThing(cursor.getString(3));
                    devices.add(dummyDevice);
                }
            }
        }
        cursor.close();
        return devices;
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

    public ArrayList<String> GetAllHome(SQLiteDatabase db) {
        ArrayList<String> home = new ArrayList<>();
        Cursor cursor = db.rawQuery(GET_ALL_HOME, null);
        while (cursor.moveToNext()) {
            home.add(cursor.getString(0));
        }
        cursor.close();
        return home;
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

    public static String getUiud(SQLiteDatabase db, String device) {
        String[] params = new String[]{device};
        Cursor cursor = db.rawQuery(GET_UIUD, params);
        String uiud = null;
        while (cursor.moveToNext()) {
            uiud = cursor.getString(0);
        }
        cursor.close();
        return uiud;
    }

    public ArrayList<SharingModel> getSharedDevices(SQLiteDatabase db, String home){
        String[] params = new String[]{home};
        Cursor cursor =db.rawQuery(GET_ALL_SHARED_DEVICES_FOR_HOME, params);
        ArrayList<SharingModel> sharedDevices = new ArrayList<>();
        while(cursor.moveToNext()){
            SharingModel device = new SharingModel();
            if(cursor.getString(0) != null){
                device.setName(cursor.getString(0));
                device.setMaster(cursor.getString(1));
                device.setDeviceId(cursor.getString(2));
                sharedDevices.add(device);
            }
        }
        cursor.close();
        return sharedDevices;
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

    public void UpdateRoom(SQLiteDatabase db, String home, String previousRoom, String room) {
        ContentValues cv = new ContentValues();
        cv.put(DeviceContract.DeviceEntry.ROOM_NAME, room);
        db.update(DeviceContract.DeviceEntry.TABLE_NAME, cv, CRUD_ROOM, new String[]{home, previousRoom});
    }

    public void UpdateRoomAndHome(SQLiteDatabase db, String home, String room, String device) {
        ContentValues cv = new ContentValues();
        cv.put(DeviceContract.DeviceEntry.ROOM_NAME, room);
        cv.put(DeviceContract.DeviceEntry.HOME_NAME, home);
        db.update(DeviceContract.DeviceEntry.TABLE_NAME, cv, UPDATE_DEVICE, new String[]{device});
    }

    public void TransferDeletedDevices(SQLiteDatabase db, String home, String room) {
        ContentValues cv = new ContentValues();
        cv.put(DeviceContract.DeviceEntry.ROOM_NAME, "Hall");
        db.update(DeviceContract.DeviceEntry.TABLE_NAME, cv, CRUD_ROOM, new String[]{home, room});
    }

    private void updateAlreadyAddedDevice(SQLiteDatabase db, String device, String uiud) {
        ContentValues cv = new ContentValues();
        String thing = null;
        cv.put(DeviceContract.DeviceEntry.UIUD, uiud);
        cv.put(DeviceContract.DeviceEntry.THING_NAME, thing);
        db.update(DeviceContract.DeviceEntry.TABLE_NAME, cv, UPDATE_THING_NAME, new String[]{device});
    }

    public void updateThing(SQLiteDatabase db, String device, String thing) {
        ContentValues cv = new ContentValues();
        cv.put(DeviceContract.DeviceEntry.THING_NAME, thing);
        db.update(DeviceContract.DeviceEntry.TABLE_NAME, cv, UPDATE_THING_NAME, new String[]{device});
    }

    public void updateLoadName(SQLiteDatabase db, String oldName, String home, String room, int loadNumber, String load) {
        String[] params = new String[]{home, room, oldName};
        ContentValues values = new ContentValues();
        switch (loadNumber) {
            case 0:
                values.put(DeviceContract.DeviceEntry.LOAD_1, load);
                db.update(DeviceContract.DeviceEntry.TABLE_NAME, values, UPDATE_LOAD1_NAME, params);
                break;
            case 1:
                values.put(DeviceContract.DeviceEntry.LOAD_2, load);
                db.update(DeviceContract.DeviceEntry.TABLE_NAME, values, UPDATE_LOAD2_NAME, params);
                break;
            case 2:
                values.put(DeviceContract.DeviceEntry.LOAD_3, load);
                db.update(DeviceContract.DeviceEntry.TABLE_NAME, values, UPDATE_LOAD3_NAME, params);
                break;
            case 3:
                values.put(DeviceContract.DeviceEntry.LOAD_4, load);
                db.update(DeviceContract.DeviceEntry.TABLE_NAME, values, UPDATE_LOAD4_NAME, params);
                break;
        }
    }

    public void removeDevice(SQLiteDatabase db, String device) {
        String[] params = new String[]{device};
        db.delete(DeviceContract.DeviceEntry.TABLE_NAME, DELETE_DEVICE, params);
    }

    public void DeleteRoom(SQLiteDatabase db, String home, String room) {
        db.delete(DeviceContract.DeviceEntry.TABLE_NAME, CRUD_ROOM, new String[]{home, room});
    }

    public void deleteHome(SQLiteDatabase db, String home){
        String[] params = new String[]{home};
        db.delete(DeviceContract.DeviceEntry.TABLE_NAME,DELETE_HOME,params);
    }
}
