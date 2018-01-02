package com.example.wozart.aura.noSql;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.wozart.amazonaws.models.nosql.DevicesTableDO;
import com.example.wozart.amazonaws.models.nosql.UserTableDO;

import java.util.ArrayList;

/**
 * Created by wozart on 28/12/17.
 */

public class SqlOperationUserTable {
    private DynamoDBMapper dynamoDBMapper;
    SqlOperationDeviceTable sqlOperationDeviceTable = new SqlOperationDeviceTable();

    public void getDevices(final String devices) {

        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                DevicesTableDO devicesTableDO = dynamoDBMapper.load(DevicesTableDO.class, devices);
            }
        }).start();
    }

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
}
