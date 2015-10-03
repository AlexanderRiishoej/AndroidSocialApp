package com.mycompany.loginapp.fragmentFactory;


import android.support.v4.app.Fragment;

import com.mycompany.loginapp.chat.ChatListFragment;
import com.mycompany.loginapp.friends.find_friends.FindFriendsFragment;
import com.mycompany.loginapp.news.NewChatFragment;
import com.mycompany.loginapp.news.WallPostFragment;
import com.mycompany.loginapp.profile.PrivateProfileFragment;

/**
 * Created by Alexander on 13-09-2015.
 * Factory responsible for creating fragments.
 */
public class FragmentFactory {
    private final static FragmentFactory FRAGMENT_FACTORY = new FragmentFactory();

    public static FragmentFactory getFragmentFactory(){
        return FRAGMENT_FACTORY;
    }

    public Fragment getFragment(String type){
        Fragment mFragment = null;

        if(type == null){
            return null;
        }

        if(type.equalsIgnoreCase(NewChatFragment.class.getSimpleName())){
            mFragment = new NewChatFragment();
        }
        if(type.equalsIgnoreCase(WallPostFragment.class.getSimpleName())){
            mFragment = new WallPostFragment();
        }
        if(type.equalsIgnoreCase(ChatListFragment.class.getSimpleName())){
            mFragment = new ChatListFragment();
        }
        if(type.equalsIgnoreCase(PrivateProfileFragment.class.getSimpleName())){
            mFragment = new PrivateProfileFragment();
        }
        if(type.equalsIgnoreCase(FindFriendsFragment.class.getSimpleName())){
            mFragment = new FindFriendsFragment();
        }

        return mFragment;
    }
}
