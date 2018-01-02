package com.example.wozart.aura.noSql;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.wozart.amazonaws.models.nosql.DevicesTableDO;

import java.util.ArrayList;
import java.util.List;

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

        if(userDevice.isEmpty()) return null;
        for (String x : devices) {
            userDevice.add(dynamoDBMapper.load(DevicesTableDO.class, x));
        }
        return userDevice;
    }
}
