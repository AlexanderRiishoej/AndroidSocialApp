package com.mycompany.loginapp.registration;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidquery.AQuery;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.profile.ProfilePrivate_act;
import com.mycompany.loginapp.registration.textWatchers.BirthdayTextWatcher;
import com.mycompany.loginapp.registration.textWatchers.ConfirmPasswordTextWatcher;
import com.mycompany.loginapp.registration.textWatchers.EmailTextWatcher;
import com.mycompany.loginapp.registration.textWatchers.FirstNameTextWatcher;
import com.mycompany.loginapp.registration.textWatchers.LastNameTextWatcher;
import com.mycompany.loginapp.registration.textWatchers.MobilePhoneNumberTextWatcher;
import com.mycompany.loginapp.registration.textWatchers.PasswordTextWatcher;
import com.mycompany.loginapp.registration.textWatchers.UsernameTextWatcher;
import com.mycompany.loginapp.utilities.Utilities;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

/**
 * The Class Register is the Activity class that shows user registration screen
 * that allows user to register itself on Parse server.
 */
//@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Register_act extends BaseActivity {

    public static final String LOG = Register_act.class.getSimpleName();
    private AQuery aQuery;
    private ScrollView mScrollView;
    private FirstNameTextWatcher mFirstNameTextWatcher;
    private LastNameTextWatcher mLastNameTextWatcher;
    private UsernameTextWatcher mUsernameTextWatcher;
    private PasswordTextWatcher mPasswordTextWatcher;
    private ConfirmPasswordTextWatcher mConfirmPasswordTextWatcher;
    private EmailTextWatcher mEmailTextWatcher;
    private BirthdayTextWatcher mBirthdayTextWatcher;
    private MobilePhoneNumberTextWatcher mMobilePhoneNumberTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeWindowTransition();
        aQuery = new AQuery(this);
        aQuery.id(R.id.toolbar_title).text("Create an account");
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
        setUpRegisterForm();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.register;
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_register, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void register(View view) {
       final MaterialDialog singupDialog =  new MaterialDialog.Builder(this)
                .title("Registering account")
                .content("Please wait...")
                .progress(true, 0)
                .show();

        if (noErrors()){
            final ParseUser parseUser = new ParseUser();
            parseUser.setPassword(mPasswordTextWatcher.getEditText().getText().toString());
            parseUser.setEmail(mEmailTextWatcher.getEditText().getText().toString());
            parseUser.setUsername(mUsernameTextWatcher.getEditText().getText().toString());

            parseUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    singupDialog.dismiss();
                    if (e == null) {
                        parseUser.put("mobilePhoneNumber", mMobilePhoneNumberTextWatcher.getEditText().getText().toString());
                        parseUser.put("firstname", mFirstNameTextWatcher.getEditText().getText().toString());
                        parseUser.put("lastname", mLastNameTextWatcher.getEditText().getText().toString());
                        parseUser.put("gender", mMobilePhoneNumberTextWatcher.getEditText().getText().toString());

                        startActivity(new Intent(Register_act.this, ProfilePrivate_act.class));
                        Log.d(LOG, "Current user is: " + ParseUser.getCurrentUser().getUsername());
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Utilities.showDialog(
                                Register_act.this,
                                getString(R.string.err_singup) + " "
                                        + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

        } else {
            singupDialog.dismiss();
            new MaterialDialog.Builder(this)
                    .content("Error signing up")
                    .negativeText("Cancel")
                    .show();
        }
    }

    private void setUpRegisterForm() {
        //firstname
        final TextInputLayout mTextInputFirstname = (TextInputLayout) findViewById(R.id.firstname_textinput);
        mFirstNameTextWatcher = new FirstNameTextWatcher(mTextInputFirstname);
        mTextInputFirstname.getEditText().addTextChangedListener(mFirstNameTextWatcher);
        //lastname
        final TextInputLayout mTextInputLastname = (TextInputLayout) findViewById(R.id.lastname_textinput);
        mLastNameTextWatcher = new LastNameTextWatcher(mTextInputLastname);
        mTextInputLastname.getEditText().addTextChangedListener(mLastNameTextWatcher);
        //username
        final TextInputLayout mTextInputUsername = (TextInputLayout) findViewById(R.id.username_textinput);
        mUsernameTextWatcher = new UsernameTextWatcher(mTextInputUsername);
        mTextInputUsername.getEditText().addTextChangedListener(mUsernameTextWatcher);
        //password
        final TextInputLayout mTextInputPassword = (TextInputLayout) findViewById(R.id.password_textinput);
        mPasswordTextWatcher = new PasswordTextWatcher(mTextInputPassword);
        mTextInputPassword.getEditText().addTextChangedListener(mPasswordTextWatcher);
        //confirm password
        final TextInputLayout mTextInputConfirmPassword = (TextInputLayout) findViewById(R.id.confirm_password_textinput);
        mConfirmPasswordTextWatcher = new ConfirmPasswordTextWatcher(mTextInputConfirmPassword, mTextInputPassword);
        mTextInputConfirmPassword.getEditText().addTextChangedListener(mConfirmPasswordTextWatcher);
        //email
        final TextInputLayout mTextInputEmail = (TextInputLayout) findViewById(R.id.email_textinput);
        mEmailTextWatcher = new EmailTextWatcher(mTextInputEmail);
        mTextInputEmail.getEditText().addTextChangedListener(mEmailTextWatcher);

        //birthday
        final TextInputLayout mTextInputBirthday = (TextInputLayout) findViewById(R.id.birth_month_textinput);
        mBirthdayTextWatcher = new BirthdayTextWatcher(mTextInputBirthday, this);
        mTextInputBirthday.getEditText().addTextChangedListener(mBirthdayTextWatcher);

        //final TextInputLayout mTextInputBirthDate = (TextInputLayout) findViewById(R.id.birth_date_textinput);
        //final TextInputLayout mTextInputBirthYear  = (TextInputLayout) findViewById(R.id.birth_year_textinput);
        //gender
        final TextInputLayout mTextInputGender = (TextInputLayout) findViewById(R.id.gender_textinput);
        mTextInputPassword.getEditText().addTextChangedListener(new PasswordTextWatcher(mTextInputPassword));
        //mobile phone number
        final TextInputLayout mTextInputMobilePhoneNumber = (TextInputLayout) findViewById(R.id.mobile_phone_number_textinput);
        mTextInputPassword.getEditText().addTextChangedListener(new PasswordTextWatcher(mTextInputPassword));

    }

    //Checks if any fields is in its error state
    private boolean noErrors() {
        if(!mFirstNameTextWatcher.hasError() && !mLastNameTextWatcher.hasError() && !mUsernameTextWatcher.hasError() && !mPasswordTextWatcher.hasError()
                && !mConfirmPasswordTextWatcher.hasError() && !mEmailTextWatcher.hasError()){
            return true;
        }
        if(mFirstNameTextWatcher.hasError()){
            mFirstNameTextWatcher.updateTextChanged();
            mFirstNameTextWatcher.getEditText().requestFocus();
            mScrollView.smoothScrollTo(0, mFirstNameTextWatcher.getEditText().getBottom());
            return false;
        }
        if(mLastNameTextWatcher.hasError()){
            mLastNameTextWatcher.updateTextChanged();
            mLastNameTextWatcher.getEditText().requestFocus();
            mScrollView.smoothScrollTo(0, mLastNameTextWatcher.getEditText().getBottom());
            return false;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * -------------------------------------------------------------------------------------------------------------------------------------------------
     * Window Transitions
     */
    private void makeWindowTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(makeEnterTransition());
            getWindow().setReturnTransition(makeReturnTransition());
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        }
    }

    private Transition makeEnterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionSet enterTransition = new TransitionSet();
//
//            Transition groupTransition = new Slide(Gravity.LEFT);
//
//            LinearLayout resetPswLayoutMain = (LinearLayout) findViewById(R.id.main_layout_register);
//            groupTransition.addTarget(resetPswLayoutMain);
//            resetPswLayoutMain.setTransitionGroup(true);
//            enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
//            enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
//            enterTransition.excludeTarget(R.id.toolbar_teal, true);
//            //enterTransition.addTransition(fadeIn);
//            enterTransition.addTransition(groupTransition).setDuration(300);

            return enterTransition;
        } else return null;
    }

    private Transition makeReturnTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TransitionSet enterTransition = new TransitionSet();

            Transition upperPartSlide = new Slide(Gravity.LEFT);
            enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
            enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
            enterTransition.excludeTarget(R.id.toolbar_teal, true);
            enterTransition.addTransition(upperPartSlide);

            enterTransition.setDuration(300);
            return enterTransition;
        } else return null;
    }
}
