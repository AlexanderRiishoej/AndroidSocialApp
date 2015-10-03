package com.mycompany.loginapp.login;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.general.ResetPassword_act;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.login.notUsed.Login_act;
import com.mycompany.loginapp.profile.ProfilePrivate_act;
import com.mycompany.loginapp.registration.Register_act;
import com.mycompany.loginapp.singletons.MySingleton;
import com.mycompany.loginapp.utilities.Utilities;
import com.mycompany.loginapp.views.CharacterCountErrorWatcher;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class SignIn_act extends AppCompatActivity {

    public static final String LOG = Login_act.class.getSimpleName();
    private TextInputLayout mTextInputUsernameLayout, mTextInputPasswordLayout;
    private EditText user, pwd;
    private ParseFacebookLogin mParseFacebookLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form_2);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.teal_700));
        //final ImageView img = (ImageView) findViewById(R.id.background_image);
        //img.setImageAlpha(25);
        //img.setColorFilter(getResources().getColor(R.color.secondary_text_icons_light_theme), PorterDuff.Mode.DARKEN);
        //MySingleton.getMySingleton().getPicasso().load(R.drawable.android_login_background).centerCrop().fit().into(img);
        //MySingleton.getMySingleton().getPicasso().load("http://www.gogreencebu.com/wp-content/uploads/2013/04/Droplets-Water.jpg").centerCrop().fit().into(img);
        //this.makeWindowTransition();
        //mCallbackManager = CallbackManager.Factory.create();
//        setUpFacebookCallbacks();
        mParseFacebookLogin = new ParseFacebookLogin(this);

//        mFacebookLoginClass = new FacebookLogin(this, img, mCallbackManager, new ParseFbSignIn() {
//            @Override
//            public void signIn() {
//                signInUserWithParse();
//            }
//        });
        //mFbProfile = Profile.getCurrentProfile();
        mTextInputUsernameLayout = (TextInputLayout) findViewById(R.id.username_textinput);
        mTextInputPasswordLayout = (TextInputLayout) findViewById(R.id.password_textinput);

        user = mTextInputUsernameLayout.getEditText();
        pwd = mTextInputPasswordLayout.getEditText();
        user.addTextChangedListener(new CharacterCountErrorWatcher(mTextInputUsernameLayout, 2, 20));
        pwd.addTextChangedListener(new CharacterCountErrorWatcher(mTextInputPasswordLayout, 2, 20));
        //mTextInputUsernameLayout.setError("Bob");
        user.setText(MySingleton.getMySingleton().getDefaultSharedPreferences().getString("parseUser", ""));
        pwd.setText(MySingleton.getMySingleton().getDefaultSharedPreferences().getString("password", ""));
    }

//    @Override
//    protected int getLayoutResource() {
//        return R.layout.login_form_2;
//    }

    /**
     * Logs the User in.
     * If User credentials are wrong an error is shown with Parse.com's error description.
     * Finishes the current activity with call, finish()
     */
    public void LoginPressed(View view) {
        String username = user.getText().toString();
        String password = pwd.getText().toString();
        if (username.length() == 0 || password.length() == 0) {
            Utilities.showDialog(this, R.string.err_fields_empty);
            return;
        }
        MySingleton.getMySingleton().getAQuery().id(R.id.main_progressBar).visibility(View.VISIBLE);
        LoginProgressDialogClass.showLoginDialog(this, "Logging in please wait...");
        ParseUser.logInInBackground(username, password, new LogInCallback() {

            @Override
            public void done(ParseUser pu, ParseException e) {
                if (pu != null) {
                    ApplicationMain.mCurrentParseUser = pu;
                    // Start the logged in activity with the name of the person who logged in
                    startActivity(new Intent(SignIn_act.this, ProfilePrivate_act.class));
                    finish();
                    //LoginProgressDialogClass.dismissLoginDialog();
                } else {
                    Utilities.showDialog(SignIn_act.this, getString(R.string.err_login) + " " + e.getMessage());
                    LoginProgressDialogClass.dismissLoginDialog();
                    e.printStackTrace();
                }
            }
        });
    }

    public void facebookLoginPressed(View view) {
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday", "user_hometown"));
        //signInUserWithParse();
        this.mParseFacebookLogin.signInUserWithParse();
    }


