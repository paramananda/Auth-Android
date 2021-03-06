package com.ppn.authandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.ppn.authandroid.R;
import com.ppn.authandroid.Utils;
import com.ppn.authandroid.models.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mContentView;
    private EditText mEmailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mContentView = findViewById(R.id.content_view);
        mEmailView = (EditText) findViewById(R.id.edit_email);

        mPasswordView = (EditText) findViewById(R.id.edit_password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        mLoginFormView = findViewById(R.id.login_form);
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
            case R.id.button_signin:
                attemptLogin();
                break;
            case R.id.text_signup:
                startActivity(new Intent(this, SignupActivity.class));
                finish();
                break;
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !Utils.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
        } else if (!Utils.isValidEmail(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
        }

        if (focusView != null) {
            focusView.requestFocus();
        } else {
            User user = new User();
            user.email = email;
            user.password = password;

            user.signin()
                    .then(res -> {
                        Log.i(TAG, "Signin success!");
                        Toast.makeText(this, "Signin success.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                        return true;
                    })
                    .error(err -> {
                        Log.e(TAG, "Error! " + err);
                    });
        }
    }


}
