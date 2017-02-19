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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.katic.centralisedfoodorder.classes.User;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "RegisterActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mLastNameField;
    private EditText mAddressField;
    private EditText mStreetNumberField;
    private EditText mCityField;
    private EditText mPhoneNumField;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Postavljanje naslova Action Baru
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.register);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mEmailField = (EditText) findViewById(R.id.mailRegText);
        mPasswordField = (EditText) findViewById(R.id.passwordRegText);
        mLastNameField = (EditText) findViewById(R.id.lastNameRegText);
        mAddressField = (EditText) findViewById(R.id.addressRegText);
        mStreetNumberField = (EditText) findViewById(R.id.streetNumberRegText);
        mCityField = (EditText) findViewById(R.id.cityRegText);
        mPhoneNumField = (EditText) findViewById(R.id.phoneNumberRegText);
        mSignUpButton = (Button) findViewById(R.id.registerButton);

        mEmailField.setInputType(InputType.TYPE_CLASS_TEXT);

        mSignUpButton.setOnClickListener(this);

        //Povezivanje s Firebase bazom podataka
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if(!user.isAnonymous()) {
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

    //Metoda za unos novog korisnika
    private void writeNewUser(String email, String lastName, String address, String streetNumber, String city, String phoneNum) {
        User mUser = new User(email, lastName, address, streetNumber, city, phoneNum);

        mDatabase.child("users").child(user.getUid()).setValue(mUser);
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

        showProgressDialog();
        final String email = mEmailField.getText().toString();
        final String password = mPasswordField.getText().toString();
        final String lastName = mLastNameField.getText().toString();
        final String address = mAddressField.getText().toString();
        final String streetNumber = mStreetNumberField.getText().toString();
        final String city = mCityField.getText().toString();
        final String phoneNum = mPhoneNumField.getText().toString();

        //Metoda za kreiranje korisnika s e-mailom i lozinkom
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, R.string.auth_success,
                                    Toast.LENGTH_SHORT).show();
                            writeNewUser(email, lastName, address, streetNumber, city, phoneNum);
                            Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    }
                });

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

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.registerButton) {
            signUp();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    public void onBackPressed()
    {
        this.startActivity(new Intent(RegisterActivity.this,MainActivity.class));
        finish();
        return;
    }

}
