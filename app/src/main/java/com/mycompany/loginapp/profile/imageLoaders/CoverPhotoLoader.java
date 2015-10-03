package com.mycompany.loginapp.profile.imageLoaders;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.common.io.Files;
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
import com.parse.SaveCallback;

import java.io.File;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Alexander on 19-04-2015.
 */
public class CoverPhotoLoader implements IPhotoLoader {
    private String LOG;
    private Activity mActivity;

    public CoverPhotoLoader() {
    }

    private void saveCoverPhotoPathToParse() {
        ApplicationMain.mCurrentParseUser.put(ParseConstants.COVER_PHOTO_PATH, ProfileImageHolder.mProfileCoverPhotoFile.getAbsolutePath());
        ApplicationMain.mCurrentParseUser.saveEventually();
    }

    /** Gets the current Activity */
    @Override
    public void getActivity(Activity activity){
        this.mActivity = activity;
        this.LOG = activity.getClass().getSimpleName();
    }

    @Override
    public void saveImageToParse() {
        final File finalCoverPhotoFile = ProfileImageHolder.mProfileCoverPhotoFile;
        final ParseFile parseCoverPhotoFile;
        try {
            parseCoverPhotoFile = new ParseFile("cover_photo.JPG", FileInputOutputHelperClass.readBytesFromImageFile(ProfileImageHolder.mProfileCoverPhotoFile));
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        /** Add picture to the Parse-cloud-backend */
        parseCoverPhotoFile.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    ApplicationMain.mCurrentParseUser.put(ParseConstants.COVER_PHOTO, parseCoverPhotoFile);
                    ApplicationMain.mCurrentParseUser.put(ParseConstants.COVER_PHOTO_PATH, finalCoverPhotoFile.getAbsolutePath());

                    ApplicationMain.mCurrentParseUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivity, R.id.fragment_profile_main_layout), "Cover photo successfully saved", Snackbar.LENGTH_SHORT);

                            } else {
                                SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivity, R.id.fragment_profile_main_layout), "Failed to save cover photo", Snackbar.LENGTH_LONG);
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else {
                    SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivity, R.id.fragment_profile_main_layout), "Failed to save cover photo", Snackbar.LENGTH_LONG);
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Loads the profile_image picture from Parse
     * Only if the path to a profile_image picture is not existing any longer
     */
    private void fetchImageFromParse() {
        Log.d(LOG, "Loading image from parse...");
        ParseFile coverPicture = null;
        try {
            coverPicture = (ParseFile) ParseUser.getCurrentUser().get(ParseConstants.COVER_PHOTO);
            //profilePicture.getUrl();
        } catch (Exception ex) {
            SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivity, R.id.fragment_profile_main_layout), "Failed to retrieve cover photo", Snackbar.LENGTH_LONG);
            ex.printStackTrace();
        }
        if (coverPicture == null) {
            return;
        }

        // If the profile_image image is different from null, then load is from parse
            coverPicture.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        try {
                            /** Creates a new image file */
                            ProfileImageHolder.mProfileCoverPhotoFile = ImageFileCreator.getOutputMediaFile();

                            if(ProfileImageHolder.mProfileCoverPhotoFile != null) {
                                /** Write the data stream of the image to the new file */
                                Files.write(data, ProfileImageHolder.mProfileCoverPhotoFile);
                                /** Save the new path of the image to Parse. Path is created in getOutputMediaFile */
                                saveCoverPhotoPathToParse();
                                /** Finally update the picture directory with the cover photo */
                                updateImageDirectory();
                                EventBus.getDefault().post(new MessageUpdateProfilePicture(ProfileImageHolder.mProfileCoverPhotoFile.getAbsolutePath()));
                            }
                        } catch (Exception ex) {
                            SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivity, R.id.fragment_profile_main_layout), "Failed to retrieve cover photo", Snackbar.LENGTH_LONG);
                            ex.printStackTrace();
                        }

                    } else {
                        SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(mActivity, R.id.fragment_profile_main_layout), "Failed to retrieve cover photo", Snackbar.LENGTH_LONG);
                        e.printStackTrace();
                    }
                }
            });
    }

    /**
     * Gets the ParseUser equal to the Username of the logged in User.
     * Checks whether the stored path is null or if the path to the file exists.
     * If currentMediaFilePath is null it means that the cover photo path field in Parse is null, load the image stored in Parse instead
     * If the file exists, load it from the file, else load the image stored in Parse instead.
     */
    @Override
    public void loadImage() {
        ParseUser.getQuery().whereEqualTo(ParseConstants.USERNAME, ApplicationMain.mCurrentParseUser.getUsername()).getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    final String currentCoverPhotoFilePath = parseUser.getString(ParseConstants.COVER_PHOTO_PATH);

                    /** Check if the stored path in Parse is empty */
                    if (currentCoverPhotoFilePath != null) {
                        File tempCoverPhotoFile = new File(currentCoverPhotoFilePath);
                        Log.d(mActivity.getClass().getSimpleName(), "PhotoPath: " + currentCoverPhotoFilePath);

                        /** Check if path exists since the user might have deleted this picture */
                        if (tempCoverPhotoFile.exists()) {
                            ProfileImageHolder.mProfileCoverPhotoFile = tempCoverPhotoFile;
                            EventBus.getDefault().post(new MessageUpdateProfilePicture(ProfileImageHolder.mProfileCoverPhotoFile.getAbsolutePath()));
                        } else {
                            fetchImageFromParse();
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void updateImageDirectory() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(ProfileImageHolder.mProfileCoverPhotoFile.getAbsolutePath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mActivity.sendBroadcast(mediaScanIntent);
    }
}
