package com.ppn.authandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ppn.authandroid.R;
import com.ppn.authandroid.Utils;
import com.ppn.authandroid.models.User;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignupActivity";
    EditText mEditTextFirstName,
            mEditTextLastName,
            mEditTextEmail,
            mEditTextPassword;
    private View mContentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mContentView = findViewById(R.id.content_view);

        mEditTextFirstName = findViewById(R.id.edit_first_name);
        mEditTextLastName = findViewById(R.id.edit_last_name);
        mEditTextEmail = findViewById(R.id.edit_email);
        mEditTextPassword = findViewById(R.id.edit_password);

    }


    @Override
    protected void onResume() {
        super.onResume();
        // ensureFullScreen();
    }

    private void ensureFullScreen() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_signup:
                handleSignup();
                break;

            case R.id.text_cancel:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void handleSignup() {
        View focusView = null;
        User user = new User();

        user.firstName = mEditTextFirstName.getText().toString().trim();
        user.lastName = mEditTextLastName.getText().toString().trim();
        user.email = mEditTextEmail.getText().toString().trim();
        user.password = mEditTextPassword.getText().toString().trim();

        if (focusView == null && TextUtils.isEmpty(user.firstName)) {
            mEditTextFirstName.setError(getString(R.string.error_field_required));
            focusView = mEditTextFirstName;
        }

        if (focusView == null && TextUtils.isEmpty(user.lastName)) {
            mEditTextLastName.setError(getString(R.string.error_field_required));
            focusView = mEditTextLastName;
        }


        // Check for a valid email address.
        if (focusView == null && TextUtils.isEmpty(user.email)) {
            mEditTextEmail.setError(getString(R.string.error_field_required));
            focusView = mEditTextEmail;
        } else if (focusView == null && !Utils.isValidEmail(user.email)) {
            mEditTextEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEditTextEmail;
        }


        if (focusView == null && (TextUtils.isEmpty(user.password) || !Utils.isPasswordValid(user.password))) {
            mEditTextPassword.setError(getString(R.string.error_invalid_password));
            focusView = mEditTextPassword;
        }


        if (focusView != null) {
            focusView.requestFocus();
        } else {
            signup(user);
        }
    }

    private void signup(User user) {
        if (user != null) {
            user.signup()
                    .then(res -> {
                        Log.i(TAG, "Signup success!");
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                        return true;
                    })
                    .error(err -> {
                        Log.e(TAG, "Error! " + err);
                    });
        }
    }

}
