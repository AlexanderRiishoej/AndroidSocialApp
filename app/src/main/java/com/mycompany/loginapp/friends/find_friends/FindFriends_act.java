package com.mycompany.loginapp.friends.find_friends;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.fragmentFactory.FragmentFactory;

public class FindFriends_act extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolbar().setTitle("Friends");
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().
                    add(R.id.content_frame, FragmentFactory.getFragmentFactory().getFragment(FindFriendsFragment.class.getSimpleName()))
                            .commit();
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_find_friends_act;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_find_friends_act, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
