package com.example.wozart.aura.activities.rooms;

/**
 * Created by wozart on 02/01/18.
 */

public class Loads {
    private String name;
    private String device;
    private String IP;
    private int thumbnail;
    private int loadNumber;
    private int state = 0;
    private int position;

    public Loads() {
    }

    public Loads(String name, String device, String IP, int thumbnail, int loadNumber) {
        this.name = name;
        this.device = device;
        this.IP = IP;
        this.thumbnail = thumbnail;
        this.loadNumber = loadNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getLoadNumber() {
        return loadNumber;
    }

    public void setLoadNumber(int loadNumber) {
        this.loadNumber = loadNumber;
    }

    public String getNumOfDevices() {
        return IP;
    }

    public void setNumOfDevices(String numOfSongs) {
        this.IP = numOfSongs;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getIP() {
        return IP;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setPostion(int position) {
        this.position = position;
    }

    public int getPostion() {
        return position;
    }
}
