package com.mycompany.loginapp.profile;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Alexander on 21-07-2015.
 */
public class MyCustomTextWatcher implements TextWatcher {
    final View mPositiveAction;

    public MyCustomTextWatcher(View mPositiveAction){
        this.mPositiveAction = mPositiveAction;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.mPositiveAction.setEnabled(s.toString().trim().length() > 0);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
