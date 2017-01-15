package com.katic.centralisedfoodorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfirmActivity extends BaseActivity {

    private static final String TAG = "ConfirmActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    private RadioGroup radioGroup;
    private EditText mLastName;
    private EditText mStreet;
    private EditText mStreetNum;
    private EditText mPhoneNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        final RelativeLayout delieveryLayout = (RelativeLayout) findViewById(R.id.delieveryLayout);
        final RelativeLayout pickupLayout = (RelativeLayout) findViewById(R.id.pickupLayout);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.delieveryRadio){
                    delieveryLayout.setVisibility(View.VISIBLE);
                    pickupLayout.setVisibility(View.GONE);
                } else if (i == R.id.pickupRadio) {
                    delieveryLayout.setVisibility(View.GONE);
                    pickupLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        mLastName = (EditText) findViewById(R.id.lastNameEdit);
        mStreet = (EditText) findViewById(R.id.streetEdit);
        mStreetNum  = (EditText) findViewById(R.id.streetNumberEdit);
        mPhoneNum = (EditText) findViewById(R.id.phoneNumberEdit);

        Button confirmBtn = (Button) findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(mLastName.getText().toString(), mStreet.getText().toString(), mStreetNum.getText().toString(), mPhoneNum.getText().toString());
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Confirm");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(4);
        actionBar.collapseActionView();

    }

    private void signIn(String lastName, String street, String streetNum, String phoneNum) {
        Log.d(TAG, "signIn:" + lastName);
        if (!validateForm()) {
            return;
        }

        //ovdje upisati naredbu koja ce se odraditi nakon klika na tipku
    }

    private boolean validateForm() {
        boolean valid = true;

        String lastName = mLastName.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            mLastName.setError(getString(R.string.required));
            valid = false;
        } else {
            mLastName.setError(null);
        }

        String street = mStreet.getText().toString();
        if (TextUtils.isEmpty(street)) {
            mStreet.setError(getString(R.string.required));
            valid = false;
        } else {
            mStreet.setError(null);
        }

        String streetNum = mStreetNum.getText().toString();
        if (TextUtils.isEmpty(streetNum)) {
            mStreetNum.setError(getString(R.string.required));
            valid = false;
        } else {
            mStreetNum.setError(null);
        }

        String phoneNum = mPhoneNum.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            mPhoneNum.setError(getString(R.string.required));
            valid = false;
        } else {
            mPhoneNum.setError(null);
        }

        return valid;
    }

    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        menu.getItem(0).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ConfirmActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
                return true;
            default:
                finish();
                return true;
        }
    }
}
