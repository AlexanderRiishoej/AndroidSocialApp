package com.mycompany.loginapp.registration.textWatchers;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Alexander on 14-06-2015.
 */
public class MobilePhoneNumberTextWatcher implements TextWatcher {

    private final TextInputLayout mTextInputLayout;
    private boolean hasError = true;

    public MobilePhoneNumberTextWatcher(TextInputLayout textInputLayout) {
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
            mTextInputLayout.setError("A lastname is required");
            return;
        }
        if(length > 0){
            mTextInputLayout.setError(null);
        }

    }

    public boolean hasError(){
        return hasError;
    }

    public EditText getEditText(){
        return mTextInputLayout.getEditText();
    }
}
