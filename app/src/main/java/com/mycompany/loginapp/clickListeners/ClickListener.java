package com.mycompany.loginapp.clickListeners;

import android.view.View;

/**
 * Created by Alexander on 12-04-2015.
 */
public interface ClickListener {

    public void onClick(View view, int position);

    public void onLongClick(View view, int position);
}
