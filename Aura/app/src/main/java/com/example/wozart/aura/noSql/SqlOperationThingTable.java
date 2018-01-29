package com.example.wozart.aura.noSql;

import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.wozart.amazonaws.models.nosql.ThingTableDO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wozart on 04/01/18.
 */

public class SqlOperationThingTable {

    private static final String LOG_TAG = SqlOperationThingTable.class.getSimpleName();
    private DynamoDBMapper dynamoDBMapper;

    public ThingTableDO searchAvailableDevices(){
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        ThingTableDO availableDevice;
        Map<String, AttributeValue> availableThings = new HashMap<>();
        availableThings.put(":val1", new AttributeValue().withN("1"));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("Available = :val1").withExpressionAttributeValues(availableThings);

        List<ThingTableDO> scanResult = dynamoDBMapper.scan(ThingTableDO.class, scanExpression);
        if(scanResult.isEmpty()) {
            Log.e(LOG_TAG, "No Devices available on AWS: " + scanResult);
            return null;
        } else {
            availableDevice = scanResult.get(0);
            Log.d(LOG_TAG, "Received Thing Name: " + availableDevice.getThing());
            return availableDevice;
        }
    }

    public void updateAvailability(String device){
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        ThingTableDO changeAvailability = dynamoDBMapper.load(ThingTableDO.class, device);
        changeAvailability.setAvailable(0.0);
        dynamoDBMapper.save(changeAvailability);
        Log.d(LOG_TAG, "Availability changed ");
    }

    public ThingTableDO thingDetails(String thing){
        ThingTableDO thingTableDO = new ThingTableDO();
        try{
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            thingTableDO = dynamoDBMapper.load(ThingTableDO.class, thing);
        } catch (Exception e){
            Log.e(LOG_TAG, "Error : " + e);
            return null;
        }
        return thingTableDO;
    }

}
