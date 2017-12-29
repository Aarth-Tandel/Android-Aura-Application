package com.example.wozart.aura.noSql;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.wozart.amazonaws.models.nosql.DevicesTableDO;

/**
 * Created by wozart on 28/12/17.
 */

public class SqlOperationUserTable {
    private DynamoDBMapper dynamoDBMapper;

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
}
