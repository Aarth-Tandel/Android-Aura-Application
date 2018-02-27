package com.example.wozart.aura.model;

/**
 * Created by wozart on 20/02/18.
 */

public class SharedAccess {
    private String access = "false";
    private String home;
    private String userId;
    private String name;

    public SharedAccess(String access, String home, String userId, String name){
        this.access = access;
        this.home = home;
        this.userId = userId;
        this.name = name;
    }

    public String getAccess() {return this.access;}

    public void setAccess(String access) {this.access = access;}

    public String getHome() {return this.home;}

    public void setHome(String home) {this.home = home;}

    public String getUserId() {return this.userId;}

    public void setUserId(String userId) {this.userId = userId;}

    public String getName(){return this.name;}
}
