package com.example.wozart.aura.noSql;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.wozart.amazonaws.models.nosql.DevicesTableDO;
import com.example.wozart.aura.sqlLite.device.DeviceDbHelper;
import com.example.wozart.aura.sqlLite.device.DeviceDbOperation;
import com.example.wozart.aura.utilities.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***************************************************************************
 * File Name : SqlOperationDeviceTable
 * Author : Aarth Tandel
 * Date of Creation : 02/01/18
 * Description : Sql operation for DeviceTable DynamoDB
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class SqlOperationDeviceTable {

    private static final String LOG_TAG = SqlOperationThingTable.class.getSimpleName();

    private DynamoDBMapper dynamoDBMapper;

    public ArrayList<DevicesTableDO> userDevices(final List<String> devices) {
        final ArrayList<DevicesTableDO> userDevice = new ArrayList<>();
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            if (devices == null) return null;
            for (String x : devices) {
                userDevice.add(dynamoDBMapper.load(DevicesTableDO.class, x));
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
            return null;
        }
        return userDevice;
    }

    public boolean newUserDevice(String deviceId, String uiud) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            String thing = null;
            if (isDeviceAlreadyRegisteredToTheUser(deviceId)) {
                thing = clearPreviousUserAndGetThing(deviceId);
            }
            List<String> loads = new ArrayList<>();
            loads.add("LOAD_1");
            loads.add("LOAD_2");
            loads.add("LOAD_3");
            loads.add("LOAD_4");

            String device = deviceId.substring(deviceId.length() - 6);

            DevicesTableDO updateDevices = new DevicesTableDO();
            Map<String, String> updatedName = new HashMap<String, String>();
            updatedName.put(Constant.IDENTITY_ID, Constant.USERNAME);
            updateDevices.setMaster(updatedName);
            updateDevices.setDeviceId(deviceId);
            updateDevices.setName(device);
            updateDevices.setUIUD(uiud);
            updateDevices.setSlave(null);
            updateDevices.setThing(thing);
            updateDevices.setLoads(loads);
            dynamoDBMapper.save(updateDevices);
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
            return false;
        }
    }

    private boolean isDeviceAlreadyRegisteredToTheUser(String device) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();
            DevicesTableDO checkDevice = new DevicesTableDO();
            if (device == null)
                return false;
            checkDevice = dynamoDBMapper.load(DevicesTableDO.class, device);
            if (checkDevice == null)
                return false;
            else {
                return true;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
            return false;
        }
    }

    private String clearPreviousUserAndGetThing(String device) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();
            DevicesTableDO checkDevice = new DevicesTableDO();
            if (device == null)
                return null;
            checkDevice = dynamoDBMapper.load(DevicesTableDO.class, device);
            if (checkDevice == null)
                return null;
            else {
                checkDevice.setMaster(null);
                checkDevice.setSlave(null);
                checkDevice.setLoads(null);
                checkDevice.setUIUD(null);
                dynamoDBMapper.save(checkDevice);
                return checkDevice.getThing();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
            return null;
        }
    }

    public boolean deleteDevice(String device) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            DevicesTableDO devicesTableDO = new DevicesTableDO();
            devicesTableDO.setDeviceId(device);
            dynamoDBMapper.delete(devicesTableDO);
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
            return false;
        }
    }

    public String getThingForDevice(String deviceId) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            DevicesTableDO deviceThing = dynamoDBMapper.load(DevicesTableDO.class, deviceId);
            return deviceThing.getThing();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
            return null;
        }
    }

    public String insertSlave(String deviceId) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            DevicesTableDO deviceThing = dynamoDBMapper.load(DevicesTableDO.class, deviceId);
            Map<String, String> slaves = deviceThing.getSlave();
            if (slaves != null) {
                for (String slave : slaves.keySet()) {
                    if (slave.equals(Constant.IDENTITY_ID)) {
                        return null;
                    }
                }
            }
            Map<String, String> master = deviceThing.getMaster();
            for (String x : master.keySet()) {
                if (x.equals(Constant.IDENTITY_ID)) {
                    return null;
                }
            }
            if (slaves != null) {
                slaves.put(Constant.IDENTITY_ID, Constant.USERNAME);
                deviceThing.setSlave(slaves);
                dynamoDBMapper.save(deviceThing);
            } else {
                Map<String, String> updatedName = new HashMap<>();
                updatedName.put(Constant.IDENTITY_ID, Constant.USERNAME);
                deviceThing.setSlave(updatedName);
                dynamoDBMapper.save(deviceThing);
            }
            return deviceThing.getThing();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
            return null;
        }
    }
}
