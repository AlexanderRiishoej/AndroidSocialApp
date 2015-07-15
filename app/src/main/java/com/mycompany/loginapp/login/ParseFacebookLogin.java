package com.mycompany.loginapp.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.profile.ProfilePrivate_act;
import com.mycompany.loginapp.singletons.MySingleton;
import com.parse.DeleteCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alexander on 12-07-2015.
 */
public class ParseFacebookLogin {

    final private Activity mMainLoginActivity;
    //http://stackoverflow.com/questions/1005073/initialization-of-an-arraylist-in-one-line
    private final List<String> mPermissions = Arrays.asList("public_profile", "email", "user_birthday");
    private String mFbEmail, mFbName, mFbBirthday, mFbHomeTown, mFbGender, mFbUsername;
    private ParseFile mFbProfilePicture;
    private ParseUser mParseUser;

    private Profile mFbProfile;
    private ProfileTracker mFbProfileTracker;
    private AccessToken mFbAccessToken;
    private AccessTokenTracker mFbAccessTokenTracker;
    private MaterialDialog mProgressDialog;

    public ParseFacebookLogin(Activity mainLoginActivity) {
        this.mMainLoginActivity = mainLoginActivity;
    }

    public void signInUserWithParse() {
        ParseFacebookUtils.logInWithReadPermissionsInBackground(mMainLoginActivity, mPermissions, new LogInCallback() {
            @Override
            public void done(final ParseUser parseUser, ParseException e) {
                mProgressDialog = new MaterialDialog.Builder(mMainLoginActivity)
                        .title("Signing in with facebook")
                        .content("Please wait...")
                        .progress(true, 0)
                        .show();
                if (e == null) {
                    if (parseUser == null) {
                        Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        new MaterialDialog.Builder(mMainLoginActivity)
                                .title("Cancel")
                                .content("Signing up with Facebook was canceled...")
                                .show();
                    } else if (parseUser.isNew()) {
                        mParseUser = parseUser;
                        startTracking();
                        //get the facebook profile picture

                        if (mFbAccessToken != null) {
                            GraphRequest request = GraphRequest.newMeRequest(mFbAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject user, GraphResponse response) {
                                        /* handle the result */
                                    try {
                                        mFbGender = response.getJSONObject().getString("gender");
                                        mFbEmail = response.getJSONObject().getString("email");
                                        mFbBirthday = response.getJSONObject().getString("birthday");
                                        mFbHomeTown = response.getJSONObject().getString("hometown");
                                        final JSONObject coverUri = response.getJSONObject().getJSONObject("pic_cover");
//                                        new MaterialDialog.Builder(mMainLoginActivity)
//                                                .title("Information")
//                                                .content("Name: " + mFbProfile.getName() + "\r\n" + "Email: " + mFbEmail + "\r\n" + "Birthday: " + mFbBirthday + "\r\n" + "Hometown: " + mFbHomeTown)
//                                                .show();

                                        //setup the parseUser with the data from the GraphRequest
                                        if (mFbProfile != null) {
                                            if (mFbProfile.getName() != null) {
                                                mFbName = mFbProfile.getName();
                                                mFbUsername = mFbProfile.getFirstName();
                                                setUpFacebookParseUser();
                                            }
                                            new Handler().post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Picasso.with(mMainLoginActivity).load(mFbProfile.getProfilePictureUri(500, 500)).into(new Target() {
                                                        @Override
                                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                            new MaterialDialog.Builder(mMainLoginActivity)
                                                                    .title("Picasso bitmap")
                                                                    .content("Bitmap has been loaded")
                                                                    .show();

                                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                                            byte[] byteArray = stream.toByteArray();
                                                            mFbProfilePicture = new ParseFile("profile_pic.jpg", byteArray);
                                                            mFbProfilePicture.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if (e == null) {
                                                                        mParseUser.put(ParseConstants.PROFILE_PICTURE, mFbProfilePicture);
                                                                        saveNewParseUser();
                                                                    } else {
                                                                        saveNewParseUser();
                                                                    }
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onBitmapFailed(Drawable errorDrawable) {

                                                        }

                                                        @Override
                                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                        }
                                                    });                            }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        mParseUser.deleteInBackground();
                                        ParseUser.logOut();
                                        new MaterialDialog.Builder(mMainLoginActivity)
                                                .title("Error")
                                                .content(e.getMessage())
                                                .show();
                                    }
                                }
                            });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "name, birthday, gender, link, email, hometown, pic_cover");
                            request.setParameters(parameters);
                            request.executeAsync();

                        }

//                        new MaterialDialog.Builder(mMainLoginActivity)
//                                .title("Delete ParseUser?")
//                                .content("Delete current ParseUser?")
//                                .positiveText("Yes")
//                                .negativeText("No")
//                                .neutralText("Cancel")
//                                .callback(new MaterialDialog.ButtonCallback() {
//                                    @Override
//                                    public void onPositive(MaterialDialog dialog) {
//                                        parseUser.deleteInBackground(new DeleteCallback() {
//                                            @Override
//                                            public void done(ParseException e) {
//                                                if (e == null) {
//                                                    ParseUser.logOut();
//
//                                                    new MaterialDialog.Builder(mMainLoginActivity)
//                                                            .title("Deleted ParseUser")
//                                                            .content("Deletion succeeded" + "Is current user null? :" + ParseUser.getCurrentUser() == null ? "True" : "False")
//                                                            .show();
//                                                }
//                                            }
//                                        });
//
//                                    }
//
//                                    @Override
//                                    public void onNeutral(MaterialDialog dialog) {
//                                    }
//
//                                    @Override
//                                    public void onNegative(MaterialDialog dialog) {
//                                    }
//                                })
//                                .show();

                    } else {
                        Log.d("MyApp", "User logged in through Facebook!");
                        //mMainLoginActivity.startActivity(new Intent(mMainLoginActivity, ProfilePrivate_act.class));
                        //mMainLoginActivity.finish();
                    }
                } else {
                    if (mMainLoginActivity.isFinishing() || mMainLoginActivity.isDestroyed()) {
                        return;
                    }

                    new MaterialDialog.Builder(mMainLoginActivity)
                            .title("Error")
                            .content("Signing up with Facebook failed..." + e.getMessage())
                            .show();

                }
            }
        });
    }

    private void setUpFacebookParseUser(){
        mParseUser.setEmail(mFbEmail);
        mParseUser.setUsername(mFbUsername);
        mParseUser.put("fullName", mFbName);
        mParseUser.put("birthday", mFbBirthday);
        mParseUser.put("hometown", mFbHomeTown);
        mParseUser.put("gender", mFbGender);
    }

    private void saveNewParseUser() {
        mParseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mProgressDialog.dismiss();

                if (e == null) {
                    mMainLoginActivity.startActivity(new Intent(mMainLoginActivity, ProfilePrivate_act.class));
                    mMainLoginActivity.finish();

                } else if (e.getCode() == ParseException.USERNAME_TAKEN) {
                    usernameTaken(e.getMessage());
                } else {
                    new MaterialDialog.Builder(mMainLoginActivity)
                            .title("Error signing up")
                            .content(e.getMessage() + "\r\n" + "Please choose a new username")
                            .show();
                }
            }
        });
    }

    private void getFbProfilePicture() {
//        final ImageView tempImageView = new ImageView(mMainLoginActivity);
//        MySingleton.getMySingleton().getPicasso().load(mFbProfile.getProfilePictureUri(100, 100)).into(tempImageView, new Callback() {
//            @Override
//            public void onSuccess() {
//                new MaterialDialog.Builder(mMainLoginActivity)
//                        .title("Error signing up")
//                        .content("AHALHLHALHLHLLHALHLH")
//                        .show();
//                saveNewParseUser();
//            }
//
//            @Override
//            public void onError() {
//
//            }
//        });
            Picasso.with(mMainLoginActivity).load(mFbProfile.getProfilePictureUri(1000, 1000)).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    new MaterialDialog.Builder(mMainLoginActivity)
//                            .title("Picasso bitmap")
//                            .content("Bitmap has been loaded")
//                            .show();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    mFbProfilePicture = new ParseFile("profile_pic.PNG", byteArray);
                    mFbProfilePicture.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                mParseUser.put(ParseConstants.PROFILE_PICTURE, mFbProfilePicture);
                                saveNewParseUser();
                            }
                            else {
                                saveNewParseUser();
                            }
                        }
                    });
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });

    }

    private void usernameTaken(String error){
        new MaterialDialog.Builder(mMainLoginActivity)
                .title("Error signing up")
                .content(error + "\r\n" + "Please choose a new username")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputMaxLength(20)
                .positiveText("Submit")
                .negativeText("Cancel")
                .input("What's your username?", "USERNAME", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        //maybe even create a parse query to check if user exists while typing?
                        mParseUser.setUsername(input.toString());
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        mParseUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                saveNewParseUser();
                            }
                        });
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        mParseUser.deleteInBackground();
                        ParseUser.logOut();
                    }
                })
                .show();
    }

    /**
     * Starts Facebook tracking accesstoken and profile
     */
    private void startTracking() {
        // App code
        mFbProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Profile.setCurrentProfile(currentProfile);
                //mFbProfileTracker.stopTracking();
            }
        };
        mFbProfileTracker.startTracking();
        mFbAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                mFbAccessToken = currentAccessToken;
            }
        };
        mFbAccessTokenTracker.startTracking();

        mFbAccessToken = AccessToken.getCurrentAccessToken();
        mFbProfile = Profile.getCurrentProfile();
    }

    /**
     * Stops Facebook from tracking accesstoken and profile
     */
    public void stopTracking() {
        if (mFbAccessTokenTracker != null) {
            mFbAccessTokenTracker.stopTracking();
        }
        if (mFbProfileTracker != null) {
            mFbProfileTracker.stopTracking();
        }
    }
}
