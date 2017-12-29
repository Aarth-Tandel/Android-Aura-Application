package com.example.wozart.aura.network;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wozart on 29/12/17.
 */

public class NsdClient {
    private Context mContext;

    private NsdManager mNsdManager;
    NsdManager.DiscoveryListener mDiscoveryListener;

    //To find all the available networks SERVICE_TYPE = "_services._dns-sd._udp"
    //Aura devices use _hap._tcp.
    public static final String SERVICE_TYPE = "_hap._tcp.";
    public static final String TAG = "NsdClient";
    private static String mServiceName = "Aura";

    private static List<NsdServiceInfo> ServicesAvailable = new ArrayList<>();

    public NsdClient(Context context) {
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void initializeNsd() {
        initializeDiscoveryListener();
    }

    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started " + regType);
                ServicesAvailable.clear();
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d(TAG, "Service discovery success " + service);

                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same Machine: " + mServiceName);
                } else if (service.getServiceName().contains(mServiceName)) {
                    Log.d(TAG, "Resolving Services: " + service);
                    mNsdManager.resolveService(service, new initializeResolveListener());
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "service lost" + service);
                for(NsdServiceInfo x : ServicesAvailable)
                    if (x.equals(service)) {
                        ServicesAvailable = null;
                    }
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public class initializeResolveListener implements NsdManager.ResolveListener {

        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.e(TAG, "Resolve failed " + errorCode);
            switch (errorCode) {
                case NsdManager.FAILURE_ALREADY_ACTIVE:
                    Log.e(TAG, "FAILURE ALREADY ACTIVE");
                    mNsdManager.resolveService(serviceInfo, new initializeResolveListener());
                    break;
                case NsdManager.FAILURE_INTERNAL_ERROR:
                    Log.e(TAG, "FAILURE_INTERNAL_ERROR");
                    break;
                case NsdManager.FAILURE_MAX_LIMIT:
                    Log.e(TAG, "FAILURE_MAX_LIMIT");
                    break;
            }
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.e(TAG, "Resolve Succeeded. " + serviceInfo);


            if (serviceInfo.getServiceName().equals(mServiceName)) {
                Log.d(TAG, "Same IP.");
                return;
            }

            if (ServicesAvailable.size() == 0)
                ServicesAvailable.add(serviceInfo);

            for (NsdServiceInfo x : ServicesAvailable) {
                if (!x.getServiceName().equals(serviceInfo.getServiceName())) {
                    ServicesAvailable.add(serviceInfo);
                }
            }
        }
    }

    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public List<NsdServiceInfo> GetAllServices() {
        return ServicesAvailable;
    }

    public void discoverServices() {
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public String GetIP(String device) {
        for (NsdServiceInfo x : ServicesAvailable) {
            if (x.getServiceName().contains(device)) {
                return x.getHost().getHostAddress();
            }
        }
        return null;
    }

    public List<NsdServiceInfo> GetServiceInfo() {
        return ServicesAvailable;
    }
}
