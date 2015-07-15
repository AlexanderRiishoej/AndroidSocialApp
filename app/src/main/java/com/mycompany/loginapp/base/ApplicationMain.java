package com.mycompany.loginapp.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Alexander on 14-02-2015.
 * The Class ChattApp is the Main Application class of this app. The onCreate
 * method of this class initializes the Parse.
 */
public final class ApplicationMain extends Application {

    private static SharedPreferences sharedPreferences;
    private final static ApplicationMain MAIN_APP_SINGLETON = new ApplicationMain();
    public static Context context;
    private final String applicationId = "N0vbt1RSFJBDS5RXGnM0DLqzCCxydwFsUcSUDEV6";
    private final String clientId = "lAXzmFMVSAiQVSuZspt1pcDzvb70S3LXqdtPSKki";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, applicationId, clientId);
        FacebookSdk.sdkInitialize(this);
        ParseFacebookUtils.initialize(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        context = this.getApplicationContext();
        //aQuery = new AQuery(this);
        // Security reasons: With access for only current user
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

    public static SharedPreferences getDefaultSharedPreferences() {
        return sharedPreferences;
    }

    public static ApplicationMain getSingleton() {

        return MAIN_APP_SINGLETON;
    }

    public static Context getAppContext() {
        return context;
    }

    //http://blog.grafixartist.com/facebook-login-with-parse-part-1/
    public void generateFacebookKeyHash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.mycompany.loginapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }
}
