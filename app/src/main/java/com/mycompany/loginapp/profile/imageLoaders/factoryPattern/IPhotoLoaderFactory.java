package com.mycompany.loginapp.profile.imageLoaders.factoryPattern;

import com.mycompany.loginapp.profile.imageLoaders.IPhotoLoader;

/**
 * Created by Alexander on 09-09-2015.
 */
public interface IPhotoLoaderFactory {

    IPhotoLoader getPhotoLoader(String type);
}
