package com.example.wozart.aura.utilities;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.wozart.aura.model.AuraSwitch;
import com.example.wozart.aura.model.AwsDataModel;
import com.example.wozart.aura.model.AwsState;
import com.google.gson.Gson;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by wozart on 29/12/17.
 */

public class JsonUtils {
    private static final String LOG_TAG = JsonUtils.class.getSimpleName();
    private static int ToggleLed = 0;

    public static AwsState DeserializeAwsData(String Data) {
        Gson gson = new Gson();
        AwsDataModel dataRD = new AwsDataModel();

        try {
            dataRD = gson.fromJson(Data, AwsDataModel.class);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error Parsing Json Data: " + e);
        }
        return dataRD.state.reported;
    }

    public String AwsRegionThing(String region, String thing) {
        String data = null;
        data = "{\"type\":7,\"thing\":\"" + thing + "\",\"region\":\"" + region + "\"}";
        return data;
    }

    public ArrayList<String> Certificates(String data) {
        String[] certificates = SegregateData.Segregate(data);
        String jsonCertificate;
        ArrayList<String> dataCertificates = new ArrayList<>();
        int length = 0;
        int pktNo = 0;
        for (String fragment : certificates) {
            jsonCertificate = "{\"type\":6,\"pos\":" + length + ",\"pktno\":" + pktNo + ",\"data\":\"" + fragment + "\"}";
            dataCertificates.add(jsonCertificate);
            length += fragment.length() + 1;
            pktNo++;
        }
        return dataCertificates;
    }

    public ArrayList<String> PrivateKeys(String data) {
        String[] privateKey = SegregateData.Segregate(data);
        String jsonPrivateKey;
        int length = 0;
        int pktNo = 0;
        ArrayList<String> dataPrivarteKey = new ArrayList<>();
        for (String fragment : privateKey) {
            jsonPrivateKey = "{\"type\":5,\"pos\":" + length + ",\"pktno\":" + pktNo + ",\"data\":\"" + fragment + "\"}";
            dataPrivarteKey.add(jsonPrivateKey);
            length += fragment.length() + 1;
            pktNo++;
        }
        return dataPrivarteKey;
    }

    public AuraSwitch Deserialize(String data) {
        Gson gson = new Gson();
        String trimedData = data.trim();
        AuraSwitch device = new AuraSwitch();
        try {
            device = gson.fromJson(trimedData, AuraSwitch.class);
        } catch (Exception e) {
            Log.e("JSON: ", "Illegal msg" + e);
        } finally {
            return device;
        }
    }

    public String Serialize(AuraSwitch device, String uiud) throws UnknownHostException {
        String name = device.getName();
        int[] states = device.getStates();
        int[] dims = device.getDims();

        String data = "{\"type\":4, \"ip\":\"" + convertIP() + "\",\"name\":\"" + name + "\",\"uiud\":\""+ uiud +"\",\"state\":[" + states[0] + "," + states[1] + "," + states[2] + "," + states[3] + "],\"dimm\":["
                + dims[0] + "," + dims[1] + "," + dims[2] + "," + dims[3] + "]}";
        return data;
    }

    public static String SerializeDataToAws() {
        String data = null;
        if (ToggleLed == 1) {
            data = "{\"state\":{\"desired\": {\"led\": " + ToggleLed + ", \"dimm\": [100, 100, 100, 100],\"state\": [1, 1, 1, 1]}}}";
            ToggleLed = 0;
        } else {
            data = "{\"state\":{\"desired\": {\"led\": " + ToggleLed + ", \"dimm\": [100, 100, 100, 100],\"state\": [0, 0, 0, 0]}}}";
            ToggleLed = 1;
        }
        return data;
    }

    public static String SerializeDataToAws(AuraSwitch device) {
        String data;
        int[] states = device.getStates();
        int led = device.getLed();
        if (led == 1) led = 0;
        else led = 1;
        data = "{\"state\":{\"desired\": {\"led\": " + led + ", \"dimm\": [100, 100, 100, 100],\"state\": [" + states[0] + ", " + states[1] + ", " + states[2] + "," +
                states[3] + "]}}}";
        return data;
    }

    public String InitialData(String ipInString) throws UnknownHostException {
        String ip = IpConvert(ipInString);

        String data = "{\"type\":1,\"ip\":\"" + ip + "\",\"time\":" + (System.currentTimeMillis() / 1000) + " }";
        return data;
    }

    public AuraSwitch DeserializeTcp(String data) {
        Gson gson = new Gson();
        AuraSwitch device = new AuraSwitch();
        try {
            device = gson.fromJson(data, AuraSwitch.class);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Illegal Message " + e);
        } finally {
            return device;
        }
    }

    public static String PairingData(String uiud, String pin) {
        String data = "{\"type\":2,\"hash\":\"" + pin + "\",\"uiud\":\"" + uiud + "\"}";
        return data;
    }

    public static String PairedData(String IP) throws UnknownHostException {
        String ip = IpConvert(IP);
        String data = "{\"type\":3,\"ip\":\"" + ip + "\"}";
        return data;
    }

    private static String convertIP() throws UnknownHostException {
        WifiManager mWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifi.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String IP = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        IP = IpConvert(IP);
        return IP;
    }

    private static String IpConvert(String ipInString) throws UnknownHostException {
        InetAddress ip = InetAddress.getByName(ipInString);
        String var = "";
        byte[] bytes = ip.getAddress();
        for (byte b : bytes) {
            var = var + String.format("%03d.", (b & 0xFF));
        }
        var = var.substring(0, var.length() - 1);
        return var;
    }
}
