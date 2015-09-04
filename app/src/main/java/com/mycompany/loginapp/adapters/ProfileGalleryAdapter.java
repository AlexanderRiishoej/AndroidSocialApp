package com.mycompany.loginapp.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.profile.ProfileImageHolder;
import com.mycompany.loginapp.singletons.MySingleton;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 19-07-2015.
 */
public class ProfileGalleryAdapter extends RecyclerView.Adapter<ProfileGalleryAdapter.MyImageViewHolder> {
    private Context actContext;
    private LayoutInflater layoutInflater;
    private List<ParseObject> mParseObjectList;
    public List<File> mImagePaths;
    public List<String> mImageUrls;

    public ProfileGalleryAdapter(Context actContext, List<ParseObject> parseObjectList){
        this.actContext = actContext;
        this.layoutInflater = LayoutInflater.from(actContext);
        this.mParseObjectList = parseObjectList;
        this.mImagePaths = new ArrayList<>();
        this.mImageUrls = new ArrayList<>();
    }

    /** For loading the images of the current user */
    public void addAllFiles(){
        for(int i = 0; i < 20; i++){
            if(i == 2  | i == 4 | i == 6){
                mImagePaths.add(i, ProfileImageHolder.profileCoverPhotoFile);
            }
            else {
                mImagePaths.add(i, ProfileImageHolder.imageFile);
            }
        }
    }

    /** For loading urls of public users */
    public void addAllUrls(ParseUser mPublicParseUser){
        for(int i = 0; i < 20; i++){
            if(i == 2  | i == 4 | i == 6){
                final ParseFile parseFile = mPublicParseUser.getParseFile(ParseConstants.PROFILE_PICTURE);
                if(parseFile != null) {
                    mImageUrls.add(i, parseFile.getUrl());
                }
                else {
                    mImageUrls.add(i, null);
                }
            }
            else {
                final ParseFile parseFile = mPublicParseUser.getParseFile(ParseConstants.COVER_PHOTO);
                if(parseFile != null) {
                    mImageUrls.add(i, parseFile.getUrl());
                }
                else {
                    mImageUrls.add(i, null);
                }
            }
        }
    }

    @Override
    public MyImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final MyImageViewHolder myImageViewHolder;
        View convertView = layoutInflater.inflate(R.layout.private_profile_gallery_image, parent, false); //Inflating the layout
        myImageViewHolder = new MyImageViewHolder(convertView, actContext);

        return myImageViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyImageViewHolder holder, int position) {
        //loading images for the PrivateProfileFragment
        if(mImagePaths != null && mImagePaths.size() > 0) {
            MySingleton.getMySingleton().getPicasso().load(mImagePaths.get(position)).centerCrop().fit().into(holder.mImageView);
        }
        //loading images for the PublicProfileFragment
        else if(mImageUrls != null && mImageUrls.size() > 0) {
            holder.mImageView.setImageBitmap(null);
            holder.mImageView.setImageDrawable(null);
            if(mImageUrls.get(position) != null) {
                MySingleton.getMySingleton().getPicasso().load(mImageUrls.get(position)).centerCrop().fit().into(holder.mImageView);
                holder.mImageView.setColorFilter(null);
            }
            else {
                MySingleton.getMySingleton().getPicasso().load(R.drawable.com_facebook_profile_picture_blank_portrait).centerCrop().fit().into(holder.mImageView);
                final int color = ApplicationMain.getAppContext().getResources().getColor(R.color.teal_500);
                holder.mImageView.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public static class MyImageViewHolder extends RecyclerView.ViewHolder {
        final private Context mActivityContext;
        public ImageView mImageView;

        public MyImageViewHolder(View itemView, Context activityContext) {
            super(itemView);
            this.mActivityContext = activityContext;
            mImageView = (ImageView) itemView.findViewById(R.id.gallery_image);
        }
    }
}
