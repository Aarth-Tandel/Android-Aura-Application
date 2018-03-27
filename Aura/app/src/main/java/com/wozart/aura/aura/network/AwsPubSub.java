package com.wozart.aura.aura.network;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.wozart.aura.aura.utilities.Constant;
import com.facebook.AccessToken;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

/***************************************************************************
 * File Name : AwsPubSub
 * Author : Aarth Tandel
 * Date of Creation : 29/12/17
 * Description : Connect, Post and GEt to AWS IOT
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class AwsPubSub extends Service {

    private static final String LOG_TAG = "AWS IoT PubSub";
    IBinder mBinder = new LocalAwsBinder();
    // --- Constants to modify per your configuration ---

    // Customer specific IoT endpoint
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "a15bui8ebaqvjn.iot.us-east-1.amazonaws.com";
    private static final String COGNITO_POOL_ID = "us-east-1:52da6706-7a78-41f4-950c-9d940b890788";

    // Region of AWS IoT
    private static final Regions MY_REGION = Regions.US_EAST_1;


    private AWSIotMqttManager mqttManager;
    private String clientId;

    private CognitoCachingCredentialsProvider credentialsProvider;

    public class LocalAwsBinder extends Binder {
        public AwsPubSub getServerInstance() {
            return AwsPubSub.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String id = prefs.getString("ID", "NO_USER");
        clientId = UUID.randomUUID().toString();

        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // context
                COGNITO_POOL_ID, // Identity Pool ID
                MY_REGION // Region
        );
        // Initialize the AWS Cognito credentials provider

//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
//        if (acct != null) {
//            String idToken = acct.getIdToken();
//            Map login = new HashMap();
//            login.put("accounts.google.com", idToken);
//            credentialsProvider.withLogins(login);
//        }

//         For Facebook
//        Map<String, String> logins = new HashMap<String, String>();
//        logins.put("graph.facebook.com", AccessToken.getCurrentAccessToken().getToken());
//        credentialsProvider.setLogins(logins);

//        clientId = id;
        Constant.IDENTITY_ID = id;
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);


        new Thread(new Runnable() {
            @Override
            public void run() {
                Connect();
            }
        }).start();
        return START_NOT_STICKY;
    }

    private void Connect() {
        Log.d(LOG_TAG, "clientId = " + clientId);

        try {
            mqttManager.connect(credentialsProvider, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                            final Throwable throwable) {
                    Log.d(LOG_TAG, "Status = " + String.valueOf(status));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (status == AWSIotMqttClientStatus.Connecting) {

                            } else if (status == AWSIotMqttClientStatus.Connected) {
                                sendDataToActivity("Connected");
                            } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                }
                            } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                    throwable.printStackTrace();
                                }
                            }
                        }
                    });
                }
            });
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Connection error: ", e);
        }
    }

    public void AwsSubscribe(String device) {
        final String topic = String.format(Constant.AWS_UPDATE_ACCEPTED, device);
        Log.d(LOG_TAG, "topic = " + topic);

        try {
            mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topic, final byte[] data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String message = new String(data, "UTF-8");
                                        Log.d(LOG_TAG, "Message arrived:");
                                        Log.d(LOG_TAG, "   Topic: " + topic);
                                        Log.d(LOG_TAG, " Message: " + message);
                                        String segments[] = topic.split("/");
                                        sendDataToActivity(message + "/" + segments[2]);
                                    } catch (UnsupportedEncodingException e) {
                                        Log.e(LOG_TAG, "Message encoding error.", e);
                                    }
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Subscription error.", e);
        }
    }

    public void AWSPublish(String device, String data) {

        final String topic = String.format(Constant.AWS_UPDATE, device);
        Log.d(LOG_TAG, "Data to Publish: " + data);
        try {
            mqttManager.publishString(data, topic, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }
    }

    public void AwsGet(String device) {
        final String topic = String.format(Constant.AWS_GET_ACCEPTED, device);
        Log.d(LOG_TAG, "topic = " + topic);

        try {
            mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topic, final byte[] data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String message = new String(data, "UTF-8");
                                        Log.d(LOG_TAG, "Message arrived:");
                                        Log.d(LOG_TAG, "   Topic: " + topic);
                                        Log.d(LOG_TAG, " Message: " + message);
                                        String segments[] = topic.split("/");
                                        sendDataToActivity(message + "/" + segments[2]);
                                    } catch (UnsupportedEncodingException e) {
                                        Log.e(LOG_TAG, "Message encoding error.", e);
                                    }
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Subscription error.", e);
        }
    }

    public void AwsGetPublish(String device) {
        final String topic = String.format(Constant.AWS_GET, device);
        final String msg = "";

        try {
            mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }
    }

    private void sendDataToActivity(String message) {
        Intent intent = new Intent("AwsShadow");
        intent.putExtra("data", message);
        LocalBroadcastManager.getInstance(AwsPubSub.this).sendBroadcast(intent);
    }
}