//    private void getUserDetailsFromParse() {
//        parseUser = ParseUser.getCurrentUser();
//
////Fetch profile photo
//        try {
//            ParseFile parseFile = parseUser.getParseFile(ParseConstants.PROFILE_PICTURE);
//            byte[] data = parseFile.getData();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        mEmailID.setText(parseUser.getEmail());
//        mUsername.setText(parseUser.getUsername());
//
//        Toast.makeText(MainActivity.this, "Welcome back " + mUsername.getText().toString(), Toast.LENGTH_SHORT).show();
//
//    }

    //https://developers.facebook.com/docs/graph-api/using-graph-api/v2.4

    /**
     * Starts the Register activity.
     */
    public void RegisterPressed(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(new Intent(this, Register_act.class), 10, ActivityOptions.makeSceneTransitionAnimation(SignIn_act.this).toBundle());
        } else {
            startActivity(new Intent(this, Register_act.class));
        }
    }

    /**
     * Whenever you use any signup or login methods, the user is cached on disk.
     * You can treat this cache as a session, and automatically assume the user is logged in
     */
    private void CurrentUserLoggedIn() {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null) {
//            final Dialog dialog = new Dialog(this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
//            dialog.setTitle(getString(R.string.alert_wait));
//            dialog.setContentView(R.layout.log_in_progress_bar_layout);
//            dialog.show();
            LoginProgressDialogClass.showLoginDialog(this, "Logging in please wait...");
            //This method will ensure the session token is valid before setting the current user
            ParseUser.becomeInBackground(currentUser.getSessionToken(), new LogInCallback() {
                public void done(ParseUser loggedInUser, ParseException e) {
                    if (user != null) {
                        // The current user is now set to user.
                        String user = loggedInUser.getUsername();
                        ApplicationMain.mCurrentParseUser = loggedInUser;
                        startActivity(new Intent(SignIn_act.this, ProfilePrivate_act.class));
                        //dialog.dismiss();
                        SignIn_act.this.finish();
                    } else {
                        // The token could not be validated.
                        LoginProgressDialogClass.dismissLoginDialog();
                    }
                }
            });
        }

//        ParseUser currentUser = ParseUser.getCurrentUser();
//        if (currentUser != null) {
//            String user = currentUser.getUsername();
//            //ProfilePrivate_act.parseUser = currentUser;
////            startActivity(new Intent(this, Social_act.class));
//            ApplicationMain.mCurrentParseUser = currentUser;
//            startActivity(new Intent(this, ProfilePrivate_act.class));
//            this.finish();
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                //this.finishAfterTransition();
//                this.finish();
//            } else {
//                finish();
//            }
//        } else {
//            return;
//            // show the signup or login screen
//        }
    }

    /**
     * If a user wants to reset a password
     *
     * @param view
     */
    public void ForgotPassword(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(new Intent(SignIn_act.this, ResetPassword_act.class));

            //startActivity(new Intent(Login2_act.this, ResetPassword_act.class), ActivityOptions.makeSceneTransitionAnimation(Login2_act.this).toBundle());
        } else {
            startActivity(new Intent(SignIn_act.this, ResetPassword_act.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
//        if (mCallbackManager.onActivityResult(requestCode, resultCode, data))
//            return;
            //        if (requestCode == 10 && resultCode == RESULT_OK) {
//            Log.d("Result_OK", "Result is ok");
//            this.finish();
//        }
    }

    /**
     * Called when this activity starts or resumes
     */
    @Override
    protected void onResume() {
        super.onResume();
        this.CurrentUserLoggedIn();
    }

    /**
     * Called when another activity is in focus
     */
    @Override
    protected void onPause() {
        super.onPause();
        ApplicationMain.getDefaultSharedPreferences()
                .edit()
                .putString("parseUser", user.getText().toString())
                .putString("password", pwd.getText().toString())
                .apply();
        //commit writes data to persistent data immediately while apply will handle it in the background
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //this.mFacebookLoginClass.stopTracking();
        this.mParseFacebookLogin.stopTracking();
    }

    /**
     * -------------------------------------------------------------------------------------------------------------------------------------------------
     * Window Transitions
     */
    private void makeWindowTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setReenterTransition(makeReenterTransition());
            this.getWindow().setEnterTransition(makeEnterTransition());
            ActivityOptions.makeSceneTransitionAnimation(SignIn_act.this).toBundle();
        }
    }

    private Transition makeEnterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TransitionSet enterTransition = new TransitionSet();
            //Find main view group of the view
            LinearLayout mainLinearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);
            // Create a slide transition
            Transition slide = new Slide(Gravity.BOTTOM).setDuration(300);
            // Add the main view group to the slide transition
            slide.addTarget(mainLinearLayout);
            // Remember to set the transitiongroup to true
            mainLinearLayout.setTransitionGroup(true);
            // Finally add the transition to the TransitionSet
            enterTransition.addTransition(slide);

            return enterTransition;

        } else return null;
    }

    private Transition makeReenterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionSet reenterTransition = new TransitionSet();
            reenterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
            reenterTransition.excludeTarget(android.R.id.statusBarBackground, true);
            reenterTransition.excludeTarget(R.id.toolbar_teal, true);

            Transition slideInFromLeft = new Slide(Gravity.LEFT);

            reenterTransition.addTransition(slideInFromLeft).setDuration(300);
            return reenterTransition;

        } else return null;
    }
}
