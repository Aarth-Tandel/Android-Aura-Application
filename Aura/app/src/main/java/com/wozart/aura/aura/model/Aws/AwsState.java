package com.wozart.aura.aura.model.Aws;

import java.util.HashMap;

/***************************************************************************
 * File Name : AwsState
 * Author : Aarth Tandel
 * Date of Creation : 29/12/17
 * Description : State model for AWS IOT JSON
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class AwsState {
    private int led = 0;
    private HashMap< String,Integer> state = new HashMap<>();
    private HashMap< String,Integer> dim = new HashMap<>();
    private String user;
    private String uiud;


     public AwsState(){
        this.state.put("s0",0);
        this.state.put("s1",1);
        this.state.put("s2",0);
        this.state.put("s3",0);
        this.dim.put("d0",100);
        this.dim.put("d2",100);
        this.dim.put("d3",100);
        this.dim.put("d4",100);
    }

    public void setLed(int led) {
        this.led = led;
    }

    public void setState(int state) {
        String value = "s" + state;
        if(this.state.get(value) == 1) this.state.put(value,0);
        else this.state.put(value,1);
    }

    public void setDimm(int dimm) {
        String value = "d" + dim;
        if(this.dim.get(value) == 1) this.dim.put(value,100);
        else this.dim.put(value,1);
    }

    public int getLed(){return led;}

    public int[] getStates() {
        int states[] = new int [4];
        for(int x=0; x<states.length; x++){
            states[x] =  this.state.get("s" + x);
        }
        return states;
    }

    public int[] getDims() {
        int dims[] = new int [4];
        for(int x : dims){
            dims[x] =  this.dim.get("d" + x);
        }
        return dims;
    }
}
