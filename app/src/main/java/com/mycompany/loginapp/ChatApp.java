package com.mycompany.loginapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Created by Alexander on 14-02-2015.
 * The Class ChattApp is the Main Application class of this app. The onCreate
 * method of this class initializes the Parse.
 */
public class ChatApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "N0vbt1RSFJBDS5RXGnM0DLqzCCxydwFsUcSUDEV6", "lAXzmFMVSAiQVSuZspt1pcDzvb70S3LXqdtPSKki");
        // Security reasons: With access for only current user
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
