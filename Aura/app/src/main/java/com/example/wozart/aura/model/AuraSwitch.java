package com.example.wozart.aura.model;

import com.example.wozart.aura.utilities.Constant;

/**
 * Created by wozart on 29/12/17.
 */

public class AuraSwitch {
    private int type = 1;
    private String name = "4module";
    private String thing;
    private int state[] = new int[]{0, 0, 0, 0};
    private int dimm[] = new int[]{100, 100, 100, 100};
    private double version = 0.0;
    private int nodes = 4;
    private String ip;
    private String code = Constant.UNPAIRED;
    private int aws = 1;
    private int error = 0;
    private int online = 0;
    private int led = 0;

    public AuraSwitch() {

    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getThing() {
        return thing;
    }

    public void setThing(String thing) {
        this.thing = thing;
    }

    public int[] getStates() {
        return state;
    }

    public int[] getDims() {
        return dimm;
    }

    public double getVersion() {
        return version;
    }

    public int getNodes() {
        return nodes;
    }

    public String getIP() {
        return ip;
    }

    public String getCode() {
        return code;
    }

    public int getAWSConfiguration() {
        return aws;
    }

    public int getError() {
        return error;
    }

    public int getOnline() {
        return online;
    }

    public void setStates(int[] state){this.state = state;}

    public void setDims(int[] dimm){this.dimm = dimm;}

    public void setName(String name) {
        this.name = name;
    }

    public void setDummyStates(int node) {
        for (int i = 0; i < 4; i++) {
            if (i == node) {
                if (this.state[i] == 0)
                    this.state[i] = 1;
                else
                    this.state[i] = 0;
            }
        }
    }

    public void setDummyDims(int node) {
        for (int i = 0; i < 4; i++) {
            if (i == node) {
                this.dimm[i] = 100;
            }
        }
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setAWSConfiguration(int aws) {
        this.aws = aws;
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getLed() {
        return led;
    }

    public void setLed(int led) {
        this.led = led;
    }
}
