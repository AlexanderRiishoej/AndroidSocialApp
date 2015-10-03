package com.mycompany.loginapp.profile.imageLoaders;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.eventMessages.MessageUpdateProfilePicture;
import com.mycompany.loginapp.helperClasses.FileInputOutputHelperClass;
import com.mycompany.loginapp.helperClasses.SnackBarHelperClass;
import com.mycompany.loginapp.profile.ProfileImageHolder;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.File;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Alexander on 19-04-2015.
 */
public class ProfilePhotoLoader implements IPhotoLoader {
    private String LOG;
    private Activity mActivity;

    public ProfilePhotoLoader() {
    }

    /** Gets the current Activity */
    @Override
    public void getActivity(Activity activity){
        this.mActivity = activity;
        this.LOG = activity.getClass().getSimpleName();
    }

    @Override
    public void saveImageToParse() {
        final ParseFile parseImageFile;
        try {
            parseImageFile = new ParseFile("profile_pic.JPG", FileInputOutputHelperClass.readBytesFromImageFile(ProfileImageHolder.mProfilePhotoFile));
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        //Save ParseFile to parse
        parseImageFile.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    ApplicationMain.mCurrentParseUser.put(ParseConstants.PROFILE_PICTURE, parseImageFile);
                    ApplicationMain.mCurrentParseUser.put(ParseConstants.PROFILE_PICTURE_PATH, ProfileImageHolder.mProfilePhotoFile.getAbsolutePath());
                    //Save the ParseFile with the new profile picture to Parse
                    ApplicationMain.mCurrentParseUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivity, R.id.fragment_profile_main_layout), "Profile picture successfully saved", Snackbar.LENGTH_SHORT);
                            } else {
                                e.printStackTrace();
                                SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivity, R.id.fragment_profile_main_layout), "Failed to save profile picture", Snackbar.LENGTH_LONG);
                            }
                        }
                    });
                } else {
                    SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivity, R.id.fragment_profile_main_layout), "Failed to save profile picture", Snackbar.LENGTH_LONG);
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Fetches the profile picture stored in Parse
     * This method is called if the profile picture does not exists on the phone already
     */
    private void fetchImageFromParse() {
        Log.d(LOG, "Loading image from parse...");
        ParseFile profilePicture = null;
        try {
            profilePicture = (ParseFile) ApplicationMain.mCurrentParseUser.get(ParseConstants.PROFILE_PICTURE);
        } catch (Exception ex) {
            SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivity, R.id.fragment_profile_main_layout), "Failed to retrieve profile picture", Snackbar.LENGTH_LONG);
            ex.printStackTrace();
        }

        // If the profile_image image is different from null, then load is from parse
        if (profilePicture == null) {
            SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivity, R.id.fragment_profile_main_layout), "Failed to retrieve profile picture", Snackbar.LENGTH_LONG);
            return;
        }
            profilePicture.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        /** Creates a new image file */
                        ProfileImageHolder.mProfilePhotoFile = ImageFileCreator.getOutputMediaFile();

                        if (ProfileImageHolder.mProfilePhotoFile != null) {
                            /** Write the data stream of the image to the new file */
                            FileInputOutputHelperClass.writeBytesToImageFile(data, ProfileImageHolder.mProfilePhotoFile);
                            /** Save the new path of the image to Parse. Path is created in getOutputMediaFile */
                            saveProfilePicturePathToParse();
                            /** Finally update the picture directory with the profile_image picture */
                            updateImageDirectory();
                            EventBus.getDefault().post(new MessageUpdateProfilePicture(ProfileImageHolder.mProfilePhotoFile.getAbsolutePath()));
                        }
                    } else {
                        SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivity, R.id.fragment_profile_main_layout), "Failed to retrieve profile picture", Snackbar.LENGTH_LONG);
                        e.printStackTrace();
                    }
                }
            }, new ProgressCallback() {
                public void done(Integer percentDone) {
                    // Update your progress spinner here. percentDone will be between 0 and 100.
                    //progressBarImage.setProgress(percentDone);
                }
            });

    }

    /**
     * https://parse.com/docs/android_guide#queries-basic
     */
    @Override
    public void loadImage() {
        ParseUser.getQuery().whereEqualTo(ParseConstants.USERNAME, ApplicationMain.mCurrentParseUser.getUsername()).getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    final String currentImageFilePath = parseUser.getString(ParseConstants.PROFILE_PICTURE_PATH);

                    /** Check if the stored path in Parse is empty */
                    if (currentImageFilePath != null) {
                        final File tempImageFile = new File(currentImageFilePath);
                        //Uri path = Uri.fromFile(tempImageFile);

                        /** Check if path exists since the user might have deleted this picture */
                        if (tempImageFile.exists()) {
                            ProfileImageHolder.mProfilePhotoFile = tempImageFile;
                            EventBus.getDefault().post(new MessageUpdateProfilePicture(ProfileImageHolder.mProfilePhotoFile.getAbsolutePath()));
                        } else {
                            fetchImageFromParse();
                        }
                    }
                } else {
                    SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivity, R.id.fragment_profile_main_layout), "Failed to retrieve profile picture", Snackbar.LENGTH_LONG);
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void updateImageDirectory() {
        //mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(mProfilePhotoFile.getAbsolutePath())));

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(ProfileImageHolder.mProfilePhotoFile.getAbsolutePath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mActivity.sendBroadcast(mediaScanIntent);
    }

    private void saveProfilePicturePathToParse() {
        ApplicationMain.mCurrentParseUser.put(ParseConstants.PROFILE_PICTURE_PATH, ProfileImageHolder.mProfilePhotoFile.getAbsolutePath());
        ApplicationMain.mCurrentParseUser.saveEventually();
    }
}
