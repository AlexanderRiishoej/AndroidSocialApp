package com.mycompany.loginapp.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.androidquery.AQuery;
import com.google.common.io.Files;
import com.mycompany.loginapp.adapters.ProfileRecyclerAdapter;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.eventMessages.MessageUpdateProfilePicture;
import com.mycompany.loginapp.utilities.Utilities;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.greenrobot.event.EventBus;

/**
 * Created by Alexander on 19-04-2015.
 */
public class ProfileImageLoader {
    private String LOG;
    private Context activityContext;

    public ProfileImageLoader(Context context) {
        this.activityContext = context;
        this.LOG = context.getClass().getSimpleName();
    }

    public void saveImageToParse() {
        final File finalImageFile = ProfileImageHolder.imageFile;
        final ParseFile parseImageFile;
        try {
            parseImageFile = new ParseFile("profile_pic.JPG", readBytesFromImageFile(ProfileImageHolder.imageFile));
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        /** Add picture to the Parse-cloud-backend */
        parseImageFile.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Utilities.showDialog(activityContext, "Error saving imageFile to cloud server... " + " " + e.getMessage());
                    e.printStackTrace();

                } else {
                    ParseUser.getCurrentUser().put(ParseConstants.PROFILE_PICTURE, parseImageFile);
                    ParseUser.getCurrentUser().put(ParseConstants.PROFILE_PICTURE_PATH, finalImageFile.getAbsolutePath());

                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Utilities.showDialog(activityContext, "Error loading imageFile" + " " + e.getMessage());
                                e.printStackTrace();

                            } else {
                                Log.d(LOG, "Profile picture successfully saved to Parse!");
                                // Inform subscribers about the new profile_image picture
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Loads the profile_image picture from Parse
     * Only if the path to a profile_image picture is not existing any longer
     */
    private void getProfilePictureFromParse() {
        Log.d(LOG, "Loading image from parse...");
        ParseFile profilePicture = null;
        try {
            profilePicture = (ParseFile) ParseUser.getCurrentUser().get(ParseConstants.PROFILE_PICTURE);
            //profilePicture.getUrl();
        } catch (Exception ex) {
            Utilities.showDialog(activityContext, "Error casting parseUser: " + " " + ex.getMessage());
            ex.printStackTrace();
        }

        // If the profile_image image is different from null, then load is from parse
        if (profilePicture != null) {
//            final ProgressBar progressBarImage = aQuery.id(R.id.profile_progress_bar).getProgressBar();
//            progressBarImage.setVisibility(View.VISIBLE);

            profilePicture.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    //progressBarImage.setVisibility(View.GONE);

                    if (e == null) {
                        try {
                            /** Creates a new file */
                            File newProfileImageFile = getOutputMediaFile(Constants.MEDIA_TYPE_IMAGE);
                            //currentMediaFilePath = newProfileImageFile.getAbsolutePath();
                            /** Write the data stream of the image to the new file */
                            Files.write(data, newProfileImageFile);
                            /** Save the new path of the image to Parse. Path is created in getOutputMediaFile */
                            saveProfilePicturePathToParse(newProfileImageFile);
                            /** Finally update the picture directory with the profile_image picture */
                            updateDirectoryPictures();
                            EventBus.getDefault().post(new MessageUpdateProfilePicture(ProfileImageHolder.imageFile.getAbsolutePath()));
                            //profileRecyclerAdapter.updateProfileImage(newProfileImageFile.getAbsolutePath());

                        } catch (Exception ex) {
                            Utilities.showDialog(activityContext, "Error creating imageFile" + " " + ex.getMessage());
                            ex.printStackTrace();
                        }

                    } else {
                        Utilities.showDialog(activityContext, "Error retrieving imageFile" + " " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }, new ProgressCallback() {
                public void done(Integer percentDone) {
                    // Update your progress spinner here. percentDone will be between 0 and 100.
                    //progressBarImage.setProgress(percentDone);
                }
            });
        } else {
            Utilities.showDialog(activityContext, "ParseFile is null...");
        }
    }

    /**
     * Gets the ParseUser equal to the Username of the logged in User.
     * Checks whether the stored path is null or if the path to the file exists.
     * If currentMediaFilePath is null it means that the profile_image picture path field in Parse is null, load the image stored in Parse instead
     * If the file exists, load it from the file, else load the image stored in Parse instead.
     */
    public void loadProfilePicture() {
        Log.d(LOG, "Current user: " + ParseUser.getCurrentUser().getUsername());
        /** https://parse.com/docs/android_guide#queries-basic */
        ParseUser.getQuery().whereEqualTo(ParseConstants.USERNAME, ParseUser.getCurrentUser().getUsername()).getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {

                if (parseUser != null) {
                    String currentImageFilePath = parseUser.getString(ParseConstants.PROFILE_PICTURE_PATH);

                    /** Check if the stored path in Parse is empty */
                    if (currentImageFilePath != null) {
                        File tempImageFile = new File(currentImageFilePath);
                        Log.d(activityContext.getClass().getSimpleName(), "PhotoPath: " + currentImageFilePath);

                        /** Check if path exists since the user might have deleted this picture */
                        if (tempImageFile.exists()) {
                            ProfileImageHolder.imageFile = tempImageFile;
                            EventBus.getDefault().post(new MessageUpdateProfilePicture(ProfileImageHolder.imageFile.getAbsolutePath()));
                        } else {
                            getProfilePictureFromParse();
                        }

                    } else {
                        getProfilePictureFromParse();
                    }

                } else {
                    Utilities.showDialog(activityContext, "Error getting parseUser object" + " " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveProfilePicturePathToParse(File imageFile) {
        ParseUser.getCurrentUser().put(ParseConstants.PROFILE_PICTURE_PATH, imageFile.getAbsolutePath());

        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {
                    Log.d(LOG, "Profile picture path successfully saved to Parse!");

                } else {
                    Utilities.showDialog(activityContext, "Error saving profile_image picture file" + " " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Puts the updated profile_image picture visible in the public directory pictures
     */
    public void updateDirectoryPictures() {
        //activityContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(imageFile.getAbsolutePath())));

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(ProfileImageHolder.imageFile.getAbsolutePath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activityContext.sendBroadcast(mediaScanIntent);
    }

    /**
     * Reads the image file and decodes it into an byte array
     */
    private byte[] readBytesFromImageFile(File imageFile) {
        byte[] data = null;
        try {
            data = Files.toByteArray(imageFile);
        } catch (Exception e) {
            Utilities.showDialog(activityContext, "Error reading file... " + " " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }

    public File getOutputMediaFile(int type) throws IOException {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!isExternalStorageAvailable()) {
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Social chat-app");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("LoginApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (type == Constants.MEDIA_TYPE_IMAGE) {
            File imageFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
            ProfileImageHolder.imageFile = imageFile;
            //ProfileImageHolder.profileImagePath = imageFile.getAbsolutePath();

        } else if (type == Constants.MEDIA_TYPE_VIDEO) {
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
            //currentMediaFilePath = mediaFile.getAbsolutePath();

        } else {
            return null;
        }

        return ProfileImageHolder.imageFile;
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}
