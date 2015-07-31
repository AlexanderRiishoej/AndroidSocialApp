package com.mycompany.loginapp.login.notUsed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mycompany.loginapp.singletons.MySingleton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by Alexander on 11-07-2015.
 */
public class FacebookLogin {

    final private Context actContext;
    final private CallbackManager mCallbackManager;
    final private ImageView img;
    private Profile mFbProfile;
    private ProfileTracker mFbProfileTracker;
    private AccessToken mFbAccessToken;
    private AccessTokenTracker mFbAccessTokenTracker;
    private ParseFbSignIn mParseFbSignIn;
    public String mFbEmail, mFbName, mFbBirthday, mFbHomeTown, mFbGender;
    public ParseFile mFbProfilePicture;

    public FacebookLogin(Context actContext, ImageView img, CallbackManager callbackManager, ParseFbSignIn parseFbSignIn){
        this.actContext = actContext;
        this.mCallbackManager = callbackManager;
        this.setUpFacebookCallbacks();
        this.img = img;
        this.mParseFbSignIn = parseFbSignIn;
    }

    //https://developers.facebook.com/docs/graph-api/using-graph-api/v2.4
    private void setUpFacebookCallbacks(){
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        startTracking();
                        mFbAccessToken = AccessToken.getCurrentAccessToken();
                        mFbProfile = Profile.getCurrentProfile();

                        if (mFbProfile != null) {
                            getFbProfilePicture();
                            mFbName = mFbProfile.getName();
                            MySingleton.getMySingleton().getPicasso().load(mFbProfile.getProfilePictureUri(1000, 1000)).centerCrop().fit().into(img);
                        }

                        if(mFbAccessToken != null) {
                            GraphRequest request = GraphRequest.newMeRequest(mFbAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject user, GraphResponse response) {
                                        /* handle the result */
                                    try {
                                        mFbGender = response.getJSONObject().getString("gender");
                                        mFbEmail = response.getJSONObject().getString("email");
                                        mFbBirthday = response.getJSONObject().getString("birthday");
                                        mFbHomeTown = response.getJSONObject().getString("hometown");

                                        new MaterialDialog.Builder(actContext)
                                                .title("Information")
                                                .content("Name: " + mFbProfile.getName() + "\r\n" + "Email: " + mFbEmail + "\r\n" + "Birthday: " + mFbBirthday + "\r\n" + "Hometown: " + mFbHomeTown)
                                                .show();

                                        mParseFbSignIn.signIn();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        new MaterialDialog.Builder(actContext)
                                                .title("Error")
                                                .content(e.getMessage())
                                                .show();
                                    }
                                }
                            });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "name, birthday, gender, link, email, hometown");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        new MaterialDialog.Builder(actContext)
                                .title("Cancel")
                                .content("Signing up with Facebook canceled...")
                                .show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        new MaterialDialog.Builder(actContext)
                                .title("Error")
                                .content("Signing up with Facebook failed..." + "\n" + exception.getMessage())
                                .show();
                    }
                });
    }

    public void getFbProfilePicture(){
        if(mFbProfile.getProfilePictureUri(1, 1) != null) {
            MySingleton.getMySingleton().getPicasso().load(mFbProfile.getProfilePictureUri(1000, 1000)).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    new MaterialDialog.Builder(actContext)
                            .title("Picasso bitmap")
                            .content("Bitmap has been loaded")
                            .show();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    mFbProfilePicture = new ParseFile("profile_pic.JPG", byteArray);

                    mFbProfilePicture.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
//                                ParseUser.getCurrentUser().put(ParseConstants.PROFILE_PICTURE, fbParseFile);
//                                parseUser.saveInBackground(new SaveCallback() {
//                                    @Override
//                                    public void done(ParseException e) {
//                                        if(e == null) {
//                                            startActivity(new Intent(Login2_act.this, ProfilePrivate_act.class));
//                                            finish();
//                                        }
//                                        else {
//                                            new MaterialDialog.Builder(Login2_act.this)
//                                                    .title("Error")
//                                                    .content(e.getMessage())
//                                                    .show();
//                                        }
//                                    }
//                                });

                            } else {
                                e.printStackTrace();
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
    }

    /** Starts Facebook tracking accesstoken and profile */
    private void startTracking(){
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
    }

    /** Stops Facebook from tracking accesstoken and profile */
    public void stopTracking(){
        mFbAccessTokenTracker.stopTracking();
        mFbProfileTracker.stopTracking();
    }
}
