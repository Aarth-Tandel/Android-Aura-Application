package com.example.wozart.aura.activities.sharing;

/**
 * Created by wozart on 15/02/18.
 */

public class SharingModel {

    private String name;
    private int numberOfDevices;
    private int thumbnail;

    public SharingModel(String name, int numberOfDevices, int thumbnail){
        this.name = name;
        this.numberOfDevices = numberOfDevices;
        this.thumbnail = thumbnail;
    }

    public String getName(){return this.name;}

    public int getNumberOfDevices(){return this.numberOfDevices;}
}
