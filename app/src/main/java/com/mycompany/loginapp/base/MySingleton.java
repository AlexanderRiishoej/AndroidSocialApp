package com.mycompany.loginapp.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Alexander on 28-03-2015.
 */
public final class MySingleton {

    private final static MySingleton MY_SINGLETON = new MySingleton();
    private static SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainApp.context);

    private MySingleton(){
    }

    public static MySingleton getMySingleton(){
        return MY_SINGLETON;
    }

    public SharedPreferences getDefaultSharedPreferences() {
        return sharedPreferences;
    }
}
