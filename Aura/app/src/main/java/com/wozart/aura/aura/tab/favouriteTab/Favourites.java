package com.wozart.aura.aura.tab.favouriteTab;

/**
 * Created by wozart on 29/12/17.
 */

public class Favourites {
    private String name;
    private String home;
    private String device;
    private String room;
    private int thumbnail;

    public Favourites() {
    }

    public Favourites(String name, String home, String device, String room, int thumbnail) {
        this.device = device;
        this.room = room;
        this.name = name;
        this.home = home;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
