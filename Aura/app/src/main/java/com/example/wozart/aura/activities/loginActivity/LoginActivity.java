package com.example.wozart.aura.activities.loginActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.example.wozart.aura.MainActivity;
import com.example.wozart.aura.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private PrefUtil prefUtil = new PrefUtil(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = loginResult.getAccessToken();
                        String user = loginResult.getAccessToken().getUserId();
                        setFacebookData(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void setFacebookData(final LoginResult loginResult) {
        Bundle params = new Bundle();
        params.putString("fields", "id,email,first_name,last_name,cover,picture.type(large)");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response != null) {
                             String email = null;
                            try {
                                JSONObject data = response.getJSONObject();
                                if(response.getJSONObject().has("email")) {
                                    email = response.getJSONObject().getString("email");
                                }
                                final String firstName = response.getJSONObject().getString("first_name");
                                final String lastName = response.getJSONObject().getString("last_name");
                                final String id = response.getJSONObject().getString("id");

                                SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                                prefEditor.putString("USERNAME", firstName + " " + lastName);
                                prefEditor.putString("EMAIL", email);
                                prefEditor.putString("ID", "us-east-1:" + id);
                                prefEditor.apply();

                                if (data.has("picture")) {
                                    String profilePicString = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                    new ConvertUrlToBitmap().execute(profilePicString);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).executeAsync();
    }

    private class ConvertUrlToBitmap extends AsyncTask<String, Long, Bitmap> {
        @Override
        public Bitmap doInBackground(String... urls) {
            Bitmap profilePic = null;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                profilePic = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return profilePic;
        }

        @Override
        protected void onPostExecute(final Bitmap profilePic) {
            super.onPostExecute(profilePic);
            if (profilePic != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                profilePic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                prefEditor.putString("PROFILE_PICTURE", encodedImage);
                prefEditor.apply();

                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(mainIntent);
                LoginActivity.this.finish();
            } else {
                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(mainIntent);
                LoginActivity.this.finish();
            }
        }
    }
}
