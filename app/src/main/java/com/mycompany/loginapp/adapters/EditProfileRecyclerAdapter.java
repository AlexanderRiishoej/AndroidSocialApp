package com.mycompany.loginapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.constants.Constants;

/**
 * Created by Alexander on 11-08-2015.
 */
public class EditProfileRecyclerAdapter extends RecyclerView.Adapter<EditProfileRecyclerAdapter.EditProfileViewHolder> {
    private final Context mActivityContext;
    private final LayoutInflater mLayoutInflater;

    public EditProfileRecyclerAdapter(Context mActivityContext){
        this.mActivityContext = mActivityContext;
        this.mLayoutInflater = LayoutInflater.from(mActivityContext);
    }

    @Override
    public EditProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final EditProfileViewHolder mEditProfileViewHolder;
        if(viewType == Constants.TYPE_HEADER) {
            View view = mLayoutInflater.inflate(R.layout.edit_profile_header, parent, false);
            mEditProfileViewHolder = new EditProfileViewHolder(view, viewType);
        }
        else {
            View view = mLayoutInflater.inflate(R.layout.edit_profile_item, parent, false);
            mEditProfileViewHolder = new EditProfileViewHolder(view, viewType);
        }

        return mEditProfileViewHolder;
    }

    @Override
    public void onBindViewHolder(final EditProfileViewHolder holder, int position) {
        if(getItemViewType(Constants.TYPE_HEADER) == 0) {

        }
        else {
            holder.mFirstTextView.setText("Name");
            holder.mSecondTextView.setText("Alexander");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 || position == 4){
            return Constants.TYPE_HEADER;
        }
        return Constants.TYPE_SECOND_ITEM;
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public static class EditProfileViewHolder extends RecyclerView.ViewHolder {
        public TextView mFirstTextView;
        public TextView mSecondTextView;

        public EditProfileViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == Constants.TYPE_HEADER){

            }
            else {
                mFirstTextView = (TextView) itemView.findViewById(R.id.edit_profile_first_text_view);
                mSecondTextView = (TextView) itemView.findViewById(R.id.edit_profile_second_text_view);
            }
        }
    }
}
