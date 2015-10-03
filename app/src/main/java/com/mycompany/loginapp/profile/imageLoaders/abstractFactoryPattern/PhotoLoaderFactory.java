package com.mycompany.loginapp.profile.imageLoaders.abstractFactoryPattern;

import com.mycompany.loginapp.profile.imageLoaders.CoverPhotoLoader;
import com.mycompany.loginapp.profile.imageLoaders.IPhotoLoader;
import com.mycompany.loginapp.profile.imageLoaders.ProfilePhotoLoader;

/**
 * Created by Alexander on 05-09-2015.
 * Factory pattern where both ProfilePhotoLoader & CoverPhotoLoader are implementing the same interface IPhotoLoader.
 * Loads a PhotoLoader depending on the parameter input.
 * By creating objects
 */
public class PhotoLoaderFactory extends PhotoLoaderFactoryCreator {

    private final static PhotoLoaderFactory mPhotoLoaderFactory = new PhotoLoaderFactory();

    public PhotoLoaderFactory(){
    }

    /** Gets the singleton instance of the PhotoLoaderFactory */
    public static PhotoLoaderFactory getPhotoLoaderFactory(){
        return mPhotoLoaderFactory;
    }

    /** The Factory method producing concrete types based on the interface IPhotoLoader */
    @Override
    protected IPhotoLoader createPhotoLoader(String photoLoaderType) {

        if(photoLoaderType == null){
            return null;
        }

        IPhotoLoader iPhotoLoader = null;

        if (photoLoaderType.equalsIgnoreCase("ProfilePhotoLoader")){
            iPhotoLoader = new ProfilePhotoLoader();
        }
        else if (photoLoaderType.equalsIgnoreCase("CoverPhotoLoader")){
            iPhotoLoader = new CoverPhotoLoader();
        }

        return iPhotoLoader;
    }
}
