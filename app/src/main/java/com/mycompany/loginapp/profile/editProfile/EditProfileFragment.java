package com.mycompany.loginapp.profile.editProfile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.constants.ParseConstants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment implements View.OnTouchListener {
    private LinearLayout mChangeUsernameLinearLayout, mChangeNameLinearLayout, mChangePhoneNumberLinearLayout;
    private TextView mUsernameTextView, mNameTextView;

    public static EditProfileFragment newInstance() {
        EditProfileFragment fragment = new EditProfileFragment();

        return fragment;
    }

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        this.initializeLayout(view);
        this.setupOnTouchListeners();
//        this.initializeSwipeRefreshLayout(view);
//        this.initializeRecyclerView(view);

        // Inflate the layout for this fragment
        return view;
    }

    private void initializeLayout(View view){
        mChangeUsernameLinearLayout = (LinearLayout) view.findViewById(R.id.change_username_linear_layout);
        mUsernameTextView = (TextView) mChangeUsernameLinearLayout.findViewById(R.id.edit_username);
        mUsernameTextView.setText(ApplicationMain.mCurrentParseUser.getString(ParseConstants.USERNAME));

        mChangeNameLinearLayout = (LinearLayout) view.findViewById(R.id.change_name_linear_layout);
        mNameTextView= (TextView) mChangeNameLinearLayout.findViewById(R.id.edit_name);
        mNameTextView.setText(ApplicationMain.mCurrentParseUser.getString("fullName"));
    }

    private void setupOnTouchListeners(){
        mChangeUsernameLinearLayout.setOnTouchListener(this);
        mChangeNameLinearLayout.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        view.onTouchEvent(event);
        final EditText mUsername;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (view.getId()) {
                case R.id.change_username_linear_layout: // Id of the button
                    EditProfileDialogHelper.showEditUsernameDialog(getActivity(), mUsernameTextView);
                    break;

                case R.id.change_name_linear_layout: // Id of the button
                    EditProfileDialogHelper.showEditNameDialog(getActivity(), mNameTextView);
                    break;

                default:
                    break;
            }
        }

        return false;
    }
}
