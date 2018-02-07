package com.example.wozart.aura.noSql;

import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.wozart.amazonaws.models.nosql.DevicesTableDO;
import com.example.wozart.amazonaws.models.nosql.UserTableDO;
import com.example.wozart.aura.utilities.Constant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public void insertUser(final String userId) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            final UserTableDO userTableDO = new UserTableDO();
            userTableDO.setUserId(userId);

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

    public boolean updateUserDevices(String device) {
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            if(isDeviceAlreadyPresent(device)) return false;

            UserTableDO updateDevice = dynamoDBMapper.load(UserTableDO.class, Constant.IDENTITY_ID);
            List<String> devices = new ArrayList<>();
            if (updateDevice.getDevices() != null)
                devices = updateDevice.getDevices();
            devices.add(device);
            updateDevice.setDevices(devices);
            dynamoDBMapper.save(updateDevice);
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error: " + e);
            return false;
        }
    }

    private boolean isDeviceAlreadyPresent(String device){
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            UserTableDO updateDevice = dynamoDBMapper.load(UserTableDO.class, Constant.IDENTITY_ID);
            for(String x : updateDevice.getDevices()){
                if(x.equals(device)){
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
}
