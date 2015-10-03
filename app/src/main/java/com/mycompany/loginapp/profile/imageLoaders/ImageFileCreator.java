package com.mycompany.loginapp.profile.imageLoaders;

import android.os.Environment;
import android.util.Log;

import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.profile.ProfileImageHolder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alexander on 05-09-2015.
 * Creates the Image File that is needed to store an image when a Camera takes a picture
 */
public class ImageFileCreator {

    public static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!isExternalStorageAvailable()) {
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SocialChatApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("LoginApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = SimpleDateFormat.getDateTimeInstance().format(new Date()); //new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

//        if(requestType == Constants.REQUEST_PROFILE_IMAGE_CAPTURE) {
//                return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
//            }
//            else {
//                ProfileImageHolder.mProfileCoverPhotoFile = new File(mediaStorageDir.getPath() + File.separator +
//                        "IMG_" + timeStamp + ".jpg");
//                return ProfileImageHolder.mProfileCoverPhotoFile;
//            }
    }

    private static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}
