package com.mycompany.loginapp.helperClasses;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.ApplicationMain;

/**
 * Created by Alexander on 05-09-2015.
 * Helper class for creating SnackBars
 */
public class SnackBarHelperClass {

    /** Shows a basic SnackBar with the primary color and white text color */
    public static void showBasicSnackBar(View baseView, String content, int duration){
        Snackbar sb = Snackbar.make(baseView, content, duration);
        sb.setActionTextColor(ApplicationMain.getAppContext().getResources().getColor(R.color.white));
        sb.getView().setBackgroundColor(ApplicationMain.getAppContext().getResources().getColor(R.color.colorPrimary));
        sb.show();
    }

    private void showSnackBar(View view) {
        Snackbar.make(view, "This ia an example!", Snackbar.LENGTH_LONG)
                .setAction("Do something useful!", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show(); // Dont forget to show!
    }
}
