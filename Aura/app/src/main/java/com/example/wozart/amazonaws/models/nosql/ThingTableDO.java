package com.example.wozart.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "aura-mobilehub-1808637480-ThingTable")

public class ThingTableDO {
    private String _thing;
    private Double _available;
    private String _certificate;
    private String _privateKey;
    private String _region;

    @DynamoDBHashKey(attributeName = "Thing")
    @DynamoDBAttribute(attributeName = "Thing")
    public String getThing() {
        return _thing;
    }

    public void setThing(final String _thing) {
        this._thing = _thing;
    }
    @DynamoDBAttribute(attributeName = "Available")
    public Double getAvailable() {
        return _available;
    }

    public void setAvailable(final Double _available) {
        this._available = _available;
    }
    @DynamoDBAttribute(attributeName = "Certificate")
    public String getCertificate() {
        return _certificate;
    }

    public void setCertificate(final String _certificate) {
        this._certificate = _certificate;
    }
    @DynamoDBAttribute(attributeName = "PrivateKey")
    public String getPrivateKey() {
        return _privateKey;
    }

    public void setPrivateKey(final String _privateKey) {
        this._privateKey = _privateKey;
    }
    @DynamoDBAttribute(attributeName = "Region")
    public String getRegion() {
        return _region;
    }

    public void setRegion(final String _region) {
        this._region = _region;
    }

}
