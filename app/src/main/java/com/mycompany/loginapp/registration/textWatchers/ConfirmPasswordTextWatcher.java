package com.mycompany.loginapp.registration.textWatchers;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Alexander on 14-06-2015.
 */
public class ConfirmPasswordTextWatcher implements TextWatcher {

    private final TextInputLayout mTextInputConfirmPassword;
    private final TextInputLayout mTextInputPassword;
    private boolean hasError = true;

    public ConfirmPasswordTextWatcher(TextInputLayout textInputConfirmPassword, TextInputLayout textInputPassword) {
        this.mTextInputConfirmPassword = textInputConfirmPassword;
        this.mTextInputPassword = textInputPassword;
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
        if (mTextInputPassword.getEditText().getText().equals(mTextInputConfirmPassword.getEditText().getText())) {
            hasError = true;
        }
        hasError = false;
    }

    public boolean hasError() {
        return hasError;
    }
}
