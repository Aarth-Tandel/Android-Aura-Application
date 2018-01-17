package com.example.wozart.aura.utilities;

import com.example.wozart.aura.model.AuraSwitch;
import com.example.wozart.aura.model.AwsState;

import java.util.ArrayList;

/**
 * Created by wozart on 29/12/17.
 */

public class DeviceUtils {
    private static ArrayList<AuraSwitch> AuraFourNodeDevice = new ArrayList<>();
    public static final String TAG = "DeviceUtils";

    public void RegisterDevice(AuraSwitch device, String ip) {
        Boolean flag = true;
        for(AuraSwitch x : AuraFourNodeDevice){
            if(x.getName().equals(device.getName()))
                flag = false;
        }
        if(flag) {
            AuraSwitch singleDevice = new AuraSwitch();
            singleDevice.setName(device.getName());
            singleDevice.setIP(ip);
            singleDevice.setType(device.getType());
            singleDevice.setCode(device.getCode());
            singleDevice.setOnline(1);
            singleDevice.setStates(device.getStates());
            singleDevice.setAWSConfiguration(device.getAWSConfiguration());
            AuraFourNodeDevice.add(singleDevice);
        }
    }

    public void CloudDevices(AwsState shadow, String thing, String device){
        Boolean flag = true;
        for(AuraSwitch x : AuraFourNodeDevice){
            if(x.getName().equals(device))
                flag = false;
        }
        if(flag) {
            AuraSwitch singleDevice = new AuraSwitch();
            singleDevice.setName(device);
            singleDevice.setOnline(1);
            singleDevice.setStates(shadow.getStates());
            singleDevice.setDims(shadow.getDims());
            singleDevice.setThing(thing);
            singleDevice.setAWSConfiguration(1);
            singleDevice.setLed(shadow.getLed());
            AuraFourNodeDevice.add(singleDevice);
        }
    }

    public int GetType(String deviceName) {
        int type = 0;
        for (AuraSwitch c : AuraFourNodeDevice) {
            if (deviceName.equals(c.getName())) {
                type = c.getType();
            }
        }
        return type;
    }

    public void UpdatePairingData(String deviceName, String code){
        for (AuraSwitch c : AuraFourNodeDevice) {
            if (c.getName().contains(deviceName)) {
                c.setCode(code);
            }
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

    public void UpdateSwitchStatesFromShadow(AwsState shadow, String thing, String device){
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

    public String GetCode(String deviceName){
        String code = null;
        for (AuraSwitch c : AuraFourNodeDevice) {
            if (deviceName.equals(c.getName())) {
                code = c.getCode();
            }
        }
        return code;
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

    public ArrayList<AuraSwitch> GetDevices(){
        return AuraFourNodeDevice;
    }
}
