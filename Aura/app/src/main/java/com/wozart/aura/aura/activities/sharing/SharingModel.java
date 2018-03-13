package com.wozart.aura.aura.activities.sharing;

/**
 * Created by wozart on 15/02/18.
 */

public class SharingModel {

    private String name;
    private int numberOfDevices;
    private int thumbnail;
    private String master;
    private String deviceId;

    public SharingModel(String name, int numberOfDevices, int thumbnail) {
        this.name = name;
        this.numberOfDevices = numberOfDevices;
        this.thumbnail = thumbnail;
    }

    public SharingModel() {

    }

    public String getName() {
        return this.name;
    }

    public String getMaster() {
        return this.master;
    }

    public int getNumberOfDevices() {
        return this.numberOfDevices;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId.substring(0, deviceId.length() - 5);
    }
}
