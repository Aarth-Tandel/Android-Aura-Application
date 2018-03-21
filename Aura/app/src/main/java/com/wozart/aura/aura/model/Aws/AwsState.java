package com.wozart.aura.aura.model.Aws;

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
