package com.mycompany.loginapp.registration.textWatchers;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Alexander on 14-06-2015.
 */
public class UsernameTextWatcher implements TextWatcher {

    private final TextInputLayout mTextInputLayout;
    private boolean hasError = true;

    public UsernameTextWatcher(TextInputLayout textInputLayout) {
        this.mTextInputLayout = textInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        updateTextChanged();
    }

    private void updateTextChanged() {
        final int length = mTextInputLayout.getEditText().length();
        if(length <= 0){
            mTextInputLayout.setError("A username is required");
            hasError = true;
            return;
        }

        if(length > 12){
            mTextInputLayout.setError("Username must not be longer than 12 characters");
            hasError = true;
            return;
        }

        if(length > 0 && length <= 12){
            mTextInputLayout.setError(null);
            hasError = false;
        }

    }

    public boolean hasError(){
        return hasError;
    }

    public EditText getEditText(){
        return mTextInputLayout.getEditText();
    }
}
