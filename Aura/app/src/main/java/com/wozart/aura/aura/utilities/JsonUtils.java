package com.wozart.aura.aura.utilities;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.wozart.aura.aura.model.AuraSwitch;
import com.wozart.aura.aura.model.AwsDataModel;
import com.wozart.aura.aura.model.AwsState;
import com.google.gson.Gson;

import java.net.UnknownHostException;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by wozart on 29/12/17.
 */

public class JsonUtils {
    private static final String LOG_TAG = JsonUtils.class.getSimpleName();

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

    public String Serialize(AuraSwitch device, String uiud, int position) throws UnknownHostException {
        String name = device.getName();
        int[] states = device.getStates();
        int[] dims = device.getDims();
        int plc[] = new int[4];
        plc[position] = 1;

        String data = "{\"type\":4, \"ip\":" + convertIP() + ",\"name\":\"" + name + "\",\"uiud\":\""+ uiud +"\",\"state\":[" + states[0] + "," + states[1] + "," + states[2] + "," + states[3] + "],\"dim\":["
                + dims[0] + "," + dims[1] + "," + dims[2] + "," + dims[3] + "], \"plc\":[" + plc[0] + "," + plc[1] + "," + plc[2] + "," + plc[3] + "]}";
        return data;
    }

    public static String SerializeDataToAws(AuraSwitch device) {
        String data;
        int[] states = device.getStates();
        int led = device.getLed();
        if (led == 1) led = 0;
        else led = 1;
        data = "{\"state\":{\"desired\": {\"led\": " + led + ", \"dim\": [100, 100, 100, 100],\"state\": [" + states[0] + ", " + states[1] + ", " + states[2] + "," +
                states[3] + "]}}}";
        return data;
    }

    public String InitialData(String uiud) throws UnknownHostException {

        String data = "{\"type\":1,\"ip\":" + convertIP() + ",\"time\":" + (System.currentTimeMillis() / 1000) + ",\"uiud\":\""+ uiud +"\" }";
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

    private static int convertIP() throws UnknownHostException {
        WifiManager mWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifi.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return ipAddress;
    }

}
