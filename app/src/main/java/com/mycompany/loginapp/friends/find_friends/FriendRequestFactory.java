package com.mycompany.loginapp.friends.find_friends;

import android.app.Activity;

import com.mycompany.loginapp.friends.IFriendsRequest;

/**
 * Created by Alexander on 26-09-2015.
 */
public class FriendRequestFactory {

    private final static FriendRequestFactory mFriendRequestFactory = new FriendRequestFactory();

    public FriendRequestFactory() {
    }

    public static FriendRequestFactory getFriendRequestFactory(){
        return mFriendRequestFactory;
    }

    public IFriendsRequest getFriendsRequestClass(Activity mActivityContext){
        return new FriendRequestClass(mActivityContext);
    }
}
