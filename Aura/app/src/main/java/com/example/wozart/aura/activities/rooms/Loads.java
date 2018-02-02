package com.example.wozart.aura.activities.rooms;

/***************************************************************************
 * File Name : Loads
 * Author : Aarth Tandel
 * Date of Creation : 02/01/18
 * Description : Data model for load cards and LoadAdapter
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * ____________________________________________________________________________
 *
 *****************************************************************************/

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
