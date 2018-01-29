package com.example.wozart.aura.noSql;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.wozart.amazonaws.models.nosql.DevicesTableDO;
import com.example.wozart.aura.utilities.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wozart on 02/01/18.
 */

public class SqlOperationDeviceTable {
    private DynamoDBMapper dynamoDBMapper;

    public ArrayList<DevicesTableDO> userDevices(final List<String> devices) {
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        final ArrayList<DevicesTableDO> userDevice = new ArrayList<>();

        if(devices == null) return null;
        for (String x : devices) {
            userDevice.add(dynamoDBMapper.load(DevicesTableDO.class, x));
        }
        return userDevice;
    }

    public boolean newUserDevice(String device, String room){
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        List<String> loads = new ArrayList<>();
        loads.add("LOAD_1");
        loads.add("LOAD_2");
        loads.add("LOAD_3");
        loads.add("LOAD_4");

        DevicesTableDO updateDevices = new DevicesTableDO();
        Map<String, String> updatedName = new HashMap<String, String>();
        updatedName.put(Constant.IDENTITY_ID, Constant.USERNAME);
        updateDevices.setMaster(updatedName);
        updateDevices.setDeviceId(device);
        updateDevices.setSlave(null);
        updateDevices.setLoads(loads);
        updateDevices.setRoom(room);
        dynamoDBMapper.save(updateDevices);
        return true;
    }

    public boolean deleteDevice(String device){
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        DevicesTableDO devicesTableDO = new DevicesTableDO();
        devicesTableDO.setDeviceId(device);
        dynamoDBMapper.delete(devicesTableDO);
        return true;
    }

    public String getThingForDevice(String device){
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        DevicesTableDO deviceThing = dynamoDBMapper.load(DevicesTableDO.class, device);
        return deviceThing.getThing();
    }
}
