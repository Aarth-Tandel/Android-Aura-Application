package com.wozart.aura.aura.noSql;

import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.wozart.aura.amazonaws.models.nosql.ThingTableDO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***************************************************************************
 * File Name : SqlOperationThingTable
 * Author : Aarth Tandel
 * Date of Creation : 04/01/18
 * Description : Sql operation for ThingTable DynamoDB
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class SqlOperationThingTable {

    private static final String LOG_TAG = SqlOperationThingTable.class.getSimpleName();
    private DynamoDBMapper dynamoDBMapper;

    public ThingTableDO searchAvailableDevices() {

        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
        }

        ThingTableDO availableDevice;
        Map<String, AttributeValue> availableThings = new HashMap<>();
        availableThings.put(":val1", new AttributeValue().withN("1"));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("Available = :val1").withExpressionAttributeValues(availableThings);

        List<ThingTableDO> scanResult = dynamoDBMapper.scan(ThingTableDO.class, scanExpression);
        if (scanResult.isEmpty()) {
            Log.e(LOG_TAG, "No Devices available on AWS: " + scanResult);
            return null;
        } else {
            availableDevice = scanResult.get(0);
            Log.d(LOG_TAG, "Received Thing Name: " + availableDevice.getThing());
            return availableDevice;
        }
    }

    public void updateAvailability(final String device) {

        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            ThingTableDO changeAvailability = dynamoDBMapper.load(ThingTableDO.class, device);
            changeAvailability.setAvailable(0.0);
            dynamoDBMapper.save(changeAvailability);
            Log.d(LOG_TAG, "Availability changed ");


        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
        }


    }

    public ThingTableDO thingDetails(String thing) {
        ThingTableDO thingTableDO = new ThingTableDO();
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
            this.dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();

            thingTableDO = dynamoDBMapper.load(ThingTableDO.class, thing);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e);
            return null;
        }
        return thingTableDO;
    }

}
