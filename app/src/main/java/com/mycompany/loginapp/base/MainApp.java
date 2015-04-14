package com.mycompany.loginapp.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Created by Alexander on 14-02-2015.
 * The Class ChattApp is the Main Application class of this app. The onCreate
 * method of this class initializes the Parse.
 */
public final class MainApp extends Application {

    private static SharedPreferences sharedPreferences;
    private final static MainApp MAIN_APP_SINGLETON = new MainApp();
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "N0vbt1RSFJBDS5RXGnM0DLqzCCxydwFsUcSUDEV6", "lAXzmFMVSAiQVSuZspt1pcDzvb70S3LXqdtPSKki");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        context = this.getApplicationContext();
        //aQuery = new AQuery(this);
        //Log.d("Singleton", MySingleton.getMySingleton().getContext().toString());
        // Security reasons: With access for only current user
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

    public static SharedPreferences getDefaultSharedPreferences() {
        return sharedPreferences;
    }

    public static MainApp getSingleton() {

        return MAIN_APP_SINGLETON;
    }

    public static Context getAppContext() {
        return context;
    }
}
