package com.mycompany.loginapp.profile.imageLoaders.factoryPattern;

/**
 * Created by Alexander on 09-09-2015.
 * A Factory that creates another Factory of type IPhotoLoaderFactory
 */
public class PhotoLoaderFactoryCreatorV2 {

    public final static PhotoLoaderFactoryCreatorV2 M_PHOTO_LOADER_FACTORY_CREATOR_V_2 = new PhotoLoaderFactoryCreatorV2();

    public static PhotoLoaderFactoryCreatorV2 getPhotoLoaderFactoryCreator(){
        return M_PHOTO_LOADER_FACTORY_CREATOR_V_2;
    }

    /** Generates objects of concrete types */
    public IPhotoLoaderFactory getPhotoLoaderFactory(String photoLoaderFactoryType){
        if(photoLoaderFactoryType == null){
            return null;
        }

        if (photoLoaderFactoryType.equalsIgnoreCase("FirstPhotoFactoryCreator")){
            return PhotoLoaderFactoryV2.getPhotoLoaderFactory();
        }
        else if (photoLoaderFactoryType.equalsIgnoreCase("SecondPhotoFactoryCreator")){
            return PhotoLoaderFactoryV2.getPhotoLoaderFactory();
        }

        return null;
    }
}
