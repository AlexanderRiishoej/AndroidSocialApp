package com.mycompany.loginapp.eventMessages;

import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Alexander on 31-03-2015.
 */
public class MessageFindFriends {
    public final ParseUser mParseUserObject;

    public MessageFindFriends(ParseUser parseUserObject){
        this.mParseUserObject = parseUserObject;
    }
}
