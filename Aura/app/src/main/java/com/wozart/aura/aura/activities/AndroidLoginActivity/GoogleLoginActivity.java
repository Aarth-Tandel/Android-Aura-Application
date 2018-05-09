package com.wozart.aura.aura.activities.AndroidLoginActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.wozart.aura.R;
import com.wozart.aura.aura.MainActivity;
import com.wozart.aura.aura.utilities.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
public class GoogleLoginActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String LOG_TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private Auth0 auth0;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

         //button listnere for login with email
        Button loginButton = (Button) findViewById(R.id.loginButton);
        findViewById(R.id.loginButton).setOnClickListener(this);


        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("719146577619-0b6307lbg7gukgmg2mjl4ti4gmpb6f0p.apps.googleusercontent.com")
                .requestId()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END build_client]

        // [START customize_button]
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        // [END customize_button]

        mAuth = FirebaseAuth.getInstance();

        auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        client = new OkHttpClient();
    }

    @Override
    public void onStart() {
        super.onStart();

        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        FirebaseUser account = mAuth.getCurrentUser();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        // [END on_start_sign_in]
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//                handleSignInResult(task);
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (Exception e) {
                // Google Sign In failed, update UI appropriately
                Log.w(LOG_TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    // [START handleSignInResult]
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            // Signed in successfully, show authenticated UI.
//            updateUI(account);
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w(LOG_TAG, "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
//        }
//    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    private void updateUI(FirebaseUser account) {
        if (account != null) {
            String personId = null;
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
            if (acct != null) {
                personId = acct.getId();
            }

            String personName = account.getDisplayName();
            String personEmail = account.getEmail();
            String personPhoto = account.getPhotoUrl().toString();

            SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(GoogleLoginActivity.this).edit();
            prefEditor.putString("USERNAME", personName);
            prefEditor.putString("EMAIL", personEmail);
            prefEditor.putString("ID", "us-east-1:" + personId);
            prefEditor.putString("PROFILE_PICTURE", personPhoto);
            prefEditor.apply();

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            Intent mainIntent = new Intent(GoogleLoginActivity.this, MainActivity.class);
            GoogleLoginActivity.this.startActivity(mainIntent);
            GoogleLoginActivity.this.finish();
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }
    private void updateUIIndipendent(String account) {
        Log.d("LOGIN","Login account: "+account);
        try{
            JSONObject user=new JSONObject(account);
            String personName = user.getString("nickname");
            String personEmail = user.getString("email");
            String personPhoto = user.getString("picture");
            String personId_string = user.getString("sub");
            int splitIndex = personId_string.indexOf("|");
            String personId  = personId_string.substring(splitIndex+1);


            SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(GoogleLoginActivity.this).edit();
            prefEditor.putString("USERNAME", personName);
            prefEditor.putString("EMAIL", personEmail);
            prefEditor.putString("ID", "us-east-1:" + personId);
            prefEditor.putString("PROFILE_PICTURE", personPhoto);
            prefEditor.apply();

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.loginButton).setVisibility(View.GONE);
            Intent mainIntent = new Intent(GoogleLoginActivity.this, MainActivity.class);
            GoogleLoginActivity.this.startActivity(mainIntent);
            GoogleLoginActivity.this.finish();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }


//    private void updateUI(@Nullable GoogleSignInAccount account) {
//        if (account != null) {
//            String personId = null;
//            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
//            if (acct != null) {
//                personId = acct.getId();
//            }
//
//            String personName = account.getDisplayName();
//            String personEmail = account.getEmail();
//            String personPhoto = account.getPhotoUrl().toString();
//
//            SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(GoogleLoginActivity.this).edit();
//            prefEditor.putString("USERNAME", personName);
//            prefEditor.putString("EMAIL", personEmail);
//            prefEditor.putString("ID", "us-east-1:" + personId);
//            prefEditor.putString("PROFILE_PICTURE", personPhoto);
//            prefEditor.apply();
//
//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            Intent mainIntent = new Intent(GoogleLoginActivity.this, MainActivity.class);
//            GoogleLoginActivity.this.startActivity(mainIntent);
//            GoogleLoginActivity.this.finish();
//        } else {
//            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//        }
//    }

    private void loginWithEmail() {
        WebAuthProvider.init(auth0)
                .withScheme("demo")
                .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
                .withScope("openid offline_access profile email")
                .start(GoogleLoginActivity.this, new AuthCallback() {
                    @Override
                    public void onFailure(@NonNull Dialog dialog) {
                        // Show error Dialog to user
                    }

                    @Override
                    public void onFailure(final AuthenticationException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("ERROR :", "Error " + exception);
                                Toast.makeText(GoogleLoginActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onSuccess(final Credentials credentials) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //token.setText("Logged in: " + credentials.getAccessToken());
                                getWebservice((credentials.getAccessToken()));

                            }
                        });
                    }
                });
    }
    private void getWebservice(String tokens) {
        final Request request = new Request.Builder().url("https://wozart.auth0.com/userinfo")
                .addHeader("authorization", "Bearer " + tokens)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //token.setText("Failure !");
                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Log.d("LOGIN","Login : "+response.body().string());
                            //JSONObject user=new JSONObject(response.body().string());
                            //Log.d("LOGIN","Login String : "+user.getString("email"));
                            updateUIIndipendent(response.body().string());
                        } catch (IOException ioe) {
                            //token.setText("Error during get body");
                            Log.d("LOGIN","LOgin : "+"Error during get body");
                        }
                    }
                });
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.loginButton:
                loginWithEmail();
               break;
//            case R.id.disconnect_button:
//                revokeAccess();
//                break;
        }
    }
}

