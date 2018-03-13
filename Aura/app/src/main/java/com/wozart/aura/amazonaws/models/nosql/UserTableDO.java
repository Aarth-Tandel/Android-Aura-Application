package com.wozart.aura.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "wozartaura-mobilehub-1863763842-UserTable")

public class UserTableDO {
    private String _userId;
    private List<String> _devices;
    private String _email;
    private String _name;
    private List<Map<String,String>> _sharedAccess;

    @DynamoDBHashKey(attributeName = "UserId")
    @DynamoDBAttribute(attributeName = "UserId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBAttribute(attributeName = "Devices")
    public List<String> getDevices() {
        return _devices;
    }

    public void setDevices(final List<String> _devices) {
        this._devices = _devices;
    }
    @DynamoDBAttribute(attributeName = "Email")
    public String getEmail() {
        return _email;
    }

    public void setEmail(final String _email) {
        this._email = _email;
    }
    @DynamoDBAttribute(attributeName = "Name")
    public String getName() {
        return _name;
    }

    public void setName(final String _name) {
        this._name = _name;
    }
    @DynamoDBAttribute(attributeName = "SharedAccess")
    public List<Map<String,String>> getSharedAccess() {
        return _sharedAccess;
    }

    public void setSharedAccess(final List<Map<String,String>> _sharedAccess) {
        this._sharedAccess = _sharedAccess;
    }

}
