package com.example.wozart.aura.noSql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.wozart.amazonaws.models.nosql.DevicesTableDO;
import com.example.wozart.amazonaws.models.nosql.UserTableDO;
import com.example.wozart.aura.model.SharedAccess;
import com.example.wozart.aura.sqlLite.device.DeviceDbHelper;
import com.example.wozart.aura.sqlLite.device.DeviceDbOperation;
import com.example.wozart.aura.utilities.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/***************************************************************************
 * File Name : SqlOperationUserTable
 * Author : Aarth Tandel
 * Date of Creation : 28/12/17
 * Description : Sql operation for UserTable DynamoDB
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * ____________________________________________________________________________
 *
 *****************************************************************************/


public class SqlOperationUserTable {

    private static final String LOG_TAG = SqlOperationUserTable.class.getSimpleName();
    private DynamoDBMapper dynamoDBMapper;
    SqlOperationDeviceTable sqlOperationDeviceTable = new SqlOperationDeviceTable();


    public UserTableDO getUser() {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            UserTableDO userTableDO = dynamoDBMapper.load(UserTableDO.class, Constant.IDENTITY_ID);
            return userTableDO;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
            return null;
        }
    }

    public ArrayList<DevicesTableDO> getUserDevices(final String userId) {

        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            UserTableDO userTableDO = dynamoDBMapper.load(UserTableDO.class, userId);
            ArrayList<DevicesTableDO> devices = sqlOperationDeviceTable.userDevices(userTableDO.getDevices());
            return devices;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
            return null;
        }
    }

    public void insertUser(final String userId, final String userName, final String email) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            final UserTableDO userTableDO = new UserTableDO();
            userTableDO.setUserId(userId);

            if (userName != null) userTableDO.setName(userName);
            if (email != null) userTableDO.setEmail(email);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    dynamoDBMapper.save(userTableDO);
                }
            }).start();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
        }

    }

    public boolean isUserAlreadyRegistered(String id) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();
            UserTableDO checkUser = dynamoDBMapper.load(UserTableDO.class, id);
            if (checkUser == null)
                return false;
            else
                return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error: " + e);
            return false;
        }

    }

    public void updateUserDevices(String deviceId) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            if (isDeviceAlreadyPresent(deviceId)) return;

            UserTableDO updateDevice = dynamoDBMapper.load(UserTableDO.class, Constant.IDENTITY_ID);
            List<String> devices = new ArrayList<>();
            if (updateDevice.getDevices() != null)
                devices = updateDevice.getDevices();
            devices.add(deviceId);
            updateDevice.setDevices(devices);
            dynamoDBMapper.save(updateDevice);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error: " + e);
        }
    }

    private boolean isDeviceAlreadyPresent(String device) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            UserTableDO updateDevice = dynamoDBMapper.load(UserTableDO.class, Constant.IDENTITY_ID);
            if (updateDevice.getDevices().size() == 0) return false;
            for (String x : updateDevice.getDevices()) {
                if (x.equals(device)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error: " + e);
            return false;
        }
    }

    public void deleteUserDevice(String device) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            if (device == null) return;
            UserTableDO updateDevice = dynamoDBMapper.load(UserTableDO.class, Constant.IDENTITY_ID);
            List<String> devices = updateDevice.getDevices();
            for (Iterator<String> iter = devices.listIterator(); iter.hasNext(); ) {
                String a = iter.next();
                if (a.equals(device)) {
                    iter.remove();
                }
            }
            updateDevice.setDevices(devices);
            dynamoDBMapper.save(updateDevice);
            return;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error: " + e);
        }
    }

    public void shareDevices(String shareId, String home) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();
            UserTableDO sharedUser;
            Map<String, AttributeValue> availableThings = new HashMap<>();
            availableThings.put(":val1", new AttributeValue().withS(shareId));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("Email = :val1").withExpressionAttributeValues(availableThings);

            List<UserTableDO> scanResult = dynamoDBMapper.scan(UserTableDO.class, scanExpression);
            if (scanResult.isEmpty()) {
                Log.e(LOG_TAG, "No user with that id exists " + shareId);
            } else if (scanResult.get(0).getUserId().equals(Constant.IDENTITY_ID)) {
                Log.e(LOG_TAG, "Cannot share with yourself");
            } else {
                sharedUser = scanResult.get(0);
                updateSharedAccessToSharedUser(sharedUser, home);
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error: " + e);
        }
    }

    private void updateSharedAccessToSharedUser(UserTableDO sharedUser, String home) {

        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            SharedAccess sharedAccess = new SharedAccess("invite", home, sharedUser.getUserId(), Constant.USERNAME);
            Map<String, String> sharedData = new HashMap<>();
            sharedData.put("Access", sharedAccess.getAccess());
            sharedData.put("Home", sharedAccess.getHome());
            sharedData.put("UserID", Constant.IDENTITY_ID);
            sharedData.put("Name", sharedAccess.getName());

            if (sharedUser.getSharedAccess() != null) {
                List<Map<String, String>> sharedAccessForUser = sharedUser.getSharedAccess();
                sharedAccessForUser.add(sharedData);
                sharedUser.setSharedAccess(sharedAccessForUser);
            } else {
                List<Map<String, String>> sharedAccessForUser = new ArrayList<>();
                sharedAccessForUser.add(sharedData);
                sharedUser.setSharedAccess(sharedAccessForUser);
            }
            dynamoDBMapper.save(sharedUser);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
        }

    }

    public void transferSharedDevices(final Map<String, String> sharedData, final Context mContext) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();
            final String home = sharedData.get("Home");
            final String userId = sharedData.get("UserID");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<DevicesTableDO> deviceWithSpecifiedHome = new ArrayList<>();
                    ArrayList<DevicesTableDO> sharedDevices = getUserDevices(userId);
                    for (DevicesTableDO device : sharedDevices) {
                        if (device.getHome().equals(home))
                            deviceWithSpecifiedHome.add(device);
                    }
                    updateSharedDevices(deviceWithSpecifiedHome, sharedData, mContext);
                }
            }).start();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
        }
    }

    private void updateSharedDevices(ArrayList<DevicesTableDO> sharedDevices, Map<String, String> sharedData, Context mContext) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            DeviceDbOperation db = new DeviceDbOperation();
            SQLiteDatabase mDb;
            DeviceDbHelper dbHelper = new DeviceDbHelper(mContext);
            mDb = dbHelper.getWritableDatabase();
            SqlOperationDeviceTable sqlOperationDeviceTable = new SqlOperationDeviceTable();

            ArrayList<DevicesTableDO> checkForDuplicate = new ArrayList<>();
            for (DevicesTableDO device : sharedDevices) {
                if (!isDeviceAlreadyPresent(device.getDeviceId())) {
                    checkForDuplicate.add(device);
                }
            }

            UserTableDO updateDevice = dynamoDBMapper.load(UserTableDO.class, Constant.IDENTITY_ID);
            List<String> devices = new ArrayList<>();
            if (updateDevice.getDevices() != null)
                devices = updateDevice.getDevices();
            for (DevicesTableDO x : checkForDuplicate)
                devices.add(x.getDeviceId());
            updateDevice.setDevices(devices);
            dynamoDBMapper.save(updateDevice);

            updateAccess(sharedData);
            sqlOperationDeviceTable.insertSlave(checkForDuplicate);
            db.devicesFromAws(mDb, checkForDuplicate);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error: " + e);
        }
    }

    private void updateAccess(Map<String, String> sharedData) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();
            UserTableDO user = new UserTableDO();
            user = getUser();
            boolean home = false, userId = false, access = false;
            for (Map<String, String> shared : user.getSharedAccess()) {
                if (shared.get("Home").equals(sharedData.get("Home"))) home = true;
                if (shared.get("UserID").equals(sharedData.get("UserID"))) userId = true;
                if (shared.get("Access").equals(sharedData.get("Access"))) access = true;

                if (home && userId && access) {
                    shared.put("Access", "accepted");
                    dynamoDBMapper.save(user);
                }

            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
        }
    }
}
