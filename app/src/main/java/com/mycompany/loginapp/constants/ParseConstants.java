package com.mycompany.loginapp.constants;

/**
 * Created by Alexander on 13-03-2015.
 * Maintains all the constants for queueing Parse
 */
public final class ParseConstants {

    /** Fields specific queries */
    public static final String ONLINE = "online";
    public static final String USERNAME = "username";
    public static final String PROFILE_PICTURE = "profilePicture";
    public static final String PROFILE_PICTURE_PATH = "profilePicturePath";
    public static final String CHAT_SENDER = "sender";
    public static final String CHAT_RECEIVER = "receiver";
    public static final String CREATED_AT = "createdAt";
    public static final String CREATED_BY = "createdBy";
    public static final String UPDATED_AT = "updatedAt";
    public static final String MESSAGE = "message";
    public static final String CHAT_ID = "chatId";

    public static final String SEEN = "seen";
    public static final String COVER_PHOTO = "coverPhoto";
    public static final String COVER_PHOTO_PATH = "coverPhotoPath";

    /** Relations */
    public static final String FRIENDS = "friendsRelation";
    public static final String CHAT_RELATION = "ChatRelation";
    public static final String USER_CHAT_RELATION = "UserChatRelation";

    /** Class specific queries */
    public static final String CHAT = "Chat";
    public static final String CHAT_USERS = "ChatUsers";
}
