package com.mycompany.loginapp.eventMessages;

/**
 * Created by Alexander on 25-04-2015.
 */
public class MessageUpdateProfilePicture {
    public String imageUri;

    public MessageUpdateProfilePicture(String imageUri){
        this.imageUri = imageUri;
    }
}
