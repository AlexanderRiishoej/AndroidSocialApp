package com.mycompany.loginapp.profile;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.adapters.MaterialDialogCustomAdapter;
import com.mycompany.loginapp.base.BaseActivity;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.eventMessages.MessageFinishActivities;
import com.mycompany.loginapp.eventMessages.MessageUpdateCoverPhoto;
import com.mycompany.loginapp.eventMessages.MessageUpdateProfilePicture;
import com.mycompany.loginapp.fragmentFactory.FragmentFactory;
import com.mycompany.loginapp.helperClasses.ProfileHelperClass;
import com.mycompany.loginapp.helperClasses.SnackBarHelperClass;
import com.mycompany.loginapp.profile.editProfile.EditProfileDialogHelper;
import com.mycompany.loginapp.profile.imageLoaders.IPhotoLoader;
import com.mycompany.loginapp.profile.imageLoaders.ImageFileCreator;
import com.mycompany.loginapp.profile.imageLoaders.abstractFactoryPattern.PhotoLoaderFactory;
import com.mycompany.loginapp.profile.imageLoaders.abstractFactoryPattern.PhotoLoaderFactoryCreator;
import com.mycompany.loginapp.utilities.Utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

//@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ProfilePrivate_act extends BaseActivity {
    public static final String LOG = ProfilePrivate_act.class.getSimpleName();
    private IPhotoLoader mProfilePictureImageLoader;
    private IPhotoLoader mProfileCoverPhotoLoader;
    private Fragment mPrivateProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProfileHelperClass.setUserOnline();
        EventBus.getDefault().register(this);
        mPrivateProfileFragment = FragmentFactory.getFragmentFactory().getFragment(PrivateProfileFragment.class.getSimpleName());
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, mPrivateProfileFragment)
                    .commit();
        }

        PhotoLoaderFactoryCreator photoLoaderFactoryCreator = PhotoLoaderFactory.getPhotoLoaderFactory();

        mProfilePictureImageLoader = photoLoaderFactoryCreator.getPhotoLoaderInstance("ProfilePhotoLoader", this);
        //mProfilePictureImageLoader.loadImage();

        mProfileCoverPhotoLoader = photoLoaderFactoryCreator.getPhotoLoaderInstance("CoverPhotoLoader", this);
        //mProfileCoverPhotoLoader.loadImage();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_private_profile;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_private_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.edit_name:
                EditProfileDialogHelper.showEditNameDialog(this, ((PrivateProfileFragment) mPrivateProfileFragment).getNameTextView());
                return true;
//            case R.id.action_chat:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    startActivity(new Intent(ProfilePrivate_act.this, Social_act.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//                } else {
//                    startActivity(new Intent(ProfilePrivate_act.this, Social_act.class));
//                }
//                return true;
            case R.id.action_pictures:
                final MaterialDialogCustomAdapter profilePhotoAdapter = new MaterialDialogCustomAdapter(this, Arrays.asList("Take picture", "Choose profile photo"));
                new MaterialDialog.Builder(this)
                        .title("Profile photo")
                        .adapter(profilePhotoAdapter, new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                String item = profilePhotoAdapter.getItem(which);
                                switch (which) {
                                    case 0:
                                        dispatchTakePictureIntent(Constants.REQUEST_PROFILE_IMAGE_CAPTURE);
                                        break;
                                    case 1:
                                        choosePhotoFromGallery(Constants.REQUEST_CHOOSE_PROFILE_PICTURE);
                                        break;
                                }
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
//            case R.id.action_video:
//                dispatchTakeVideoIntent();
//                return true;
//            case R.id.action_edit_profile:
//                startActivity(new Intent(this, ProfilePublic_act.class));
//                return true;
            case R.id.action_cover_photo:
                final MaterialDialogCustomAdapter coverPhotoAdapter = new MaterialDialogCustomAdapter(this, Arrays.asList("Take picture", "Choose cover photo"));
                new MaterialDialog.Builder(this)
                        .title("Cover photo")
                        .adapter(coverPhotoAdapter, new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                String item = coverPhotoAdapter.getItem(which);
                                switch (which) {
                                    case 0:
                                        dispatchTakePictureIntent(Constants.REQUEST_COVER_IMAGE_CAPTURE);
                                        break;
                                    case 1:
                                        choosePhotoFromGallery(Constants.REQUEST_CHOOSE_COVER_PHOTO);
                                        break;
                                }
                                dialog.dismiss();
                            }
                        })
                        .show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        Log.d(LOG, "is destroyed.");
        super.onDestroy();
    }

    /**
     * Finishes this activity
     */
    public void onEvent(MessageFinishActivities event) {
        Log.d(LOG, "FINISHING USER_ACTIVITY");
        this.finish();
    }

    /**
     * Event received when a new profile_image picture has been chosen
     * ProfilePrivate starts this event from the OnActivityResult() and from within ProfilePhotoLoader
     */
    public void onEvent(MessageUpdateProfilePicture newProfilePictureEvent) {
        ((PrivateProfileFragment) mPrivateProfileFragment).updateProfilePicture();
    }

