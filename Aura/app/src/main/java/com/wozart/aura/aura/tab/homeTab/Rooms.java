package com.wozart.aura.aura.tab.homeTab;

/**
 * Created by wozart on 29/12/17.
 */

public class Rooms {
    private String name;
    private int numOfDevices;
    private int thumbnail;

    public Rooms() {
    }

    public Rooms(String name, int numOfSongs, int thumbnail) {
        this.name = name;
        this.numOfDevices = numOfSongs;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfDevices() {
        return numOfDevices;
    }

    public void setNumOfDevices(int numOfSongs) {
        this.numOfDevices = numOfSongs;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
