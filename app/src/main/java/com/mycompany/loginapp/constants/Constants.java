package com.mycompany.loginapp.constants;

/**
 * Created by Alexander on 05-03-2015.
 */
public final class Constants {
    /** The Constant EXTRA_DATA. */
    public static final String EXTRA_DATA = "extraData";

    /** Media intents */
    public static final int REQUEST_PROFILE_IMAGE_CAPTURE = 1;
//    public static final int REQUEST_VIDEO_CAPTURE = 2;
    public static final int REQUEST_CHOOSE_PROFILE_PICTURE = 2;
    public static final int REQUEST_CHOOSE_VIDEO = 8;
    public static final int REQUEST_COVER_IMAGE_CAPTURE = 3;
    public static final int REQUEST_CHOOSE_COVER_PHOTO = 4;
    public static final int FILE_SIZE_LIMIT = 1024*1024*10; // 10 MB

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    /** Constants for Chat ListView View-layout */
    public static final int CHAT_ITEM_SENT = 1;
    public static final int CHAT_ITEM_RECEIVE = 2;

    /** Constants for NavigationAdapter */
    public final static String PROFILE_PRIVATE = "ProfilePrivate_act";
    public final static String USER_LIST_ACT_NAME = "UserChatList_act";
    public final static String NEW_USER_CHAT_ACT_NAME = "NewUserChat_act";
    public final static String SOCIAL = "Social_act";

    /** Constants for ProfileRecyclerAdapter */
    public static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is navigation_header or Items
    public static final int TYPE_SECOND_ITEM = 1;
    public static final int TYPE_ITEM = 2;

    //Constant for logging in
    public static final int LOGGING_IN = 10;
}
