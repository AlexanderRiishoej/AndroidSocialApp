package com.mycompany.loginapp.base;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseRole;

/**
 * Created by Alexander on 14-02-2015.
 * The Class ChattApp is the Main Application class of this app. The onCreate
 * method of this class initializes the Parse.
 */
public class MainApp extends Application {

    private static SharedPreferences sharedPreferences;
    private static MainApp mainApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mainApp = new MainApp();
        Parse.initialize(this, "N0vbt1RSFJBDS5RXGnM0DLqzCCxydwFsUcSUDEV6", "lAXzmFMVSAiQVSuZspt1pcDzvb70S3LXqdtPSKki");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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
}
