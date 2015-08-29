package com.mycompany.loginapp.general;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.mycompany.loginapp.R;
import com.mycompany.loginapp.login.SignIn_act;
import com.mycompany.loginapp.registration.Register_act;
import com.mycompany.loginapp.singletons.MySingleton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartupFragment extends Fragment implements View.OnClickListener {
    private Button mSignInButton, mSignUpButton;

    public static StartupFragment newInstance() {
        final StartupFragment fragment = new StartupFragment();

        return fragment;
    }

    public StartupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_startup, container, false);
        mSignInButton = (Button) view.findViewById(R.id.login);
        mSignInButton.setOnClickListener(this);
        mSignUpButton = (Button) view.findViewById(R.id.sign_up);
        mSignUpButton.setOnClickListener(this);
        // Inflate the layout for this fragment
        final ImageView mImageView = (ImageView) view.findViewById(R.id.background_image);
        MySingleton.getMySingleton().getPicasso().load(R.drawable.couple_hiking_valley).centerCrop().fit().into(mImageView);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.login){
            startActivity(new Intent(getActivity(), SignIn_act.class));
        }
        if(v.getId() == R.id.sign_up){
            startActivity(new Intent(getActivity(), Register_act.class));
        }
    }
}
