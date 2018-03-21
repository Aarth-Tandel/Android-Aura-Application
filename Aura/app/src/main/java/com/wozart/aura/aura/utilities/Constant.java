package com.wozart.aura.aura.utilities;

import com.wozart.aura.aura.sqlLite.favourite.FavouriteContract;
import com.wozart.aura.aura.sqlLite.device.DeviceContract;

/***************************************************************************
 * File Name : Constant
 * Author : Aarth Tandel
 * Date of Creation : 29/12/17
 * Description : All constant values
 * Revision History :
 * ____________________________________________________________________________
 * 30/01/18  Aarth Tandel - Sharing Details
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * 30/01/18 Version 1.1
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class Constant {
    //User's and Device Constants
    public static String IDENTITY_ID;
    public static String USERNAME = null;
    public static final String CLOSED_CONNECTION = "client_closed_connection";
    public static final String SERVER_NOT_REACHABLE = "Server Not Reachable";
    public static final String UNPAIRED = "xxxxxxxxxxxxxxxxx";
    public static final String NETWORK_SSID = "Aura";
    public static final String URL = "http://192.168.10.1/";
    public static final int MAX_HOME = 5;

    //SQL - Lite Queries for Device DB
    public static final String GET_ALL_SHARED_DEVICES_FOR_HOME = "select " + DeviceContract.DeviceEntry.DEVICE_NAME + ", " + DeviceContract.DeviceEntry.ACCESS + ", " + DeviceContract.DeviceEntry.UIUD + " from " + DeviceContract.DeviceEntry.TABLE_NAME + " where " + DeviceContract.DeviceEntry.HOME_NAME + "=?";
    public static final String GET_ALL_DEVICES = "select " + DeviceContract.DeviceEntry.DEVICE_NAME + ", " + DeviceContract.DeviceEntry.HOME_NAME + ", " + DeviceContract.DeviceEntry.ROOM_NAME + ", " + DeviceContract.DeviceEntry.THING_NAME + " from " + DeviceContract.DeviceEntry.TABLE_NAME;
    public static final String GET_ALL_HOME = "select distinct " + DeviceContract.DeviceEntry.HOME_NAME + " from " + DeviceContract.DeviceEntry.TABLE_NAME;
    public static final String INSERT_INITIAL_DATA = "select * from " + DeviceContract.DeviceEntry.TABLE_NAME;
    public static final String GET_ROOMS = "select distinct " + DeviceContract.DeviceEntry.ROOM_NAME + " from " + DeviceContract.DeviceEntry.TABLE_NAME + " where " + DeviceContract.DeviceEntry.HOME_NAME + " = ?";
    public static final String INSERT_ROOMS = "select " + DeviceContract.DeviceEntry.HOME_NAME + " from " + DeviceContract.DeviceEntry.TABLE_NAME + " where " + DeviceContract.DeviceEntry.ROOM_NAME + " = ?";
    public static final String CRUD_ROOM = DeviceContract.DeviceEntry.HOME_NAME + " =? and " + DeviceContract.DeviceEntry.ROOM_NAME + " =? ";
    public static final String GET_DEVICES_IN_ROOM = "select " + DeviceContract.DeviceEntry.DEVICE_NAME + " from " + DeviceContract.DeviceEntry.TABLE_NAME + " where " + DeviceContract.DeviceEntry.HOME_NAME + " = ? and " + DeviceContract.DeviceEntry.ROOM_NAME + " =?";
    public static final String GET_LOADS_JSON = "select " + DeviceContract.DeviceEntry.LOAD + " from " + DeviceContract.DeviceEntry.TABLE_NAME + " where " + DeviceContract.DeviceEntry.DEVICE_NAME + " = ?";
    public static final String INSERT_DEVICES = "select " + DeviceContract.DeviceEntry.DEVICE_NAME + " from " + DeviceContract.DeviceEntry.TABLE_NAME;
    public static final String CHECK_DEVICES = "select " + DeviceContract.DeviceEntry.DEVICE_NAME + " from " + DeviceContract.DeviceEntry.TABLE_NAME + " where " + DeviceContract.DeviceEntry.DEVICE_NAME + " = ?";
    public static final String GET_THING_NAME = "select " + DeviceContract.DeviceEntry.THING_NAME + " from " + DeviceContract.DeviceEntry.TABLE_NAME;
    public static final String GET_DEVICES_FOR_THING = "select " + DeviceContract.DeviceEntry.DEVICE_NAME + " from " + DeviceContract.DeviceEntry.TABLE_NAME + " where " + DeviceContract.DeviceEntry.THING_NAME + " = ?";
    public static final String UPDATE_DEVICE = DeviceContract.DeviceEntry.DEVICE_NAME + "=?";
    public static final String UPDATE_THING_NAME = DeviceContract.DeviceEntry.DEVICE_NAME + "=?";
    public static final String GET_ROOM_FOR_DEVICE = "select " + DeviceContract.DeviceEntry.ROOM_NAME + " from " + DeviceContract.DeviceEntry.TABLE_NAME + " where " + DeviceContract.DeviceEntry.DEVICE_NAME + " =?";
    public static final String DELETE_DEVICE = DeviceContract.DeviceEntry.DEVICE_NAME + " = ?";
    public static final String DELETE_HOME = DeviceContract.DeviceEntry.HOME_NAME + " =?";
    public static final String GET_UIUD = "select " + DeviceContract.DeviceEntry.UIUD + " from " + DeviceContract.DeviceEntry.TABLE_NAME + " where " + DeviceContract.DeviceEntry.DEVICE_NAME + " = ?";
    public static final String UPDATE_UIUD = DeviceContract.DeviceEntry.DEVICE_NAME + " = ? ";

    //SQL - Lite Queries for Favourite DB
    public static final String GET_ALL_FAVOURITE = "select * from " + FavouriteContract.FavouriteEntry.TABLE_NAME + " where " + FavouriteContract.FavouriteEntry.HOME_NAME + " = ?";
    public static final String CRUD_FAVOURITE = FavouriteContract.FavouriteEntry.DEVICE_NAME + " = ? and " + FavouriteContract.FavouriteEntry.LOAD_NAME + " = ?";
    public static final String UPDATE_LOAD_NAME = FavouriteContract.FavouriteEntry.HOME_NAME + " =? and " + FavouriteContract.FavouriteEntry.ROOM_NAME + " =? and " + FavouriteContract.FavouriteEntry.DEVICE_NAME + " =? and " + FavouriteContract.FavouriteEntry.LOAD_NAME + " =?";

    //AWS MQTT Messages
    public static final String AWS_UPDATE_ACCEPTED = "$aws/things/%s/shadow/update/accepted";
    public static final String AWS_GET_ACCEPTED = "$aws/things/%s/shadow/get/accepted";
    public static final String AWS_UPDATE = "$aws/things/%s/shadow/update";
    public static final String AWS_GET = "$aws/things/%s/shadow/get";

    //Sharing
    public static final String EMAIL_SUBJECT = "Aura Guest access Invite for ";
    public static final String EMAIL_BODY = "You have been invited to share access of %s.\n" + "Use the following code and experience smart and convenient living.\n";

}
