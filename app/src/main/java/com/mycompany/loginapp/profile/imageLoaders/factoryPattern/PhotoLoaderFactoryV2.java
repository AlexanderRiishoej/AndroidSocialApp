package com.mycompany.loginapp.profile.imageLoaders.factoryPattern;

import com.mycompany.loginapp.profile.imageLoaders.CoverPhotoLoader;
import com.mycompany.loginapp.profile.imageLoaders.IPhotoLoader;
import com.mycompany.loginapp.profile.imageLoaders.ProfilePhotoLoader;

/**
 * Created by Alexander on 05-09-2015.
 * Factory pattern where both ProfilePhotoLoader & CoverPhotoLoader are implementing the same interface IPhotoLoader.
 * Loads a PhotoLoader depending on the parameter input.
 */
public class PhotoLoaderFactoryV2 implements IPhotoLoaderFactory {

    private final static PhotoLoaderFactoryV2 mPhotoLoaderFactory = new PhotoLoaderFactoryV2();

    public PhotoLoaderFactoryV2(){
    }

    /** Gets the singleton instance of the PhotoLoaderFactory */
    public static PhotoLoaderFactoryV2 getPhotoLoaderFactory(){
        return mPhotoLoaderFactory;
    }

    /** Generates objects of concrete types */
    public IPhotoLoader getPhotoLoader(String photoLoaderType){
        if(photoLoaderType == null){
            return null;
        }

        if (photoLoaderType.equalsIgnoreCase("ProfilePhotoLoader")){
            return new ProfilePhotoLoader();
        }
        else if (photoLoaderType.equalsIgnoreCase("CoverPhotoLoader")){
            return new CoverPhotoLoader();
        }

        return null;
    }
}
