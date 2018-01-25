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

@DynamoDBTable(tableName = "aura-mobilehub-1808637480-DevicesTable")

public class DevicesTableDO {
    private String _deviceId;
    private List<String> _loads;
    private Map<String, String> _master;
    private String _room;
    private Map<String, String> _slave;
    private String _thing;
    private String _uIUD;

    @DynamoDBHashKey(attributeName = "DeviceId")
    @DynamoDBAttribute(attributeName = "DeviceId")
    public String getDeviceId() {
        return _deviceId;
    }

    public void setDeviceId(final String _deviceId) {
        this._deviceId = _deviceId;
    }
    @DynamoDBAttribute(attributeName = "Loads")
    public List<String> getLoads() {
        return _loads;
    }

    public void setLoads(final List<String> _loads) {
        this._loads = _loads;
    }
    @DynamoDBAttribute(attributeName = "Master")
    public Map<String, String> getMaster() {
        return _master;
    }

    public void setMaster(final Map<String, String> _master) {
        this._master = _master;
    }
    @DynamoDBAttribute(attributeName = "Room")
    public String getRoom() {
        return _room;
    }

    public void setRoom(final String _room) {
        this._room = _room;
    }
    @DynamoDBAttribute(attributeName = "Slave")
    public Map<String, String> getSlave() {
        return _slave;
    }

    public void setSlave(final Map<String, String> _slave) {
        this._slave = _slave;
    }
    @DynamoDBAttribute(attributeName = "Thing")
    public String getThing() {
        return _thing;
    }

    public void setThing(final String _thing) {
        this._thing = _thing;
    }
    @DynamoDBAttribute(attributeName = "UIUD")
    public String getUIUD() {
        return _uIUD;
    }

    public void setUIUD(final String _uIUD) {
        this._uIUD = _uIUD;
    }

}
