package com.mycompany.loginapp.navigationDrawer;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.chat.NewUserChat_act;
import com.mycompany.loginapp.chat.oldNotUsed.UserChatList_act;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.eventMessages.MessageUpdateProfilePicture;
import com.mycompany.loginapp.friends.find_friends.FindFriends_act;
import com.mycompany.loginapp.general.Startup_act;
import com.mycompany.loginapp.helperClasses.ProfileHelperClass;
import com.mycompany.loginapp.login.LoginProgressDialogClass;
import com.mycompany.loginapp.news.Social_act;
import com.mycompany.loginapp.profile.ProfileImageHolder;
import com.mycompany.loginapp.profile.ProfilePrivate_act;
import com.mycompany.loginapp.singletons.MySingleton;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {
    private DrawerLayout mDrawerLayout;
    //own version of ActionBarDrawerToggle
    private SmoothActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;

    @Bind(R.id.circleView)public ImageView profile_image;
    @Bind(R.id.name)public TextView name;
    @Bind(R.id.email)public TextView email;

    private int id;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mUserLearnedDrawer = MySingleton.getMySingleton().getDefaultSharedPreferences().getBoolean(getString(R.string.user_learned_drawer), false);
        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MenuItem drawerItem = mNavigationView.getMenu().findItem(getDrawerItemId());
        drawerItem.setChecked(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View navigationView = inflater.inflate(R.layout.fragment_navigation_view, container, false);
        ButterKnife.bind(this, navigationView);
        mNavigationView = (NavigationView) navigationView.findViewById(R.id.navigation_view);
        MenuItem drawerItem = mNavigationView.getMenu().findItem(getDrawerItemId());
        drawerItem.setChecked(true);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getOrder();
                selectDrawerItem(id);
                mDrawerLayout.closeDrawers();

                return true;
            }
        });

        if (ApplicationMain.mCurrentParseUser.getString("fullName") != null) {
            name.setText(ApplicationMain.mCurrentParseUser.getString("fullName"));
        }
        if (ApplicationMain.mCurrentParseUser.getEmail() != null) {
            email.setText(ApplicationMain.mCurrentParseUser.getEmail());
        }
        this.loadNavigationHeaderViewImage();
        // Inflate the layout for this fragment
        return navigationView;
    }

    public void setUpDrawer(DrawerLayout mDrawerLayout) {
        this.mDrawerLayout = mDrawerLayout;
        setupDrawerToggle();
    }

    // The drawer toggle that binds the toolbar together with the navigation drawer
    private void setupDrawerToggle() {
        this.mDrawerToggle = new SmoothActionBarDrawerToggle(getActivity(), mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    MySingleton.getMySingleton().getDefaultSharedPreferences().edit()
                            .putBoolean(getString(R.string.user_learned_drawer), mUserLearnedDrawer)
                            .apply();
                }
                //getSupportActionBar().setTitle("Navigation!");
                /** Redraws the toolbar */
                // getActivity().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getSupportActionBar().setTitle(mActivityTitle);
                //getActivity().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(this.mNavigationView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
    }

    // Starts an activity based on the draweritem clicked
    private void selectDrawerItem(int item) {
        final String activityClassName = getActivity().getClass().getSimpleName();
        switch (item) {
            case 1: // item is Home/User activity
                if (activityClassName.equals(ProfilePrivate_act.class.getSimpleName())) break;
                this.mDrawerToggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getActivity().startActivity(new Intent(getActivity(), ProfilePrivate_act.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                        } else {
                            startActivity(new Intent(getActivity(), ProfilePrivate_act.class));
                        }
                    }
                });
                break;

            case 2: // item is profile_image not implemented yet
                if (activityClassName.equals(Social_act.class.getSimpleName())) break;
                this.mDrawerToggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    getActivity().startActivity(new Intent(getActivity(), Social_act.class
// ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                            getActivity().startActivity(new Intent(getActivity(), Social_act.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                        } else {
                            startActivity(new Intent(getActivity(), Social_act.class));
                        }
                    }
                });
                break;

            case 3: // item is active chat user list
                if (activityClassName.equals(UserChatList_act.class.getSimpleName())) break;
                this.mDrawerToggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getActivity().startActivity(new Intent(getActivity(), UserChatList_act.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                        } else {
                            startActivity(new Intent(getActivity(), UserChatList_act.class));
                        }
                    }
                });
                break;

            case 4: // item is create new chat
                if (activityClassName.equals(NewUserChat_act.class.getSimpleName())) break;
                this.mDrawerToggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getActivity().startActivity(new Intent(getActivity(), NewUserChat_act.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                        } else {
                            getActivity().startActivity(new Intent(getActivity(), NewUserChat_act.class));
                        }
                    }
                });
                break;
            case 5: // item is create new chat
                if (activityClassName.equals(FindFriends_act.class.getSimpleName())) break;
                this.mDrawerToggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getActivity().startActivity(new Intent(getActivity(), FindFriends_act.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                        } else {
                            getActivity().startActivity(new Intent(getActivity(), FindFriends_act.class));
                        }
                    }
                });
                break;
            case 7:
                this.mDrawerToggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        LoginProgressDialogClass.showLoginDialog(getActivity(), "Logging out please wait...");
                        ParseUser.logOutInBackground(new LogOutCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ProfileImageHolder.setImageFilesNull();
                                    ProfileHelperClass.setUserOffline();
                                    getActivity().startActivity(new Intent(getActivity(), Startup_act.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    getActivity().finish();
                                    LoginProgressDialogClass.dismissLoginDialog();
                                } else {
                                    LoginProgressDialogClass.dismissLoginDialog();
                                    new MaterialDialog.Builder(getActivity()).content("An error occurred while logging out...").show();
                                }
                            }
                        });
                    }
                });
                break;
            default:
                break;
        }
    }

    /* Gets the drawer item id of the navigation drawer in order to highlight it */
    private int getDrawerItemId() {
        switch (getActivity().getClass().getSimpleName()) {
            case Constants.PROFILE_PRIVATE:
                return R.id.navigation_sub_item_1;

            case Constants.SOCIAL:
                return R.id.navigation_sub_item_2;

            case Constants.USER_LIST_ACT_NAME:
                return R.id.navigation_sub_item_3;

            case Constants.NEW_USER_CHAT_ACT_NAME:
                return R.id.navigation_sub_item_4;

            case Constants.FIND_FRIENDS:
                return R.id.navigation_sub_item_5;

            default:
                return R.id.navigation_sub_item_1;
        }
    }

    public ActionBarDrawerToggle getNavigationDrawerToggle() {
        return mDrawerToggle;
    }

    public NavigationView getNavigationView() {
        return mNavigationView;
    }

    /**
     * Event received when a new profile_image picture has been chosen
     */
    public void onEvent(MessageUpdateProfilePicture newProfilePictureEvent) {
        loadNavigationHeaderViewImage();
    }

    private void loadNavigationHeaderViewImage() {
        if (ProfileImageHolder.mProfilePhotoFile != null && ProfileImageHolder.mProfilePhotoFile.exists()) {
            MySingleton.getMySingleton().getPicasso().load(ProfileImageHolder.mProfilePhotoFile).centerCrop().fit().noPlaceholder().into(profile_image);
        } else {
            ParseUser.getQuery().whereEqualTo(ParseConstants.USERNAME, ApplicationMain.mCurrentParseUser.getUsername()).getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e == null) {
                        MySingleton.getMySingleton().getPicasso().load("file:" + parseUser.getString(ParseConstants.PROFILE_PICTURE_PATH)).centerCrop().fit().noPlaceholder().
                                into(profile_image);
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
