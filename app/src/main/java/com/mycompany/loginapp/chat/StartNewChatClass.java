package com.mycompany.loginapp.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.eventMessages.MessageUserChat;
import com.mycompany.loginapp.helperClasses.AlphabeticSortClass;
import com.mycompany.loginapp.utilities.Utilities;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import de.greenrobot.event.EventBus;

/**
 * Created by Alexander on 29-07-2015.
 * This class either starts a chat or creates a new chat
 */
public class StartNewChatClass {
    final Context mActivityContext;

    public StartNewChatClass(Context mActivityContext){
        this.mActivityContext = mActivityContext;
    }

    /** Query Parse for a UserChatObject matching the selected user and the current logged in user.
     *  If the chat already exists it starts the activity Chat_act with the two users otherwise it creates a new chat */
    public void startUserChatQuery(final ParseUser mClickedUser) {
        final ParseQuery<ParseObject> mParseObjectQuery = ParseQuery.getQuery(ParseConstants.CHAT_USERS);
        mParseObjectQuery.whereMatches(ParseConstants.CHAT_ID, AlphabeticSortClass.addUniqueId(ApplicationMain.mCurrentParseUser.getObjectId(), mClickedUser.getObjectId()));
        mParseObjectQuery.include(ParseConstants.CREATED_BY);
        mParseObjectQuery.include(ParseConstants.USERNAME);

        mParseObjectQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject mChatUserParseObject, ParseException e) {
                if (e == null || mChatUserParseObject == null) {

                    if (mChatUserParseObject != null) {
                        EventBus.getDefault().postSticky(new MessageUserChat(mChatUserParseObject));
                        mActivityContext.startActivity(new Intent(mActivityContext, Chat_act.class));

                    } else {
                        createNewUserChat(mClickedUser);
                    }
                } else {
                    new MaterialDialog.Builder(mActivityContext)
                            .title("An error occurred")
                            .content(e.getMessage())
                            .show();
                }
            }
        });
    }

    /**
     * Creates a new chat with the current logged in user and the user clicked in the Adapter
     */
    private void createNewUserChat(final ParseUser mClickedUser) {
        final ParseObject newChat = new ParseObject(ParseConstants.CHAT);
        newChat.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    final ParseObject mNewChatUserPair = new ParseObject(ParseConstants.CHAT_USERS);
                    final ParseRelation<ParseObject> mChatRelation = mNewChatUserPair.getRelation(ParseConstants.CHAT_RELATION);
                    mNewChatUserPair.put(ParseConstants.CREATED_BY, ApplicationMain.mCurrentParseUser);
                    mNewChatUserPair.put(ParseConstants.USERNAME, mClickedUser);
                    mNewChatUserPair.put("chatUserId", AlphabeticSortClass.addUniqueId(ApplicationMain.mCurrentParseUser.getUsername(), mClickedUser.getUsername()));
                    mNewChatUserPair.put(ParseConstants.CHAT_ID, AlphabeticSortClass.addUniqueId(ApplicationMain.mCurrentParseUser.getObjectId(), mClickedUser.getObjectId()));
                    mChatRelation.add(newChat);
                    mNewChatUserPair.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    EventBus.getDefault().postSticky(new MessageUserChat(mNewChatUserPair));
                                    mActivityContext.startActivity(new Intent(mActivityContext, Chat_act.class));
                                } else {
                                    EventBus.getDefault().postSticky(new MessageUserChat(mNewChatUserPair));
                                    mActivityContext.startActivity(new Intent(mActivityContext, Chat_act.class));
                                }
                            } else {
                                Utilities.showDialog(mActivityContext, e.getMessage());
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
