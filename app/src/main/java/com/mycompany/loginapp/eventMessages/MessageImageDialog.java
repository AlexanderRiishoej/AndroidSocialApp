package com.mycompany.loginapp.eventMessages;

/**
 * Created by Alexander on 20-07-2015.
 * Generic type message class that handles the images posted by EventBus postSticky()
 */
public class MessageImageDialog<T>{
    public T value;

    public MessageImageDialog(T imageValue){
        this.value = imageValue;
    }
}
