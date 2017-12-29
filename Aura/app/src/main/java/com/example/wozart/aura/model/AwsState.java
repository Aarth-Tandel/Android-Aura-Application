package com.example.wozart.aura.model;

/**
 * Created by wozart on 29/12/17.
 */

public class AwsState {
    private int led = 0;
    private int state[] = new int[]{0, 0, 0, 0};
    private int dimm[] = new int[]{100, 100, 100, 100};

    public void setLed(int led) {
        this.led = led;
    }

    public void setState(int state) {
        for (int i : this.state) {
            if (i == state) {
                if(this.state[i] == 1)
                    this.state[i] = 0;
                else
                    this.state[i] = 1;
            }
        }
    }

    public void setDimm(int dimm) {
        for (int i : this.dimm) {
            if (i == dimm)
                this.dimm[i] = dimm;
        }
    }

    public int getLed(){return led;}

    public int[] getStates() {
        return state;
    }

    public int[] getDims() {
        return dimm;
    }
}
