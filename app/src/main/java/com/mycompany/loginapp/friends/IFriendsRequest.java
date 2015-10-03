package com.mycompany.loginapp.friends;

import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Alexander on 26-09-2015.
 */
public interface IFriendsRequest {
    public void sendFriendRequest(ParseUser parseUser);

    public List<ParseUser> getFriendsRequests();
}
