package com.mycompany.loginapp.profile.editProfile;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mycompany.loginapp.R;
import com.mycompany.loginapp.base.ApplicationMain;
import com.mycompany.loginapp.constants.ParseConstants;
import com.mycompany.loginapp.profile.MyCustomTextWatcher;
import com.parse.ParseException;
import com.parse.SaveCallback;

import de.greenrobot.event.EventBus;

/**
 * Created by Alexander on 22-08-2015.
 * Material dialog helper class that handles the initialisation and showing of EditProfileFragments dialogs
 * Also performs the queries to parse and sends the EventBus message containing updated data, in the OnPositive callback of the dialog
 */
public class EditProfileDialogHelper {

    public EditProfileDialogHelper(){
    }

    public static void showEditUsernameDialog(Context mActivityContext, final TextView mUsernameTextView){
        final EditText mUsername;
        final TextView mHeaderTextView;
        final View mPositiveAction;
        MaterialDialog mUsernameDialog = new MaterialDialog.Builder(mActivityContext)
                .title("Edit name")
                .customView(R.layout.material_dialog_generic_view, true)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .positiveText("Save")
                .negativeText("Cancel")
                .alwaysCallInputCallback() // this forces the callback to be invoked with every input change
                .build();

        mPositiveAction = mUsernameDialog.getActionButton(DialogAction.POSITIVE);
        //noinspection ConstantConditions
        mHeaderTextView = (TextView) mUsernameDialog.getCustomView().findViewById(R.id.header_description);
        mHeaderTextView.setText("Username");
        mUsername = (EditText) mUsernameDialog.getCustomView().findViewById(R.id.edit_text_content);
        mUsername.addTextChangedListener(new MyCustomTextWatcher(mPositiveAction));
        mUsernameDialog.getBuilder().callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                ApplicationMain.mCurrentParseUser.put(ParseConstants.USERNAME, mUsername.getText().toString());
                ApplicationMain.mCurrentParseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            EventBus.getDefault().post(new MessageUpdateUsername(mUsername.getText().toString()));
                            mUsernameTextView.setText(ApplicationMain.mCurrentParseUser.getString(ParseConstants.USERNAME));
                        }
                        else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        mUsernameDialog.show();
        mPositiveAction.setEnabled(false);
    }

    public static void showEditNameDialog(Context mActivityContext, final TextView mNameTextView){
        final EditText mName;
        final TextView mHeaderTextView;
        final View mPositiveAction;
        MaterialDialog mUsernameDialog = new MaterialDialog.Builder(mActivityContext)
                .title("Edit name")
                .customView(R.layout.material_dialog_generic_view, true)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .positiveText("Save")
                .negativeText("Cancel")
                .alwaysCallInputCallback() // this forces the callback to be invoked with every input change
                .build();

        mPositiveAction = mUsernameDialog.getActionButton(DialogAction.POSITIVE);
        //noinspection ConstantConditions
        mHeaderTextView = (TextView) mUsernameDialog.getCustomView().findViewById(R.id.header_description);
        mHeaderTextView.setText("Name");
        mName = (EditText) mUsernameDialog.getCustomView().findViewById(R.id.edit_text_content);
        mName.addTextChangedListener(new MyCustomTextWatcher(mPositiveAction));
        mUsernameDialog.getBuilder().callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                ApplicationMain.mCurrentParseUser.put("fullName", mName.getText().toString());
                ApplicationMain.mCurrentParseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            EventBus.getDefault().post(new MessageUpdateName(mName.getText().toString()));
                            mNameTextView.setText(ApplicationMain.mCurrentParseUser.getString("fullName"));
                        }
                        else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        mUsernameDialog.show();
        mPositiveAction.setEnabled(false);
    }
}
