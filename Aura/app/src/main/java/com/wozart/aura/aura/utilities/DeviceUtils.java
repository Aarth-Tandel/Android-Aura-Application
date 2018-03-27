package com.wozart.aura.aura.utilities;

import com.wozart.aura.aura.model.AuraSwitch.AuraSwitch;
import com.wozart.aura.aura.model.Aws.AwsState;

import java.util.ArrayList;

/***************************************************************************
 * File Name : MainActivity
 * Author : Aarth Tandel
 * Date of Creation : 29/12/17
 * Description : Handles device and state management of different aura switches
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * 31/01/18  Aarth Tandel - Device id added for AWS and Security
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * 31/01/18 Version 1.1
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class DeviceUtils {
    private static ArrayList<AuraSwitch> AuraFourNodeDevice = new ArrayList<>();
    public static final String TAG = "DeviceUtils";

    public void RegisterDevice(AuraSwitch device, String ip, String uiud) {
        Boolean flag = true;
        for (AuraSwitch x : AuraFourNodeDevice) {
            if (x.getName().equals(device.getName())) {
                x.setIP(ip);
                x.setType(device.getType());
                x.setUiud(device.getUiud());
                x.setOnline(1);
                x.setId(device.getId());
                x.setStates(device.getStates());
                x.setAWSConfiguration(device.getAWSConfiguration());
                flag = false;
            }
        }
        if (flag) {
            AuraSwitch singleDevice = new AuraSwitch();
            singleDevice.setName(device.getName());
            singleDevice.setIP(ip);
            singleDevice.setType(device.getType());
            singleDevice.setUiud(device.getUiud());
            singleDevice.setOnline(1);
            singleDevice.setId(device.getId());
            singleDevice.setStates(device.getStates());
            singleDevice.setAWSConfiguration(device.getAWSConfiguration());
            AuraFourNodeDevice.add(singleDevice);
        }
    }

    public void CloudDevices(AwsState shadow, String thing, String device) {
        Boolean flag = true;
        for (AuraSwitch x : AuraFourNodeDevice) {
            if (x.getName().equals(device))
                flag = false;
        }
        if (flag) {
            AuraSwitch singleDevice = new AuraSwitch();
            singleDevice.setName(device);
            singleDevice.setOnline(1);
            if(shadow != null){
                singleDevice.setStates(shadow.getStates());
                singleDevice.setDims(shadow.getDims());
                singleDevice.setLed(shadow.getLed());
            }

            singleDevice.setThing(thing);
            singleDevice.setAWSConfiguration(1);
            AuraFourNodeDevice.add(singleDevice);
        }
    }


    public AuraSwitch UpdateSwitchState(String deviceName, int switchNumber) {
        AuraSwitch singleDevice = new AuraSwitch();
        for (AuraSwitch c : AuraFourNodeDevice) {
            if (deviceName.equals(c.getName())) {
                singleDevice.setStates(c.getStates());
                singleDevice.setDims(c.getDims());
                singleDevice.setDummyStates(switchNumber);
                singleDevice.setDummyDims(switchNumber);
                singleDevice.setName(c.getName());
                singleDevice.setThing(c.getThing());
                singleDevice.setLed(c.getLed());
                return singleDevice;
            }
        }
        return null;
    }

    public void UpdateSwitchStatesFromShadow(AwsState shadow, String thing, String device) {
        for (AuraSwitch c : AuraFourNodeDevice) {
            if (device.equals(c.getName())) {
                c.setStates(shadow.getStates());
                c.setDims(shadow.getDims());
                c.setLed(shadow.getLed());
            }
        }
    }

    public AuraSwitch GetInfo(String deviceName) {
        AuraSwitch SingleDevice = new AuraSwitch();
        for (AuraSwitch c : AuraFourNodeDevice) {
            if (deviceName.equals(c.getName())) {
                SingleDevice = c;
                return SingleDevice;
            }
        }
        return SingleDevice;
    }

    public void UpdateDevice(AuraSwitch device) {
        String name = device.getName();

        for (AuraSwitch c : AuraFourNodeDevice) {
            String deviceName = c.getName();
            if (deviceName.contains(name)) {
                c.setStates(device.getStates());
                c.setDims(device.getDims());
            }
        }
    }

    public AuraSwitch GetStatesDims(String deviceName) {
        AuraSwitch SingleDevice = new AuraSwitch();
        for (AuraSwitch c : AuraFourNodeDevice) {
            if (deviceName.equals(c.getName())) {

                String name = deviceName.substring(deviceName.lastIndexOf('-') + 1);
                SingleDevice.setName(name);
                SingleDevice.setStates(c.getStates());
                SingleDevice.setDims(c.getDims());
            }
        }
        return SingleDevice;
    }


    public String GetIP(String deviceName) {
        String ip = null;
        for (AuraSwitch c : AuraFourNodeDevice) {
            if (deviceName.equals(c.getName())) {
                ip = c.getIP();
            }
        }
        return ip;
    }

    public ArrayList<AuraSwitch> GetDevices() {
        return AuraFourNodeDevice;
    }
}
