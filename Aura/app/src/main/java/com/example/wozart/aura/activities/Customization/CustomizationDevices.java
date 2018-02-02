package com.example.wozart.aura.activities.customization;

/***************************************************************************
 * File Name : CustomizationDevices
 * Author : Aarth Tandel
 * Date of Creation : 29/12/17
 * Description : Data model for CustomizationActivity
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class CustomizationDevices {
    private String home;
    private String room;
    private String device;
    private String thing;
    private int aws;
    private int position;
    private int online;

    public CustomizationDevices(String home, String room, String device, String thing, int aws, int online){
        this.home = home;
        this.room = room;
        this.device = device;
        this.thing = thing;
        this.aws = aws;
        this.online = online;
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

    public int getAws() {return this.aws;}

    public void setAws(int aws) {this.aws = aws; }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getOnline() {
        return online;
    }
}
