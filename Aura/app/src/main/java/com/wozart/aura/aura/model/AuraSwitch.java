package com.wozart.aura.aura.model;

import com.wozart.aura.aura.utilities.Constant;

/***************************************************************************
 * File Name : AuraSwitch
 * Author : Aarth Tandel
 * Date of Creation : 29/12/17
 * Description : Data model for Aura Switch mini
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * 31/01/18  Aarth Tandel - Set id for table
 * 09/02/18  Aarth Tandel - Mdl added
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * 31/01/18 Version 1.1
 * 09/02/18 Version 1.2
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class AuraSwitch {
    private int type = -1;
    private String name = "Error";
    private String thing;
    private int state[] = new int[]{0, 0, 0, 0};
    private int dim[] = new int[]{100, 100, 100, 100};
    private int mdl;
    private String ip;
    private String uiud = Constant.UNPAIRED;
    private int aws = 0;
    private int error;
    private int online = 0;
    private int led = 0;
    private String id = null;
    private int dsy = -1;
    private int t = -1;
    private int r = -1;
    private int k = -1;
    private int c = -1;
    private int p = -1;

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
        return dim;
    }

    public double getModel() {
        return mdl;
    }

    public String getIP() {
        return ip;
    }

    public String getUiud() {
        return uiud;
    }

    public int getAWSConfiguration() {
        return aws;
    }

    public int getError() {
        return error;
    }

    public int getThingFlag(){return this.t;}

    public int getCertificateFlag(){return this.c;}

    public int getKeysFlag(){return this.k;}

    public int getRegionFlag(){return this.r;}

    public int getPercentage(){return this.p;}

    public void setStates(int[] state){this.state = state;}


    public void setDims(int[] dimm){this.dim = dimm;}

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
                this.dim[i] = 100;
            }
        }
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setModel(int mdl) {
        this.mdl = mdl;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public void setUiud(String code) {
        this.uiud = code;
    }

    public void setAWSConfiguration(int aws) {
        this.aws = aws;
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

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public int getDiscovery(){return dsy;}
}
