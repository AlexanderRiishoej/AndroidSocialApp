package com.mycompany.loginapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mycompany.loginapp.R;

import java.util.Arrays;
import java.util.List;

/**
 * Simple adapter example for custom items in the dialog
 */
public class MaterialDialogCustomAdapter extends BaseAdapter implements View.OnClickListener {

    private Toast mToast;
    private final Context mContext;
    private final List<String> mItems;
    private final List<Integer> mIcons = Arrays.asList(R.drawable.ic_photo_camera_grey600_24dp, R.drawable.ic_image_grey600_24dp);

//    public MaterialDialogCustomAdapter(Context context, @ArrayRes List<String> arrayResId) {
//        this(context, arrayResId);
//    }

    public MaterialDialogCustomAdapter(Context context, List<String> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public String getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.material_dialog_customlistitem, null);
        }
        final TextView textView = (TextView) convertView.findViewById(R.id.title);
        textView.setText(mItems.get(position));
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.dialog_image);
        imageView.setBackground(mContext.getResources().getDrawable(mIcons.get(position)));
//        Button button = (Button) convertView.findViewById(R.id.button);
//        button.setTag(position);
//        button.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        Integer index = (Integer) v.getTag();
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(mContext, "Clicked button " + index, Toast.LENGTH_SHORT);
        mToast.show();
    }
}

