package com.example.kasia.helpinghand.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kasia.helpinghand.R;
import com.example.kasia.helpinghand.helpers.DonationHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class GatheringActivity extends AppCompatActivity {

    private GatheringTask mGatheringTask = null;


    private EditText mDonationName;
    private EditText mDonationAccount;
    private EditText mDonationAmount;
    private EditText mDonationDate;
    private EditText mDonationDesc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_donation);

        mDonationName = findViewById(R.id.donationNameEditText);
        mDonationAccount = findViewById(R.id.donationAccountEditText);
        mDonationAmount = findViewById(R.id.amountDonationEditText);
        mDonationDate = findViewById(R.id.endDonationEditText);
        mDonationDate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptGathering();
                    return true;
                }
                return false;
            }
        });

        mDonationDesc = findViewById(R.id.editText5);

        Button mCreateDonationButton = findViewById(R.id.createDonationButton);
        mCreateDonationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptGathering();
            }
        });

    }

    /**
     * Attempts to donate the foundation specified in form
     * If there are form errors, they are presented and no actual donation attempt is made.
     */
    private void attemptGathering() {
        if (mGatheringTask != null) {
            return;
        }

        // Store values at the time of the gathering attempt.
        String name = mDonationName.getText().toString();
        String account = mDonationAccount.getText().toString();
        Integer amount = Integer.parseInt(mDonationAmount.getText().toString());
        String date = mDonationDate.getText().toString();
        String desc = mDonationDesc.getText().toString();

        boolean cancel = false;
        View focusView = null;


        //Check for valid:
        //  name
        if (TextUtils.isEmpty(name)) {
            mDonationName.setError(getString(R.string.error_field_required));
            focusView = mDonationName;
            cancel = true;
        }

        //  account
        if (TextUtils.isEmpty(account)) {
            mDonationAccount.setError(getString(R.string.error_field_required));
            focusView = mDonationAccount;
            cancel = true;
        } else if (!isAccountValid(account)) {
            mDonationAccount.setError(getString(R.string.error_invalid_account));
            focusView = mDonationAccount;
            cancel = true;
        }

        // amount
        if (!isAmountValid(amount)) {
            mDonationAmount.setError(getString(R.string.error_invalid_amount));
            focusView = mDonationAmount;
            cancel = true;
        }


        //  date
        if (TextUtils.isEmpty(date)) {
            mDonationDate.setError(getString(R.string.error_invalid_date));
            focusView = mDonationDate;
            cancel = true;
        }

        //  description
        if (TextUtils.isEmpty(desc)) {
            mDonationDesc.setError(getString(R.string.error_field_required));
            focusView = mDonationDesc;
            cancel = true;
        } else if (!isDescriptionValid(desc)) {
            mDonationDesc.setError(getString(R.string.error_invalid_desc));
            focusView = mDonationDesc;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt donation and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            mGatheringTask = new GatheringTask(name, account, amount, date, desc);
            mGatheringTask.execute((Void) null);
        }
    }

    private boolean isAccountValid(String accountNumber) {
        return (accountNumber.length() > 20) && (accountNumber.length() <= 30);
    }

    private boolean isAmountValid(Integer amount) {
        return (amount > 0);
    }

    private boolean isDescriptionValid(String description) {
        return (description.length() > 1) && (description.length() <= 300);
    }


    /**
     * Represents an asynchronous donation task
     */
    public class GatheringTask extends AsyncTask<Void, Void, Integer> {

        //TODO Finish donation task
        private final String mName;
        private final String mAccount;
        private final Integer mAmount;
        private final String mDate;
        private final String mDescription;
        private final String URL = "https://donationserver.herokuapp.com/gathering/";

        private String errorMsg = " trololo";

        private int responseCode;
        //private String request;

        GatheringTask(String name, String account, Integer amount, String date, String description) {
            mName = name;
            mAccount = account;
            mAmount = amount;
            mDate = date;
            mDescription = description;
        }

        //TODO finish
        @Override
        protected Integer doInBackground(Void... params) {
            Response response;
            try {
                JSONObject request = new JSONObject()
                        .put("name", mName)
                        .put("account", mAccount)
                        .put("amount", mAmount)
                        .put("date", mDate)
                        .put("description", mDescription);
                Log.d("REQUEST doInBack", request.toString());
                response = DonationHttpClient.registerRequest(URL, request);

                errorMsg = " " + response.body().string();
                //TODO change response codes
                Log.d("POST RESPONSE", "responseCode: " + responseCode);
            } catch (IOException | JSONException ex) {
                Log.d("EXCEPTION", ex.getMessage());
                Toast.makeText(getApplicationContext(), "Exception occured", Toast.LENGTH_LONG).show();
                return 1;
            }
            return response.code();
        }

        @Override
        protected void onPostExecute(final Integer serverResponse) {
            mGatheringTask = null;
            // showProgress(false);

            //TODO finish managment
            if (serverResponse == 200) {
                finish();
            } else if (serverResponse == 501) {
                mDonationName.setError("Name wrong?");
                mDonationName.requestFocus();
            } else {
                Toast.makeText(getApplicationContext(), "Error: " + serverResponse, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mGatheringTask = null;
            // showProgress(false);
        }
    }


}
