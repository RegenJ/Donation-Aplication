package com.example.kasia.helpinghand.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kasia.helpinghand.R;
import com.example.kasia.helpinghand.helpers.DonationHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mLogTask = null;
    private UserRegisterTask mRegisterTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;

    private EditText mPasswordView;
    private AutoCompleteTextView mLoginView;


    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        populateAutoComplete();
        mLoginView = findViewById(R.id.login);
        populateAutoComplete();

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mRegisterButton = findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mLogTask != null) {
            return;
        }

        // Reset errors.
        mPasswordView.setError(null);
        mLoginView.setError(null);

        // Store values at the time of the login attempt.

        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        //Check for valid login
        if (TextUtils.isEmpty(login)) {
            mLoginView.setError(getString(R.string.error_field_required));
            focusView = mLoginView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first form
            focusView.requestFocus();
        } else {
            // Show a progress spinner
            showProgress(true);
            mLogTask = new UserLoginTask(login, password);
            mLogTask.execute((Void) null);
        }
    }


    private void attemptRegister() {
        if (mLogTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mLoginView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        //Check for valid login
        if (TextUtils.isEmpty(login)) {
            mLoginView.setError(getString(R.string.error_field_required));
            focusView = mLoginView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt register and focus the first form field
            focusView.requestFocus();
        } else {
            // Show a progress spinner
            showProgress(true);
            mRegisterTask = new UserRegisterTask(email, login, password);
            mRegisterTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && (email.length() < 40);
    }

    private boolean isPasswordValid(String password) {
        return (password.length() > 4) && (password.length() <= 30);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login task used to authenticate user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mPassword;
        private final String mLogin;
        private final String URL = "https://donationserver.herokuapp.com/login/";

        private String errorMsg = " trololo";

        private int responseCode = 0;


        UserLoginTask(String login, String password) {
            mPassword = password;
            mLogin = login;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                JSONObject request = new JSONObject()
                        .put("username", mLogin)
                        .put("password", mPassword);
                Log.d("REQUEST doInBack", request.toString());
                responseCode = DonationHttpClient.loginRequest(URL, request);

                //TODO change response codes
                Log.d("POST RESPONSE", "responseCode: " + responseCode);
            } catch (IOException | JSONException ex) {
                Log.d("EXCEPTION", ex.getMessage());
                Toast.makeText(getApplicationContext(), "Exception occured", Toast.LENGTH_LONG).show();
                return 0;
            }
            return responseCode;
        }

        @Override
        protected void onPostExecute(final Integer serverResponse) {
            mLogTask = null;
            showProgress(false);
            Log.d("ONPOSTEXEC", "is being executed");

            if (serverResponse == 200) {
                finish();
            } else if (serverResponse == 405) {
                mLoginView.setError("Specify username");
                mLoginView.requestFocus();
            } else if (serverResponse == 505) {
                mPasswordView.setError("Wrong password");
                mPasswordView.requestFocus();
            } else {
                Toast.makeText(getApplicationContext(), "Error: " + serverResponse, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mLogTask = null;
            showProgress(false);
        }
    }

    /**
     * Represents an asynchronous registration task used to register user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Integer> {


        private final String mEmail;
        private final String mPassword;
        private final String mLogin;
        private final String URL = "https://donationserver.herokuapp.com/register/";

        private String errorMsg = " trololo";

        private int responseCode;
        //private String request;

        UserRegisterTask(String email, String login, String password) {
            mEmail = email;
            mPassword = password;
            mLogin = login;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                JSONObject request = new JSONObject()
                        .put("username", mLogin)
                        .put("password", mPassword)
                        .put("email", mEmail);
                responseCode = DonationHttpClient.registerRequest(URL, request);

                //TODO change response codes
                Log.d("POST RESPONSE", "responseCode: " + responseCode);
            } catch (IOException | JSONException ex) {
                Toast.makeText(getApplicationContext(), "Exception occured", Toast.LENGTH_LONG).show();
                return 0;
            }
            return responseCode;
        }

        @Override
        protected void onPostExecute(final Integer serverResponse) {
            mLogTask = null;
            showProgress(false);

            if (serverResponse == 200) {
                finish();
            } else if (serverResponse == 405) {
                mLoginView.setError("Nickname in use");
                mLoginView.requestFocus();
            } else if (serverResponse == 505) {
                mPasswordView.setError("Wrong password");
                mPasswordView.requestFocus();
            } else {
                Toast.makeText(getApplicationContext(), "Error: " + serverResponse, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mLogTask = null;
            showProgress(false);
        }
    }
}

