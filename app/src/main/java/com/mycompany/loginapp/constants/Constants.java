package com.mycompany.loginapp.constants;

/**
 * Created by Alexander on 05-03-2015.
 */
public final class Constants {
    /** The Constant EXTRA_DATA. */
    public static final String EXTRA_DATA = "extraData";

    /** Media intents */
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_VIDEO_CAPTURE = 2;
    public static final int REQUEST_CHOOSE_PICTURE = 3;
    public static final int REQUEST_CHOOSE_VIDEO = 4;
    public static final int FILE_SIZE_LIMIT = 1024*1024*10; // 10 MB

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    /** Constants for Chat ListView View-layout */
    public static final int CHAT_ITEM_SENT = 0;
    public static final int CHAT_ITEM_RECEIVE = 1;

    /** Constants for NavigationAdapter */
    public final static String USER_ACT_NAME = "User_act";
    public final static String USER_LIST_ACT_NAME = "UserList_act";
    public final static String NEW_USER_CHAT_ACT_NAME = "NewUserChat_act";

}
