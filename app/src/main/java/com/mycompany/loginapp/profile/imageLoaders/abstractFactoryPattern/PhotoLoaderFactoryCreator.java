package com.mycompany.loginapp.profile.imageLoaders.abstractFactoryPattern;

import android.app.Activity;

import com.mycompany.loginapp.profile.imageLoaders.IPhotoLoader;

/**
 * Created by Alexander on 09-09-2015.
 * An Abstract Factory creator.
 * This class creates different factories for instances of the interface IPhotoLoaders.
 * Subclasses implements the method createPhotoLoader() and decides which concrete type of class is created.
 */
public abstract class PhotoLoaderFactoryCreator {

    /** Gets the concrete type of the interface PhotoLoader from the Factory Method called from a subclass */
    public IPhotoLoader getPhotoLoaderInstance(String type, Activity activity){
        IPhotoLoader photoLoader = createPhotoLoader(type);
        photoLoader.getActivity(activity);
        photoLoader.loadImage();

        return photoLoader;
    }

    /** The Factory Method.
     * Derived by subclasses in which they create concrete classes
     */
    protected abstract IPhotoLoader createPhotoLoader(String type);
}
