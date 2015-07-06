package com.mycompany.loginapp.eventMessages;

import com.parse.ParseObject;

/**
 * Created by Alexander on 31-03-2015.
 */
public class MessageUserChat {
    public final ParseObject mUserChatObject;

    public MessageUserChat(ParseObject userChatObject){
        this.mUserChatObject = userChatObject;
    }
}
