package com.mycompany.loginapp.adapters;

/**
 * Created by Alexander on 24-03-2015.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.constants.Constants;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.profile.ProfileImageHolder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

/**
 * Created by hp1 on 28-12-2014.
 * Takes a ViewHolder as parameter which is the ViewHolder i create to hold the static references to the child views inflated
 */
public class NavigationRecyclerAdapter extends RecyclerView.Adapter<NavigationRecyclerAdapter.MyViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private Picasso picasso;
    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see
    private String mNavTitles[] = {"Profile", "News", "Chat", "New Chat", "Mail", "Settings"};
    private int mIcons[] = {R.drawable.ic_home_grey600_24dp, R.drawable.ic_person_grey600_24dp,
            R.drawable.ic_messenger_grey600_24dp, R.drawable.ic_chat_grey600_24dp, R.drawable.ic_action_email, R.drawable.ic_settings_applications_grey600_24dp};
    private String name = ParseUser.getCurrentUser().getUsername();        //String Resource for navigation_header View Name
    private String email = ParseUser.getCurrentUser().getEmail();

    /** Constructor of RecyclerAdapter */
    public NavigationRecyclerAdapter(Context actContext){
        this.context = actContext;
        this.layoutInflater = LayoutInflater.from(actContext);
        this.picasso = Picasso.with(actContext);
    }

    /* Number of the active activity equal to the position of the row clicked in ItemClick() */
    private int itemRowColor() {
        switch (context.getClass().getSimpleName()) {
            case Constants.PROFILE_PRIVATE:
                return 1;
            case Constants.NEWS:
                return 2;
            case Constants.USER_LIST_ACT_NAME:
                return 3;
            case Constants.NEW_USER_CHAT_ACT_NAME:
                return 4;
        }
        return 1;
    }

    /** Notifies the adapter to update the position where a new profile picture has been chosen */
    public void updateRecyclerItem(){
        this.notifyItemChanged(0);
    }


    /** Use the onCreateViewHolder to inflate your custom layout file with a LayoutInflater, and pass the root of this layout to your instance
     * of RecyclerView.ViewHolder so that it can call findViewById on all your child views for your root layout
     * In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate navigation_header if the viewType is TYPE_HEADER
     * nd pass it to the view holder*/
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder;

        if (viewType == Constants.TYPE_ITEM) {

            //View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_list_items, parent, false); //Inflating the layout
            /** View layout to be inflated onto the Recycler */
            View convertView = layoutInflater.inflate(R.layout.navigation_list_items, parent, false); //Inflating the layout
            viewHolder = new MyViewHolder(convertView, Constants.TYPE_ITEM); //Creating ViewHolder and passing the object of type view

            return viewHolder; // Returning the created object

        } else if (viewType == Constants.TYPE_HEADER) {

            /** View layout to be inflated onto the Recycler */
            View convertView = layoutInflater.inflate(R.layout.navigation_header, parent, false); //Inflating the layout
            viewHolder = new MyViewHolder(convertView, Constants.TYPE_HEADER); //Creating ViewHolder and passing the object of type view

            return viewHolder; //returning the object created
        }
        return null;

    }

    /** Use the onBindViewHolder to get the data at current position and update the child views inside your root layout instance.
     * Next we override a method which is called when the item in a row is needed to be displayed, here the int position
     * Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
     * which view type is being created 1 for item row and 0 for header
     * */
    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        if (viewHolder.Holderid == 1) {                              // as the list view is going to be called after the navigation_header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image
            if (itemRowColor() == position) {
                viewHolder.textView.setText(mNavTitles[position - 1]); // Setting the Text with the array of our Titles with color
                viewHolder.textView.setTextColor(context.getResources().getColor(R.color.teal_500)); // Setting the color to highlight the selected row
                viewHolder.imageView.setColorFilter(context.getResources().getColor(R.color.teal_500));// Setting the color to highlight the selected row
                Picasso.with(context).load(mIcons[position - 1]).into(viewHolder.imageView);// Setting the image with array of our icons
            } else {
                viewHolder.textView.setText(mNavTitles[position - 1]); // Setting the Text with the array of our Titles
                Picasso.with(context).load(mIcons[position - 1]).into(viewHolder.imageView);
            }
        } else {

            final MyViewHolder viewHolderFinal = viewHolder;
            /** If an event for a new profile picture has been received, this will not be null */
            if(ProfileImageHolder.imageFile != null && ProfileImageHolder.imageFile.exists()){
                picasso.load(ProfileImageHolder.imageFile).fit().noPlaceholder().into(viewHolderFinal.profile);
            }
            else {
                ParseUser.getQuery().whereEqualTo(ParseConstants.USERNAME, ParseUser.getCurrentUser().getUsername()).getFirstInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            picasso.load("file:" + parseUser.getString(ParseConstants.PROFILE_PICTURE_PATH)).fit().noPlaceholder().
                                    into(viewHolderFinal.profile);
                            Log.d("FILE FROM PARSE", parseUser.getString(ParseConstants.PROFILE_PICTURE_PATH));
                            Log.d("USER FROM PARSE", parseUser.getUsername());
//                        if (User_act.CurrentMediaFilePath != null) {
//                            Log.d("FILE", User_act.CurrentMediaFilePath);
//                        }
                            //Log.d("USER", ParseUser.getCurrentUser().getUsername());
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
            viewHolder.Name.setText(name);
            viewHolder.email.setText(email);
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mNavTitles.length + 1; // the number of items in the list will be +1 the titles including the navigation_header view.
    }


    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        return position == 0 ? Constants.TYPE_HEADER : Constants.TYPE_ITEM;
    }

    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        int Holderid;
        // List
        public TextView textView;
        public ImageView imageView;
        // Header
        public ImageView profile;
        public TextView Name;
        public TextView email;


        public MyViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created
            if (ViewType == Constants.TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from item_row.xml
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);// Creating ImageView object with the id of ImageView from item_row.xml
                Holderid = 1;                                               // setting holder id as 1 as the object being populated are of type item row
            } else {
                Name = (TextView) itemView.findViewById(R.id.name);         // Creating Text View object from navigation_header.xml_header.xml for name
                email = (TextView) itemView.findViewById(R.id.email);       // Creating Text View object from navigation_header.xml_header.xml for email
                profile = (ImageView) itemView.findViewById(R.id.circleView);// Creating Image view object from navigation_headertion_header.xml for profile pic
                Holderid = 0;                                                // Setting holder id = 0 as the object being populated are of type navigation_header view
            }
        }
    }
}
