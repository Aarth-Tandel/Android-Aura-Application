package com.example.wozart.aura.network;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.wozart.aura.utilities.Constant;
import com.example.wozart.aura.utilities.awsConfiguration.AWSConfigurationConstant;
import com.facebook.AccessToken;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import static com.example.wozart.aura.utilities.Constant.AWS_GET;
import static com.example.wozart.aura.utilities.Constant.AWS_GET_ACCEPTED;
import static com.example.wozart.aura.utilities.Constant.AWS_UPDATE;
import static com.example.wozart.aura.utilities.Constant.AWS_UPDATE_ACCEPTED;

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


    private AWSIotMqttManager mqttManager;
    private String clientId;

    private AWSCredentials awsCredentials;
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
        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // context
                AWSConfigurationConstant.AMAZON_COGNITO_IDENTITY_POOL_ID,// Identity Pool ID
                AWSConfigurationConstant.AMAZON_COGNITO_REGION // Region
        );

        Region region = Region.getRegion(AWSConfigurationConstant.AMAZON_COGNITO_REGION);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String id = prefs.getString("ID", "NO_USER");

        Map<String, String> logins = new HashMap<String, String>();
        logins.put("graph.facebook.com", AccessToken.getCurrentAccessToken().getToken());
        credentialsProvider.setLogins(logins);

        // MQTT Client
        clientId = id;
        Constant.IDENTITY_ID = clientId;
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);

        // The following block uses IAM user credentials for authentication with AWS IoT.
        //awsCredentials = new BasicAWSCredentials("ACCESS_KEY_CHANGE_ME", "SECRET_KEY_CHANGE_ME");
        //btnConnect.setEnabled(true);

        // The following block uses a Cognito credentials provider for authentication with AWS IoT.

        new Thread(new Runnable() {
            @Override
            public void run() {
                awsCredentials = credentialsProvider.getCredentials();
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
        final String topic = String.format(AWS_UPDATE_ACCEPTED, device) ;
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

        final String topic = String.format(AWS_UPDATE, device);
        Log.d(LOG_TAG, "Data to Publish: " + data);
        try {
            mqttManager.publishString(data, topic, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }
    }

    public void AwsGet(String device) {
        final String topic = String.format(AWS_GET_ACCEPTED,device);
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
        final String topic = String.format(AWS_GET,device);
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
