package com.wozart.aura.aura.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.wozart.aura.aura.MainActivity;
import com.wozart.aura.R;
import com.wozart.aura.aura.activities.AndroidLoginActivity.GoogleLoginActivity;
import com.wozart.aura.aura.network.AwsPubSub;
import com.wozart.aura.aura.network.TcpServer;
import com.facebook.FacebookSdk;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/***************************************************************************
 * File Name : SplashScreen
 * Author : Aarth Tandel
 * Date of Creation : 29/12/17
 * Description : Loads all connection with AWS server, TCP Server and User Auth
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class SplashActivity extends AppCompatActivity {

    private static final String LOG_TAG = SplashActivity.class.getSimpleName();

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    public static PinpointManager pinpointManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FacebookSdk.sdkInitialize(getApplicationContext());

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.BLACK);

        startService(new Intent(this, TcpServer.class));
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isInternetWorking()) {
                    try {
                        AWSMobileClient.getInstance().initialize(SplashActivity.this).execute();
                        Thread.sleep(1000);
                        startService(new Intent(SplashActivity.this, AwsPubSub.class));
                        awsAnalytics();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLoggedIn()) {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, GoogleLoginActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    /**
     * AWS analytics function
     */

    public void awsAnalytics() {
        PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                getApplicationContext(),
                AWSMobileClient.getInstance().getCredentialsProvider(),
                AWSMobileClient.getInstance().getConfiguration());

        pinpointManager = new PinpointManager(pinpointConfig);

        // Start a session with Pinpoint
        pinpointManager.getSessionClient().startSession();

        // Stop the session and submit the default app started event
        pinpointManager.getSessionClient().stopSession();
        pinpointManager.getAnalyticsClient().submitEvents();
    }

    public boolean isLoggedIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //AccessToken accessToken = AccessToken.getCurrentAccessToken();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        final String userId = prefs.getString("ID", "NULL");
        boolean flag = true;
        if(userId.equals("NULL") && account == null ) flag = false;
        return flag;
    }

    public boolean isInternetWorking() {
        boolean success = false;
        try {
            URL url = new URL("https://google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            success = connection.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

}
