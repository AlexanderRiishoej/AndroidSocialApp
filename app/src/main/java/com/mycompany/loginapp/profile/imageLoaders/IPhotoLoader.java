package com.mycompany.loginapp.profile.imageLoaders;

import android.app.Activity;

/**
 * Created by Alexander on 05-09-2015.
 * Interface for the ImageLoaders of profile picture and cover photo
 */
public interface IPhotoLoader {

    void saveImageToParse();

    void loadImage();

    void updateImageDirectory();

    void getActivity(Activity activity);
}
