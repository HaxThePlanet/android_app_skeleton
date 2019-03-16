package com.example.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;

public class LoginScreen extends Activity {

    private CallbackManager callbackManager;
    private static final String LOG_TAG = "skelly_app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //must be called first
        initFacebook();

        setContentView(R.layout.activity_login);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, callback);
    }

    private void initFacebook() {
        FacebookSdk.sdkInitialize(this);

        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }

        callbackManager = CallbackManager.Factory.create();

        printHashKey();
    }

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.skeleton", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i(LOG_TAG, Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            Log.i(LOG_TAG, e.toString());
        }
    }

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();

            Log.i(LOG_TAG, "fb onSuccess = " + accessToken.getApplicationId() + " " + accessToken.getToken());

            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(intent);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
