package com.mycompany.loginapp.friends.find_friends;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.friends.IFriendsRequest;
import com.mycompany.loginapp.helperClasses.AlphabeticSortClass;
import com.mycompany.loginapp.helperClasses.SnackBarHelperClass;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Alexander on 26-09-2015.
 */
public class FriendRequestClass implements IFriendsRequest {
    final Activity mActivityContext;

    public FriendRequestClass(Activity mActivityContext) {
        this.mActivityContext = mActivityContext;
    }

    @Override
    public void sendFriendRequest(ParseUser parseUser) {
        ParseObject mFriendsRequest = new ParseObject("FriendRequests");
        mFriendsRequest.put("From", ApplicationMain.mCurrentParseUser);
        mFriendsRequest.put("To", parseUser);
        mFriendsRequest.put("to_id", parseUser.getObjectId());
        mFriendsRequest.put("from_id", ApplicationMain.mCurrentParseUser.getObjectId());
        mFriendsRequest.put("request_acc", false);
        mFriendsRequest.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivityContext, R.id.main_content), "Friends request sent", Snackbar.LENGTH_LONG);
                }
                else {
                    SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivityContext, R.id.main_content), "Error sending friends request", Snackbar.LENGTH_LONG);
                }
            }
        });
    }

    @Override
    public List<ParseUser> getFriendsRequests() {
        final List<ParseUser> mFriendRequests = Collections.emptyList();
        final ParseQuery<ParseObject> mParseObjectQuery = ParseQuery.getQuery("FriendRequests");
        mParseObjectQuery.whereMatches("to_id", ApplicationMain.mCurrentParseUser.getObjectId());
        mParseObjectQuery.include("From");
        mParseObjectQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i > list.size(); i++) {
                            mFriendRequests.add(list.get(i).getParseUser("From"));
                        }
                    }
                } else {
                    SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivityContext, R.id.main_content), "Error loading friend requests", Snackbar.LENGTH_LONG);
                }
            }
        });

        return mFriendRequests;
    }
}