// http://developer.android.com/training/camera/photobasics.html - camera intent

    /**
     * Result received by the camera intent containing the path to the image file
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            // If its a picture taken from the camera
            if (requestCode == Constants.REQUEST_PROFILE_IMAGE_CAPTURE) {
                Log.d(LOG + " onActivityResult", "Photo taken with path: " + ProfileImageHolder.mProfilePhotoFile.getAbsolutePath());
                mProfilePictureImageLoader.saveImageToParse();
                mProfilePictureImageLoader.updateImageDirectory();
                EventBus.getDefault().post(new MessageUpdateProfilePicture(ProfileImageHolder.mProfilePhotoFile.getAbsolutePath()));
            }
            if (requestCode == Constants.REQUEST_COVER_IMAGE_CAPTURE) {
                mProfileCoverPhotoLoader.saveImageToParse();
                mProfileCoverPhotoLoader.updateImageDirectory();
                EventBus.getDefault().post(new MessageUpdateCoverPhoto(ProfileImageHolder.mProfileCoverPhotoFile.getAbsolutePath()));
            }
            // If is a picture taken from the gallery
            if (requestCode == Constants.REQUEST_CHOOSE_PROFILE_PICTURE) {
                /** Get the Uri for the image chosen */
                final Uri imageUri = data.getData();
                final String path = FileUtils.getPath(this, imageUri);
                /** Populate image */
                //Log.d(LOG, "MediaImagePath: " + getMediaImagePath(imageUri));
                if (path != null && FileUtils.isLocal(path)) {
                    //ProfileImageHolder.mProfilePhotoFile = new File(getMediaImagePath(imageUri));
                    ProfileImageHolder.mProfilePhotoFile = new File(path);
                    mProfilePictureImageLoader.saveImageToParse();
                    EventBus.getDefault().post(new MessageUpdateProfilePicture(path));
                } else {
                    SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(this, R.id.fragment_profile_main_layout), "Couldn't find image", Snackbar.LENGTH_SHORT);
                }
            }
            //cover photo
            if (requestCode == Constants.REQUEST_CHOOSE_COVER_PHOTO) {
                final Uri coverUri = data.getData();
                // https://github.com/iPaulPro/aFileChooser
                final String path = FileUtils.getPath(this, coverUri);

                if (path != null && FileUtils.isLocal(path)) {
                    ProfileImageHolder.mProfileCoverPhotoFile = new File(path);
                    mProfileCoverPhotoLoader.saveImageToParse();
                    EventBus.getDefault().post(new MessageUpdateCoverPhoto(path));
                } else {
                    SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(this, R.id.fragment_profile_main_layout), "Couldn't find image", Snackbar.LENGTH_SHORT);
                }
            }

            if (requestCode == Constants.REQUEST_CHOOSE_VIDEO) {
                Log.d(LOG + " onActivityResult", "Video chosen with path: " + ProfileImageHolder.mProfilePhotoFile.getAbsolutePath());
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
                    //Copy Uri contents into temp mProfilePhotoFile.
                    final String videoPath = FileUtils.getPath(this, mediaUri);
                    if(videoPath != null) {
                    File videoFile = new File(videoPath);
                    }
                    //saveCoverPhotoToParse();
                }
            }

        } else if (resultCode == RESULT_CANCELED) {
//            if (requestCode == Constants.REQUEST_PROFILE_IMAGE_CAPTURE || requestCode == Constants.REQUEST_CHOOSE_PROFILE_PICTURE) {
//
//                if (ProfileImageHolder.mProfilePhotoFile != null && ProfileImageHolder.mProfilePhotoFile.exists()) {
//                    Log.d(LOG, "Camera request canceled and file exists: " + ProfileImageHolder.mProfilePhotoFile.exists());
//                    ProfileImageHolder.mProfilePhotoFile.delete();
//
//                    if (ProfileImageHolder.mProfilePhotoFile == null) {
//                        Log.d(LOG, "Camera request canceled and file deleted: " + true);
//                    } else {
//                        Log.d(LOG, "Camera request canceled and file deleted: " + false);
//                    }
//                }
//            }
        }

    }

    /**
     * method is protected by a condition that calls resolveActivity(), which returns the first activity
     * component that can handle the intent. Performing this check is important because if you call startActivityForResult()
     * using an intent that no app can handle, your app will crash. So as long as the result is not null, it's safe to use the intent
     */
    public void dispatchTakePictureIntent(final int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //ACTION_IMAGE_CAPTURE

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                if(requestCode == Constants.REQUEST_PROFILE_IMAGE_CAPTURE || requestCode == Constants.REQUEST_CHOOSE_PROFILE_PICTURE) {
                    photoFile = ImageFileCreator.getOutputMediaFile();
                    ProfileImageHolder.mProfilePhotoFile = photoFile;
                }
                else if (requestCode == Constants.REQUEST_COVER_IMAGE_CAPTURE || requestCode == Constants.REQUEST_CHOOSE_COVER_PHOTO){
                    photoFile = ImageFileCreator.getOutputMediaFile();
                    ProfileImageHolder.mProfileCoverPhotoFile = photoFile;
                }
                    //photoFile = getOutputMediaFile(Constants.MEDIA_TYPE_IMAGE);

            } catch (Exception ex) {
                // Error occurred while creating the File
                SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(this, R.id.fragment_profile_main_layout), "Error occurred while taking picture", Snackbar.LENGTH_LONG);
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                //place photo in file
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                Log.d(LOG, photoFile.getAbsolutePath());

                startActivityForResult(takePictureIntent, requestCode);

            } else {
                SnackBarHelperClass.showBasicSnackBar(ButterKnife.findById(this, R.id.fragment_profile_main_layout), "Error occurred while taking picture", Snackbar.LENGTH_LONG);
            }
        }
    }

    private void choosePhotoFromGallery(final int requestCode) {
        if (Build.VERSION.SDK_INT < 19) {
//            Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
//            choosePictureIntent.setType("image/*");
//            startActivityForResult(Intent.createChooser(choosePictureIntent, "Select Picture"), Constants.REQUEST_CHOOSE_COVER_PHOTO);
            // Use the GET_CONTENT intent from the utility class
            Intent target = FileUtils.createGetContentIntent();
            // Create the chooser Intent
            Intent intent = Intent.createChooser(
                    target, "Select file");
            try {
                startActivityForResult(intent, requestCode);
            } catch (ActivityNotFoundException e) {
                // The reason for the existence of aFileChooser
            }
        } else {
            Intent choosePictureIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            //Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
            //choosePictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
            choosePictureIntent.setType("image/*");
            //The data that is returned by the result is the path to the image file
            startActivityForResult(choosePictureIntent, requestCode);
        }
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
