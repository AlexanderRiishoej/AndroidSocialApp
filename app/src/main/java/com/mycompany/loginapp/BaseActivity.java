package com.mycompany.loginapp;

/**
 * Created by Alexander on 06-02-2015.
 */

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public abstract class BaseActivity extends ActionBarActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        toolbar = (Toolbar) findViewById(R.id.toolbar_teal);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // Sets the ToolBar's title appearance
            toolbar.setTitleTextAppearance(this, R.style.customTextViewStyle);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected abstract int getLayoutResource();

    protected void setActionBarIcon(int iconRes) {
        toolbar.setNavigationIcon(iconRes);
    }

    protected void setActionBarTitle(int titleId) {
        toolbar.setTitle(titleId);
    }

    protected void setDisplayHomeAsUpEnabled(boolean isEnabled){
        getSupportActionBar().setDisplayHomeAsUpEnabled(isEnabled);
    }
}
