package com.example.wozart.aura.utilities;

import com.example.wozart.aura.sqlLite.favourite.FavouriteContract;

import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.DEVICE_NAME;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.HOME_NAME;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.LOAD_1;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.LOAD_2;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.LOAD_3;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.LOAD_4;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.ROOM_NAME;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.TABLE_NAME;
import static com.example.wozart.aura.sqlLite.device.DeviceContract.DeviceEntry.THING_NAME;

/**
 * Created by wozart on 29/12/17.
 */

public class Constant {
    //User's and Device Constants
    public static String IDENTITY_ID;
    public static final String CLOSED_CONNECTION = "client_closed_connection";
    public static final String SERVER_NOT_REACHABLE = "Server Not Reachable";
    public static final String UNPAIRED = "00000000000000000";
    public static final String PAIRED = "00000000000000011";
    public static final String WRONG_PIN = "xx:xx:xx:xx:xx:xx";
    public static final String NETWORK_SSID = "Aura";
    public static final String URL = "http://192.168.10.1/";
    public static final int MAX_HOME = 5;

    //SQL - Lite Queries for Device DB
    public static final String GET_ALL_DEVICES = "select * from " + TABLE_NAME;
    public static final String GET_ALL_HOME = "select distinct " + HOME_NAME + " from " + TABLE_NAME;
    public static final String INSERT_INITIAL_DATA = "select * from " + TABLE_NAME;
    public static final String GET_ROOMS = "select distinct " + ROOM_NAME + " from " + TABLE_NAME + " where " + HOME_NAME + " = ?";
    public static final String INSERT_ROOMS = "select " + HOME_NAME + " from " + TABLE_NAME + " where " + ROOM_NAME + " = ?";
    public static final String CRUD_ROOM = HOME_NAME + " =? and " + ROOM_NAME + " =? ";
    public static final String GET_DEVICES_IN_ROOM = "select " + DEVICE_NAME + " from " + TABLE_NAME + " where " + HOME_NAME + " = ? and " + ROOM_NAME + " =?";
    public static final String GET_LOADS = "select " + LOAD_1 + ", " + LOAD_2 + ", " + LOAD_3 + ", " + LOAD_4 + " from " + TABLE_NAME + " where " + DEVICE_NAME + " = ?";
    public static final String INSERT_DEVICES = "select " + DEVICE_NAME + " from " + TABLE_NAME;
    public static final String CHECK_DEVICES = "select " + DEVICE_NAME + " from " + TABLE_NAME + " where " + DEVICE_NAME + " = ?";
    public static final String GET_THING_NAME = "select " + THING_NAME + " from " + TABLE_NAME;
    public static final String GET_DEVICES_FOR_THING = "select " + DEVICE_NAME + " from " + TABLE_NAME + " where " + THING_NAME + " = ?";
    public static final String UPDATE_DEVICE = DEVICE_NAME + "=?";
    public static final String UPDATE_LOAD1_NAME = HOME_NAME + " =? and " + ROOM_NAME + " =? and " + LOAD_1 + " =?";
    public static final String UPDATE_LOAD2_NAME = HOME_NAME + " =? and " + ROOM_NAME + " =? and " + LOAD_2 + " =?";
    public static final String UPDATE_LOAD3_NAME = HOME_NAME + " =? and " + ROOM_NAME + " =? and " + LOAD_3 + " =?";
    public static final String UPDATE_LOAD4_NAME = HOME_NAME + " =? and " + ROOM_NAME + " =? and " + LOAD_4 + " =?";
    public static final String UPDATE_THING_NAME = DEVICE_NAME + "=?";
    public static final String GET_ROOM_FOR_DEVICE = "select " + ROOM_NAME + " from " + TABLE_NAME + " where " + DEVICE_NAME + " =?";
    public static final String DELETE_DEVICE = DEVICE_NAME + " = ?";

    //SQL - Lite Queries for Favourite DB
    public static final String GET_ALL_FAVOURITE = "select * from " + FavouriteContract.FavouriteEntry.TABLE_NAME + " where " + FavouriteContract.FavouriteEntry.HOME_NAME + " = ?";
    public static final String CRUD_FAVOURITE = FavouriteContract.FavouriteEntry.DEVICE_NAME + " = ? and " + FavouriteContract.FavouriteEntry.LOAD_NAME + " = ?";
    public static final String UPDATE_LOAD_NAME = FavouriteContract.FavouriteEntry.HOME_NAME + " =? and " + FavouriteContract.FavouriteEntry.ROOM_NAME + " =? and " + FavouriteContract.FavouriteEntry.DEVICE_NAME + " =? and " + FavouriteContract.FavouriteEntry.LOAD_NAME + " =?";

    //AWS MQTT Messages
    public static final String AWS_UPDATE_ACCEPTED = "$aws/things/%s/shadow/update/accepted";
    public static final String AWS_GET_ACCEPTED = "$aws/things/%s/shadow/get/accepted";
    public static final String AWS_UPDATE = "$aws/things/%s/shadow/update";
    public static final String AWS_GET = "$aws/things/%s/shadow/get";

}
