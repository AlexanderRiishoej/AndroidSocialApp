package com.mycompany.loginapp.singletons;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.androidquery.AQuery;
import com.mycompany.loginapp.base.ApplicationMain;
import com.squareup.picasso.Picasso;

/**
 * Created by Alexander on 28-03-2015.
 */
public final class MySingleton {

    private final static MySingleton MY_SINGLETON = new MySingleton();
    private static SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationMain.context);
    private static Picasso picasso = Picasso.with(ApplicationMain.context);
    private static AQuery aQuery = new AQuery(ApplicationMain.context);

    private MySingleton(){
    }

    public static MySingleton getMySingleton(){
        return MY_SINGLETON;
    }

    public SharedPreferences getDefaultSharedPreferences() {
        return sharedPreferences;
    }

    /** Gets an instance of Picasso */
    public Picasso getPicasso(){ return picasso; }

    /** Gets an instance of AQuery */
    public AQuery getAQuery(){ return aQuery; }
}
