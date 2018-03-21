package com.wozart.aura.aura.utilities;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.wozart.aura.aura.model.AuraSwitch.AuraSwitch;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by wozart on 29/12/17.
 */

public class Encryption {
    public static String SHA256(String pin) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try {
            md.update(pin.getBytes("UTF-8")); // Change this to "UTF-16" if needed
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] digest = md.digest();
        return String.format("%064x", new java.math.BigInteger(1, digest));
    }

    public static String MAC(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return wInfo.getMacAddress();
    }

    public static String generateUIUD(AuraSwitch device) {
        Random rand = new Random();
        int uiudNumbers = rand.nextInt(100000);
        String uiudString = String.valueOf(uiudNumbers);
        if (uiudString.length() == 1) uiudString += "0000";
        if (uiudString.length() == 2) uiudString += "000";
        if (uiudString.length() == 3) uiudString += "00";
        if (uiudString.length() == 4) uiudString += "0";
        String uiud = device.getId() + uiudString;
        return uiud;
    }
}
