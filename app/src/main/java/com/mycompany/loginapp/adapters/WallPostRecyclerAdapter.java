package com.mycompany.loginapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.chat.UserChatList_act;
import com.mycompany.loginapp.constants.ParseConstants;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Alexander on 24-04-2015.
 */
public class WallPostRecyclerAdapter extends RecyclerView.Adapter<WallPostRecyclerAdapter.MyNewsViewHolder> {
    public static final String LOG = UserChatList_act.class.getSimpleName();
    private static final int WALL_POST_HEADER = 0;
    private static final int WALL_POST = 1;
    private Context activityContext;
    private List<ParseObject> userChatList;
    private LayoutInflater layoutInflater;
    private String name = ParseUser.getCurrentUser().getUsername();
    private String profilePicturePath;
    private static AQuery aQuery;
    private Picasso picasso;
    private final Long calendarTime = Calendar.getInstance().getTimeInMillis();

    private String names[] = {"Alexander Riishøj", "Anne-Marie Christensen", "Lars Ulrik", "Søren Gade", "Rene Toft", "Elisabeth Hansen"};
    private String status[] = {"This is a status update. This is how it will look when somebody posts a message",
            "Hello everyone! I've had an awesome day today!", "People are stupid...", "Wooooooow that was an insane movie! Need to watch!!!",
            "Hehe, you're a funny guy aren't ya!", "..."};

    private int mIcons[] = {R.drawable.ic_local_phone_grey600_24dp, R.drawable.ic_person_grey600_24dp,
            R.drawable.ic_messenger_grey600_24dp, R.drawable.ic_chat_grey600_24dp, R.drawable.ic_action_email, R.drawable.ic_settings_applications_grey600_24dp};

    public WallPostRecyclerAdapter(Context actContext) {
        this.activityContext = actContext;
        this.layoutInflater = LayoutInflater.from(actContext);
        this.aQuery = new AQuery(actContext);
        this.picasso = Picasso.with(actContext);
    }

    @Override
    public WallPostRecyclerAdapter.MyNewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyNewsViewHolder myNewsViewHolder;
        //View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_list_items, parent, false); //Inflating the layout
        /** View layout to be inflated onto the Recycler */
        if (viewType == 0) {
            View convertView = layoutInflater.inflate(R.layout.wallpost_header, parent, false); //Inflating the layout
            myNewsViewHolder = new MyNewsViewHolder(convertView); //Creating ViewHolder and passing the object of type view

            return myNewsViewHolder; // Returning the created object
        } else {
            View convertView = layoutInflater.inflate(R.layout.wallpost, parent, false); //Inflating the layout
            myNewsViewHolder = new MyNewsViewHolder(convertView); //Creating ViewHolder and passing the object of type view

            return myNewsViewHolder; // Returning the created object
        }
    }

    @Override
    public void onBindViewHolder(MyNewsViewHolder myNewsViewHolder, int position) {                         // as the list view is going to be called after the navigation_header view so we decrement the
        // position by 1 and pass it to the holder while setting the text and image
        myNewsViewHolder.username.setText(names[position]); // Setting the Text with the array of our Titles
        myNewsViewHolder.body.setText(status[position]);
//        myNewsViewHolder.time.setText(DateUtils.getRelativeDateTimeString(activityContext, calendarTime,
//                DateUtils.SECOND_IN_MILLIS, DateUtils.MINUTE_IN_MILLIS, 0));
        myNewsViewHolder.time.setText(DateUtils.getRelativeTimeSpanString(calendarTime,
                Calendar.getInstance().getTimeInMillis(), DateUtils.SECOND_IN_MILLIS, 0));

        final MyNewsViewHolder finalViewHolder = myNewsViewHolder;
        ParseUser.getQuery().whereEqualTo(ParseConstants.USERNAME, ParseUser.getCurrentUser().getUsername()).getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    Picasso.with(activityContext).load("file:" + parseUser.getString(ParseConstants.PROFILE_PICTURE_PATH)).centerCrop().fit().into(finalViewHolder.profilePicture);

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void notifyItemsHasChanged() {
        this.notifyItemRangeChanged(0, names.length);
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? WALL_POST_HEADER : WALL_POST;
    }

    public static class MyNewsViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{
        public TextView body;
        public ImageView profilePicture;
        public TextView time;
        public TextView username;
        public Toolbar cardToolbar;
        public RelativeLayout relativeLayout;

        public TextView commentTextView;
        private GestureDetector gestureDetector;

        public MyNewsViewHolder(View itemView) {
            super(itemView);
            body = (TextView) itemView.findViewById(R.id.wall_post_body);
            time = (TextView) itemView.findViewById(R.id.wall_post_time);
            username = (TextView) itemView.findViewById(R.id.wall_post_username);
            profilePicture = (ImageView) itemView.findViewById(R.id.wall_post_profile_picture);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.comment_layout);
            cardToolbar = (Toolbar) itemView.findViewById(R.id.card_toolbar);
            cardToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    return false;
                }
            });
            cardToolbar.inflateMenu(R.menu.menu_main);

            commentTextView = (TextView)itemView.findViewById(R.id.textView_comment);
            gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

                public void onLongPress(MotionEvent e) {
                    Log.e("", "Longpress detected");
                    commentTextView.setTextColor(ApplicationMain.context.getResources().getColor(R.color.secondary_text_icons_light_theme));
                }

            });
            relativeLayout.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        commentTextView.setTextColor(ApplicationMain.context.getResources().getColor(R.color.teal_500));
                        return true;
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){{
                        commentTextView.setTextColor(ApplicationMain.context.getResources().getColor(R.color.secondary_text_icons_light_theme));
                        return true;
                    }}
                    else{
                        commentTextView.setTextColor(ApplicationMain.context.getResources().getColor(R.color.secondary_text_icons_light_theme));
                        //return gestureDetector.onTouchEvent(motionEvent);
                        //return gestureDetector.onTouchEvent(motionEvent);
                        return true;
                    }

                }
            });
            }


        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                commentTextView.setTextColor(ApplicationMain.context.getResources().getColor(R.color.teal_500));
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){{
                commentTextView.setTextColor(ApplicationMain.context.getResources().getColor(R.color.secondary_text_icons_light_theme));
                return true;
            }}
            else{
                commentTextView.setTextColor(ApplicationMain.context.getResources().getColor(R.color.teal_500));
                //return gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        }
    }
}
