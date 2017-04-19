package com.katic.centralisedfoodorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.katic.centralisedfoodorder.classes.User;

public class RegisterActivity extends BaseActivity{

    private static final String TAG = "RegisterActivity";

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mLastNameField;
    private EditText mAddressField;
    private EditText mStreetNumberField;
    private EditText mCityField;
    private EditText mPhoneNumField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Postavljanje naslova Action Baru
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.register);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mEmailField = (EditText) findViewById(R.id.mailRegText);
        mPasswordField = (EditText) findViewById(R.id.passwordRegText);
        mLastNameField = (EditText) findViewById(R.id.lastNameRegText);
        mAddressField = (EditText) findViewById(R.id.addressRegText);
        mStreetNumberField = (EditText) findViewById(R.id.streetNumberRegText);
        mCityField = (EditText) findViewById(R.id.cityRegText);
        mPhoneNumField = (EditText) findViewById(R.id.phoneNumberRegText);

        Button mSignUpButton = (Button) findViewById(R.id.registerButton);

        mEmailField.setInputType(InputType.TYPE_CLASS_TEXT);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        signUp();
                }
        });

        //Povezivanje s Firebase bazom podataka
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if (!user.isAnonymous()) {
                        Intent intent = new Intent(RegisterActivity.this, ChooseActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (user!=null && user.isAnonymous()){
            FirebaseAuth.getInstance().signOut();
        }
        if (!validateForm()) {
            return;
        }

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        String lastName = mLastNameField.getText().toString();
        String address = mAddressField.getText().toString();
        String streetNumber = mStreetNumberField.getText().toString();
        String city = mCityField.getText().toString();
        String phoneNum = mPhoneNumField.getText().toString();

        User user = new User(email, lastName, address, streetNumber, city, phoneNum);
        createUser(user, password, TAG);
    }

    //Metoda kojom provjeravamo jesu li obavezna polja unešena
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError(getString(R.string.required));
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError(getString(R.string.required));
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        return result;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    public void onBackPressed()
    {
        this.startActivity(new Intent(RegisterActivity.this,MainActivity.class));
        finish();
    }

}
