package com.example.wozart.aura.noSql;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.wozart.amazonaws.models.nosql.DevicesTableDO;
import com.example.wozart.amazonaws.models.nosql.UserTableDO;
import com.example.wozart.aura.utilities.Constant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wozart on 28/12/17.
 */

public class SqlOperationUserTable {
    private DynamoDBMapper dynamoDBMapper;
    SqlOperationDeviceTable sqlOperationDeviceTable = new SqlOperationDeviceTable();

    public ArrayList<DevicesTableDO> getUserDevices(final String userId) {
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        UserTableDO userTableDO = dynamoDBMapper.load(UserTableDO.class, userId);
        ArrayList<DevicesTableDO> devices = sqlOperationDeviceTable.userDevices(userTableDO.getDevices());
        return devices;
    }

    public void insertUser(final String userId) {
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

    }

    public boolean isUserAlreadyRegistered(String id) {
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
    }

    public boolean updateUserDevices(String device){
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        UserTableDO updateDevice = dynamoDBMapper.load(UserTableDO.class, Constant.IDENTITY_ID);
        List<String> devices = updateDevice.getDevices();
        devices.add(device);
        updateDevice.setDevices(devices);
        dynamoDBMapper.save(updateDevice);
        return true;
    }

    public boolean deleteUserDevice(String device){
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        String id = Constant.IDENTITY_ID;
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
        return true;
    }
}
