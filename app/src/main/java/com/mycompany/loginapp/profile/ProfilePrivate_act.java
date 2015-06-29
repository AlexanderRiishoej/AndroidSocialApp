package com.mycompany.loginapp.profile;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.androidquery.AQuery;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.activities.Login_act;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.chat.UserChatList_act;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.eventMessages.MessageFinishActivities;
import com.mycompany.loginapp.eventMessages.MessageUpdateCoverPhoto;
import com.mycompany.loginapp.eventMessages.MessageUpdateProfilePicture;
import com.mycompany.loginapp.utilities.Utilities;
import com.parse.ParseUser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import de.greenrobot.event.EventBus;

//@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ProfilePrivate_act extends BaseActivity {
    public static final String LOG = ProfilePrivate_act.class.getSimpleName();
    private AQuery aQuery;
    private ProfileImageLoader profileImageLoader;
    private ProfileCoverPhotoLoader mProfileCoverPhotoLoader;
    private PrivateProfileFragment mPrivateProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //makeWindowTransition();
        EventBus.getDefault().register(this);
        mPrivateProfileFragment = PrivateProfileFragment.newInstance();
        aQuery = new AQuery(this);
        //setActionBarIcon(R.drawable.ic_arrow_back_white_24dp);
        aQuery.id(R.id.toolbar_title).text(ParseUser.getCurrentUser().getUsername());
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, mPrivateProfileFragment)
                    .commit();
        }
        profileImageLoader = new ProfileImageLoader(this);
        mProfileCoverPhotoLoader = new ProfileCoverPhotoLoader(this);
        mProfileCoverPhotoLoader.loadCoverPhoto();
        profileImageLoader.loadProfilePicture();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.private_profile;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_chat:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(new Intent(ProfilePrivate_act.this, UserChatList_act.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                } else {
                    startActivity(new Intent(ProfilePrivate_act.this, UserChatList_act.class));
                }
                return true;
            case R.id.action_camera:
                dispatchTakePictureIntent();
                return true;
            case R.id.action_pictures:
                choosePictureFromGallery();
                return true;
//            case R.id.action_video:
//                dispatchTakeVideoIntent();
//                return true;
//            case R.id.action_logout:
//                ParseUser.logOut();
//                //this.finish();
//                //Clear top: Clears the entire stack except for the activity being launched
//                // http://stackoverflow.com/questions/3007998/on-logout-clear-activity-history-stack-preventing-back-button-from-opening-l
//                startActivity(new Intent(UserActivity.this, Login.class).setFlags(
//                        Intent.FLAG_ACTIVITY_NEW_TASK |
//                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
//                        Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                this.finishAfterTransition();
//                return true;
            case R.id.action_edit_profile:
                startActivity(new Intent(this, ProfilePublic_act.class));

                //startActivity(new Intent(this, NewsFeed_act.class));
                //startActivity(new Intent(this, ToolbarControlRecyclerViewActivity.class));
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.content_frame, PrivateProfileFragment.newInstance()).addToBackStack(null)
//                        .commit();
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                DialogFragment newFragment = EditProfileFragment.newInstance();
////                newFragment.setStyle( DialogFragment.STYLE_NORMAL, android.R.style.Theme );
//                newFragment.show(getSupportFragmentManager(), "");

                return true;
            case R.id.action_cover_photo:
                chooseCoverFromGallery();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        Log.d(LOG, "is destroyed.");
        super.onDestroy();
    }

    public void showSnackBar(View view){
        Snackbar.make(view, "This ia an example!", Snackbar.LENGTH_LONG)
                .setAction("Do something useful!", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show(); // Dont forget to show!
    }

    /**
     * Finishes this activity
     *
     * @param event - received by all active activities to call finish()
     *              http://developer.android.com/guide/components/tasks-and-back-stack.html
     *              gets called twice - fix it
     */
    public void onEvent(MessageFinishActivities event) {
        Log.d(LOG, "FINISHING USER_ACTIVITY");
        this.finish();
    }

    /** Event received when a new profile picture has been chosen
     *  ProfilePrivate starts this event from the OnActivityResult() */
    public void onEvent(MessageUpdateProfilePicture newProfilePictureEvent){
        //profileRecyclerAdapter.updateRecyclerItem(0);
        mPrivateProfileFragment.updateProfilePicture();
    }

// http://developer.android.com/training/camera/photobasics.html - camera intent

    /**
     * Result received by the camera intent containing the path to the image file
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            // If its a picture taken from the camera
            if (requestCode == Constants.REQUEST_IMAGE_CAPTURE) {
                Log.d(LOG + " onActivityResult", "Photo taken with path: " + ProfileImageHolder.imageFile.getAbsolutePath());

                profileImageLoader.saveImageToParse();
                profileImageLoader.updateDirectoryPictures();
                EventBus.getDefault().post(new MessageUpdateProfilePicture(ProfileImageHolder.imageFile.getAbsolutePath()));
            }
            // If is a picture taken from the gallery
            if (requestCode == Constants.REQUEST_CHOOSE_PICTURE) {
                Log.d(LOG + " onActivityResult", "Photo chosen with path: " + data.getData());
                /** Get the Uri for the image chosen */
                Uri imageUri = data.getData();
                Log.d(LOG, "MediaUri: " + imageUri);
                /** Populate image */
                Log.d(LOG, "MediaImagePath: " + getMediaImagePath(imageUri));

                ProfileImageHolder.imageFile = new File(getMediaImagePath(imageUri));
                profileImageLoader.saveImageToParse();
                EventBus.getDefault().post(new MessageUpdateProfilePicture(getMediaImagePath(imageUri)));
            }
            //cover photo
            if(requestCode == Constants.REQUEST_CHOOSE_COVER_PHOTO){
                Uri coverUri = data.getData();

                ProfileImageHolder.profileCoverPhotoFile = new File(getMediaImagePath(coverUri));
                mProfileCoverPhotoLoader.saveCoverPhotoToParse();
                EventBus.getDefault().post(new MessageUpdateCoverPhoto(getMediaImagePath(coverUri)));
            }

            if (requestCode == Constants.REQUEST_CHOOSE_VIDEO) {
                Log.d(LOG + " onActivityResult", "Video chosen with path: " + ProfileImageHolder.imageFile.getAbsolutePath());
                if (data == null) {
                    Log.d(LOG, "Data is null");
                } else {
                    Uri mediaUri = data.getData();
                    Log.d(LOG, "MediaUri: " + mediaUri);

                    int fileSize = 0;
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mediaUri);
                        fileSize = inputStream.available();
                    } catch (Exception e) {
                        Utilities.showDialog(ProfilePrivate_act.this, "Error reading file... " + " " + e.getMessage());
                        e.printStackTrace();
                        return;
                    } finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (fileSize >= Constants.FILE_SIZE_LIMIT) {
                        Utilities.showDialog(ProfilePrivate_act.this, "The selected file is too large. Select a new file");
                        return;
                    }

                    //picasso.load(mediaUri).fit().memoryPolicy(MemoryPolicy.NO_CACHE).into(aQuery.id(R.id.profile_picture).getImageView());
                    //Copy Uri contents into temp imageFile.
                    File videoFile = new File(getMediaImagePath(mediaUri));
                    //saveCoverPhotoToParse();
                }
            }

        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == Constants.REQUEST_IMAGE_CAPTURE || requestCode == Constants.REQUEST_CHOOSE_PICTURE) {

                if (ProfileImageHolder.imageFile != null && ProfileImageHolder.imageFile.exists()) {
                    Log.d(LOG, "Camera request canceled and file exists: " + ProfileImageHolder.imageFile.exists());
                    ProfileImageHolder.imageFile.delete();

                    if (ProfileImageHolder.imageFile == null) {
                        Log.d(LOG, "Camera request canceled and file deleted: " + true);
                    } else {
                        Log.d(LOG, "Camera request canceled and file deleted: " + false);
                    }
                }
            }
        }

    }

    /**
     * http://stackoverflow.com/questions/20067508/get-real-path-from-uri-android-kitkat-new-storage-access-framework?lq=1
     * Gets the path from the URI
     * This is needed since i have to read the bytes from the file and store it in Parse
     */
    public String getMediaImagePath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        Log.d(LOG, "Document id: " + document_id);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        if (path == null) {
            return null;
        }
        Log.d(LOG, "Path: " + path);
        cursor.close();

        return path;
    }

    /**
     * http://stackoverflow.com/questions/20067508/get-real-path-from-uri-android-kitkat-new-storage-access-framework?lq=1
     * Gets the path from the URI
     * This is needed since i have to read the bytes from the file and store it in Parse
     */
    public String getMediaVideoPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        Log.d(LOG, "Document id: " + document_id);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Video.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        if (path == null) {
            return null;
        }
        Log.d(LOG, "Path: " + path);
        cursor.close();

        return path;
    }

    /**
     * method is protected by a condition that calls resolveActivity(), which returns the first activity
     * component that can handle the intent. Performing this check is important because if you call startActivityForResult()
     * using an intent that no app can handle, your app will crash. So as long as the result is not null, it's safe to use the intent
     */
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //ACTION_IMAGE_CAPTURE

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                //photoFile = getOutputMediaFile(Constants.MEDIA_TYPE_IMAGE);
                photoFile = profileImageLoader.getOutputMediaFile(Constants.MEDIA_TYPE_IMAGE);

            } catch (IOException ex) {
                // Error occurred while creating the File
                Utilities.showDialog(ProfilePrivate_act.this, "Error loading imageFile" + " " + ex.getMessage());
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                //place photo in file
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                Log.d(LOG, photoFile.getAbsolutePath());

                startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);

            } else {
                Utilities.showDialog(ProfilePrivate_act.this, "There was a problem accessing your device external storage");
            }
        }
    }

    public void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE); //ACTION_IMAGE_CAPTURE
        // Ensure that there's a camera activity to handle the intent
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File videoFile = null;
            try {
                videoFile = profileImageLoader.getOutputMediaFile(Constants.MEDIA_TYPE_VIDEO);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Utilities.showDialog(ProfilePrivate_act.this, "Error loading imageFile" + " " + ex.getMessage());
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (videoFile != null) {
                //place photo in file
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                Log.d(LOG, videoFile.getAbsolutePath());

                startActivityForResult(takeVideoIntent, Constants.REQUEST_IMAGE_CAPTURE);
            } else {
                Utilities.showDialog(ProfilePrivate_act.this, "There was a problem accessing your device external storage");
            }
        }
    }

    /**
     * Opens the gallery
     */
    private void choosePictureFromGallery() {
        Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        choosePictureIntent.setType("image/*");
        //The data that is returned by the result is the path to the image file
        startActivityForResult(choosePictureIntent, Constants.REQUEST_CHOOSE_PICTURE);
    }

    private void chooseCoverFromGallery() {
        Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        choosePictureIntent.setType("image/*");
        //The data that is returned by the result is the path to the image file
        startActivityForResult(choosePictureIntent, Constants.REQUEST_CHOOSE_COVER_PHOTO);
    }
    private void chooseVideoFromGallery() {
        Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseVideoIntent.setType("video/*");
        //The data that is returned by the result is the path to the image file
        startActivityForResult(chooseVideoIntent, Constants.REQUEST_CHOOSE_VIDEO);
    }

    /**
     * -------------------------------------------------------------------------------------------------------------------------------------------------
     * Window Transitions
     */
    private void makeWindowTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(makeEnterTransition());
            getWindow().setExitTransition(makeExitTransition());
            getWindow().setReenterTransition(makeReenterTransition());
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        }
    }

    private Transition makeEnterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TransitionSet enterTransition = new TransitionSet();

            Transition t = new Slide(Gravity.TOP).setDuration(600);
            enterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
            enterTransition.excludeTarget(android.R.id.statusBarBackground, true);
            enterTransition.excludeTarget(R.id.toolbar_teal, true);
            enterTransition.addTransition(t);

            Transition tt = new Fade();
            enterTransition.addTransition(tt).setDuration(1000);
            return enterTransition;
        } else return null;
    }

    private Transition makeExitTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TransitionSet exitTransition = new TransitionSet();

            exitTransition.excludeTarget(android.R.id.navigationBarBackground, true);
            exitTransition.excludeTarget(android.R.id.statusBarBackground, true);
            exitTransition.excludeTarget(R.id.toolbar_teal, true);
            Transition fade = new Fade();
            exitTransition.addTransition(fade);

            exitTransition.setDuration(200);
            return exitTransition;
        } else return null;
    }

    private Transition makeReenterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionSet reenterTransition = new TransitionSet();
            reenterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
            reenterTransition.excludeTarget(android.R.id.statusBarBackground, true);
            reenterTransition.excludeTarget(R.id.appbar, true);

            Transition slideInFromLeft = new Slide(Gravity.LEFT);

            reenterTransition.addTransition(slideInFromLeft).setDuration(300);
            return reenterTransition;

        } else return null;
    }
}
