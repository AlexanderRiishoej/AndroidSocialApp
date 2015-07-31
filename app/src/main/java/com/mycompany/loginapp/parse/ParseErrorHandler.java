package com.mycompany.loginapp.parse;

import com.parse.ParseException;

/**
 * Created by Alexander on 17-07-2015.
 */
public class ParseErrorHandler {
    public static void handleParseError(ParseException e) {
//        switch (e.getCode()) {
//            case INVALID_SESSION_TOKEN: handleInvalidSessionToken()
//                break;
//
//            ... // Other Parse API errors that you want to explicitly handle
//        }
    }

    private static void handleInvalidSessionToken() {
        //--------------------------------------
        // Option 1: Show a message asking the user to log out and log back in.
        //--------------------------------------
        // If the user needs to finish what they were doing, they have the opportunity to do so.
        //
        // new AlertDialog.Builder(getActivity())
        //   .setMessage("Session is no longer valid, please log out and log in again.")
        //   .setCancelable(false).setPositiveButton("OK", ...).create().show();

        //--------------------------------------
        // Option #2: Show login screen so user can re-authenticate.
        //--------------------------------------
        // You may want this if the logout button could be inaccessible in the UI.
        //
        // startActivityForResult(new ParseLoginBuilder(getActivity()).build(), 0);
    }
}
