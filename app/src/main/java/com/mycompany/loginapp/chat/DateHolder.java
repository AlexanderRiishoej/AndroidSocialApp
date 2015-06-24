package com.mycompany.loginapp.chat;

import java.util.Date;

/**
 * Created by Alexander on 02-06-2015.
 * Generic class DateHolder.
 * Responsibility of holding on the date representing each chat in the list of chats.
 */
public class DateHolder <T>{
    private T lastUpdated;

    public DateHolder(T date){
        this.lastUpdated = date;
    }

    public T getLastUpdated(){
        return lastUpdated;
    }
}
