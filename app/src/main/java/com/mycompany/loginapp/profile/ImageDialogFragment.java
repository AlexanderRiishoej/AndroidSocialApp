package com.mycompany.loginapp.profile;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.eventMessages.MessageImageDialog;
import com.mycompany.loginapp.singletons.MySingleton;

import java.io.File;

import de.greenrobot.event.EventBus;

/**
 * Guide to DialogFragment
 * http://developer.android.com/reference/android/app/DialogFragment.html
 */
public class ImageDialogFragment extends DialogFragment {
    private ImageView mImageView;

    public static ImageDialogFragment newInstance() {
        ImageDialogFragment fragment = new ImageDialogFragment();
        return fragment;
    }

    public ImageDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(DialogFragment.STYLE_NO_TITLE, getTheme());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_image, container, false);
        // Inflate the layout for this fragment
        mImageView = (ImageView) view.findViewById(R.id.main_image);

        final File imageFile;
        final String imageUrl;

        //check sticky for null
        if(EventBus.getDefault().getStickyEvent(MessageImageDialog.class) != null) {

            //if the value posted is of type File
            if (EventBus.getDefault().getStickyEvent(MessageImageDialog.class).value instanceof File) {
                imageFile = (File) EventBus.getDefault().getStickyEvent(MessageImageDialog.class).value;

                if(imageFile != null) {
                    MySingleton.getMySingleton().getPicasso().load(imageFile).centerCrop().fit().into(mImageView);
                }
            }
            //else value must be of type String
            else {
                imageUrl = (String) EventBus.getDefault().getStickyEvent(MessageImageDialog.class).value;

                if (imageUrl != null) {
                    MySingleton.getMySingleton().getPicasso().load(imageUrl).centerCrop().fit().into(mImageView);
                }
            }
        }
        else { //else some error happened with posting the sticky event
            new MaterialDialog.Builder(getActivity())
                    .title("An error occurred")
                    .content("Error loading image")
                    .show();
        }
//        final File imageFile = EventBus.getDefault().getStickyEvent(MessageImageDialog.class) == null ? null : (File)EventBus.getDefault().getStickyEvent(MessageImageDialog.class).value;
//        final String imageUrl = EventBus.getDefault().getStickyEvent(MessageImageDialogUrl.class) == null ? null : EventBus.getDefault().getStickyEvent(MessageImageDialogUrl.class).mImageUrl;
//
//        if(imageFile != null) {
//            MySingleton.getMySingleton().getPicasso().load(imageFile).centerCrop().fit().into(mImageView);
//        }
//        else if (imageUrl != null) {
//            MySingleton.getMySingleton().getPicasso().load(imageUrl).centerCrop().fit().into(mImageView);
//        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
