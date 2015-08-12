package com.mycompany.loginapp.helperClasses;

import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.constants.ParseConstants;

/**
 * Created by Alexander on 08-08-2015.
 * Profile helper class that has responsibility of various things such as setting online/offline mode of the current user
 */
public class ProfileHelperClass {

    public ProfileHelperClass(){

    }

    /** Sets user online */
    public static void setOnline(){
        ApplicationMain.mCurrentParseUser.put(ParseConstants.ONLINE, true);
        ApplicationMain.mCurrentParseUser.saveEventually();
    }

    /** Sets user offline */
    public static void setOffline(){
        ApplicationMain.mCurrentParseUser.put(ParseConstants.ONLINE, false);
        ApplicationMain.mCurrentParseUser.saveEventually();
    }
}
