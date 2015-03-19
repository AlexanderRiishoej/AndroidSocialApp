package com.mycompany.loginapp.base;

/**
 * Created by Alexander on 06-02-2015.
 */

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.eventMessages.MessageEvent;
import com.mycompany.loginapp.eventMessages.MessageFinishActivities;

import de.greenrobot.event.EventBus;

public abstract class BaseActivity extends ActionBarActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        toolbar = (Toolbar) findViewById(R.id.toolbar_teal);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setTitle(null);
            toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_logout:
                EventBus.getDefault().post(new MessageFinishActivities());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
