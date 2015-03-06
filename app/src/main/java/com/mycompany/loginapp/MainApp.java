package com.mycompany.loginapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.androidquery.AQuery;
import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Created by Alexander on 14-02-2015.
 * The Class ChattApp is the Main Application class of this app. The onCreate
 * method of this class initializes the Parse.
 */
public class MainApp extends Application {

    private static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "N0vbt1RSFJBDS5RXGnM0DLqzCCxydwFsUcSUDEV6", "lAXzmFMVSAiQVSuZspt1pcDzvb70S3LXqdtPSKki");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //aQuery = new AQuery(this);

        // Security reasons: With access for only current user
//        ParseACL defaultACL = new ParseACL();
//        ParseACL.setDefaultACL(defaultACL, true);
    }

    public static SharedPreferences getDefaultSharedPreferences() {
        return sharedPreferences;
    }
}
