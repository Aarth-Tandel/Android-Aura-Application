package com.wozart.aura.aura.noSql;

import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.wozart.aura.amazonaws.models.nosql.DevicesTableDO;
import com.wozart.aura.aura.activities.sharing.SharingModel;
import com.wozart.aura.aura.utilities.Constant;

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

    public String newUserDevice(String deviceId, String uiud, String home, String room, ArrayList<String> load) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            String thing = null;

            DevicesTableDO checkDevice = new DevicesTableDO();
            if (deviceId == null) return null;

            checkDevice = dynamoDBMapper.load(DevicesTableDO.class, deviceId);
            if (checkDevice != null) {
                checkDevice.setMaster(null);
                checkDevice.setSlave(null);
                checkDevice.setLoads(null);
                checkDevice.setUIUD(null);
                dynamoDBMapper.save(checkDevice);
                thing = checkDevice.getThing();
            }
            String device = deviceId.substring(deviceId.length() - 6);


            List<String> loads = new ArrayList<>();
            loads.add(load.get(0));
            loads.add(load.get(1));
            loads.add(load.get(2));
            loads.add(load.get(3));


            DevicesTableDO updateDevices = new DevicesTableDO();
            updateDevices.setMaster(Constant.IDENTITY_ID);
            updateDevices.setDeviceId(deviceId);
            updateDevices.setName(device);
            updateDevices.setUIUD(uiud);
            updateDevices.setSlave(null);
            updateDevices.setThing(thing);
            updateDevices.setLoads(loads);
            updateDevices.setRoom(room);
            updateDevices.setHome(home);
            dynamoDBMapper.save(updateDevices);
            return thing;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
            return null;
        }
    }

//    private boolean isDeviceAlreadyRegisteredToTheUser(String device) {
//        try {
//            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
//            this.dynamoDBMapper = DynamoDBMapper.builder()
//                    .dynamoDBClient(dynamoDBClient)
//                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
//                    .build();
//            DevicesTableDO checkDevice = new DevicesTableDO();
//            if (device == null)
//                return false;
//            checkDevice = dynamoDBMapper.load(DevicesTableDO.class, device);
//            if (checkDevice == null)
//                return false;
//            else {
//                return true;
//            }
//        } catch (Exception e) {
//            Log.e(LOG_TAG, "Error : " + e);
//            return false;
//        }
//    }
//
//    private String clearPreviousUserAndGetThing(String device) {
//        try {
//            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
//            this.dynamoDBMapper = DynamoDBMapper.builder()
//                    .dynamoDBClient(dynamoDBClient)
//                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
//                    .build();
//            DevicesTableDO checkDevice = new DevicesTableDO();
//            if (device == null)
//                return null;
//            checkDevice = dynamoDBMapper.load(DevicesTableDO.class, device);
//            if (checkDevice == null)
//                return null;
//            else {
//                checkDevice.setMaster(null);
//                checkDevice.setSlave(null);
//                checkDevice.setLoads(null);
//                checkDevice.setUIUD(null);
//                dynamoDBMapper.save(checkDevice);
//                return checkDevice.getThing();
//            }
//        } catch (Exception e) {
//            Log.e(LOG_TAG, "Error : " + e);
//            return null;
//        }
//    }

    public void deleteDevice(String deviceId) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            DevicesTableDO devicesTableDO = dynamoDBMapper.load(DevicesTableDO.class, deviceId);
            devicesTableDO.setHome(null);
            devicesTableDO.setLoads(null);
            devicesTableDO.setMaster(null);
            devicesTableDO.setSlave(null);
            devicesTableDO.setUIUD(null);
            dynamoDBMapper.save(devicesTableDO);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
        }
    }

    public void updateThingForDevice(final String deviceId, final String thing) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            DevicesTableDO deviceThing = dynamoDBMapper.load(DevicesTableDO.class, deviceId);
            deviceThing.setThing(thing);
            dynamoDBMapper.save(deviceThing);


        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
        }
    }

    public void insertSlave(ArrayList<DevicesTableDO> devices) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            for (DevicesTableDO device : devices) {
                DevicesTableDO deviceThing = dynamoDBMapper.load(DevicesTableDO.class, device.getDeviceId());
                ArrayList<String> slaves = new ArrayList<>();
                if (deviceThing.getSlave() != null) {
                    for (String slave : deviceThing.getSlave()) {
                        if (slave.equals(Constant.IDENTITY_ID)) {
                            return;
                        }
                    }
                }
                String master = deviceThing.getMaster();
                if (master.equals(Constant.IDENTITY_ID)) {
                    return;
                }
                if (deviceThing.getSlave() != null) {
                    slaves.add(Constant.IDENTITY_ID);
                    deviceThing.setSlave(slaves);
                    dynamoDBMapper.save(deviceThing);
                } else {
                    ArrayList<String> slave = new ArrayList<>();
                    slave.add(Constant.IDENTITY_ID);
                    deviceThing.setSlave(slave);
                    dynamoDBMapper.save(deviceThing);
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
        }
    }

    public void removeSlaves(ArrayList<SharingModel> devices) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();
            if (devices != null) {
                for (SharingModel device : devices) {
                    DevicesTableDO sharedDevice = dynamoDBMapper.load(DevicesTableDO.class, device.getDeviceId());
                    ArrayList<Integer> indexes = new ArrayList<>();
                    if (sharedDevice.getSlave() != null) {
                        for (int i = 0; i < sharedDevice.getSlave().size(); i++) {
                            String slave = sharedDevice.getSlave().get(i);
                            if (sharedDevice.getSlave().get(i).equals(Constant.IDENTITY_ID)) {
                                indexes.add(i);
                            }
                        }
                        for (int x : indexes) sharedDevice.getSlave().remove(x);
                        dynamoDBMapper.save(sharedDevice);
                    }

                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
        }
    }
}
