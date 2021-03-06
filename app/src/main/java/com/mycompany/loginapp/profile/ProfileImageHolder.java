package com.mycompany.loginapp.profile;

import java.io.File;

/**
 * Created by Alexander on 07-05-2015.
 * Holds a global reference to the profile_image image that all activities and fragments can use
 */
public class ProfileImageHolder {
    public static File mProfilePhotoFile;
    public static File mProfileCoverPhotoFile;

    public ProfileImageHolder(){

    }

    /** Sets the image files to null. Always do this when logging out. */
    public static void setImageFilesNull(){
        mProfilePhotoFile = null;
        mProfileCoverPhotoFile = null;
    }
}
