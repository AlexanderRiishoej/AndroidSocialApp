package com.mycompany.loginapp.registration.textWatchers;

import android.app.FragmentManager;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.mycompany.loginapp.registration.Register_act;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

/**
 * Created by Alexander on 14-06-2015.
 */
public class BirthdayTextWatcher implements TextWatcher,
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private final TextInputLayout mTextInputLayout;
    private EditText mBirthdayEditText;
    final Context actContext;
    private boolean hasError = true;

    public BirthdayTextWatcher(TextInputLayout textInputLayout, Context actContext) {
        this.mTextInputLayout = textInputLayout;
        this.mBirthdayEditText = textInputLayout.getEditText();
        this.actContext = actContext;
        this.setOnClickListener();
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

    public void updateTextChanged() {
        final int length = mTextInputLayout.getEditText().length();
        if(length <= 0){
            mTextInputLayout.setError("A birthday is required");
            hasError = true;
            return;
        }
        if(length > 0){
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

    public TextInputLayout getTextInputLayout(){
        return mTextInputLayout;
    }

    private void setOnClickListener(){
        mBirthdayEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View editTextView, MotionEvent event) {
//                Snackbar.make(v, "This ia an example!", Snackbar.LENGTH_LONG)
//                        .setAction("Do something useful!", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                            }
//                        })
//                        .show(); // Dont forget to show!
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    editTextView.requestFocus();
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            BirthdayTextWatcher.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(((FragmentActivity)actContext).getFragmentManager(), "Datepickerdialog");

                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String month = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;
        String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
        String date = "" + day + "/" + month + "/" + year;
        mBirthdayEditText.setText(date);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String time = "You picked the following time: " + hourString + "h" + minuteString;
        mBirthdayEditText.setText(time);
    }
}
