package com.wozart.aura.aura.utilities;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.wozart.aura.aura.model.AuraSwitch.AuraSwitch;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.ECField;
import java.util.Random;

/**
 * Created by wozart on 29/12/17.
 */

public class Encryption {

    private static String value = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890.,';:[]{}!@#$%^&*()_=+\"/- ";
    private static int[] random = {82, 11, 31, 63, 52, 13, 74, 3, 42, 46,
            10, 64, 12, 9, 17, 15, 75, 16, 66, 55,
            25, 36, 57, 61, 50, 83, 72, 20, 33, 69,
            18, 77, 1, 14, 6, 0, 4, 65, 24, 37,
            39, 70, 59, 32, 51, 2, 35, 68, 67, 84,
            27, 78, 45, 48, 41, 87, 80, 60, 73, 40,
            53, 76, 81, 43, 38, 30, 23, 44, 19, 86,
            56, 58, 62, 7, 29, 47, 8, 28, 22, 5,
            71, 49, 85, 21, 34, 79, 54, 26};

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

    public static String enryptMessage(String data) {
        String encryptedData = "";
        for (int i = 0; i < data.length(); i++) {
            int index = value.indexOf(data.charAt(i));
            if (index == 87) {
                String wer = "rets";
            }
            int modIndex = random[index];
            char modLEtter = value.charAt(modIndex);
            encryptedData = encryptedData + modLEtter;
        }

        return encryptedData;
    }

    public static String denryptMessage(String data) {

        if (data == null) return null;
        String dencryptedData = "";
        for (int i = 0; i < data.length(); i++) {
            int index = value.indexOf(data.charAt(i));
            for (int j = 0; j < random.length; j++) {
                if (random[j] == index) {
                    dencryptedData = dencryptedData + value.charAt(j);
                }
            }
        }
        return dencryptedData;

    }
}
