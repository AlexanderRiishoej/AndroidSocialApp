package com.mycompany.loginapp.singletons;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mycompany.loginapp.base.ApplicationMain;

/**
 * Created by Alexander on 28-03-2015.
 */
public final class MySingleton {

    private final static MySingleton MY_SINGLETON = new MySingleton();
    private static SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationMain.context);

    private MySingleton(){
    }

    public static MySingleton getMySingleton(){
        return MY_SINGLETON;
    }

    public SharedPreferences getDefaultSharedPreferences() {
        return sharedPreferences;
    }
}
