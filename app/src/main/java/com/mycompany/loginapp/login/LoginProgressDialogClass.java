package com.mycompany.loginapp.login;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.mycompany.loginapp.R;

/**
 * Created by Alexander on 08-08-2015.
 * Responsibility of showing and dismissing the login and logout dialog
 */
public class LoginProgressDialogClass {
    private static Dialog mLoginDialog;

    public LoginProgressDialogClass(){
    }

    /** Shows the login dialog */
    public static void showLoginDialog(Context mActivityContext, String title){
        mLoginDialog = new Dialog(mActivityContext, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        mLoginDialog.setContentView(R.layout.log_in_progress_bar_layout);
        final TextView textView = (TextView) mLoginDialog.findViewById(R.id.login_logout_status);
        textView.setText(title);
        //mLoginDialog.setTitle(title);
        mLoginDialog.show();
    }

    /** Dismisses the login dialog */
    public static void dismissLoginDialog(){
        mLoginDialog.dismiss();
    }
}
