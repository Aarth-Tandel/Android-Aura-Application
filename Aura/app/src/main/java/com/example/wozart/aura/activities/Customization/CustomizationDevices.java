package com.example.wozart.aura.activities.Customization;

/**
 * Created by wozart on 29/12/17.
 */

public class CustomizationDevices {
    private String home;
    private String room;
    private String device;
    private String thing;

    public CustomizationDevices(String home, String room, String device, String thing){
        this.home = home;
        this.room = room;
        this.device = device;
        this.thing = thing;
    }

    public CustomizationDevices(){}

    public String getHome() {return this.home;}

    public void setHome(String home) {this.home = home; }

    public String getRoom() {return this.room;}

    public void setRoom(String room) {this.room = room; }

    public String getDevice() {return this.device;}

    public void setDevice(String device) {this.device = device; }

    public String getThing() {return this.thing;}

    public void setThing(String thing) {this.thing = thing; }
}
