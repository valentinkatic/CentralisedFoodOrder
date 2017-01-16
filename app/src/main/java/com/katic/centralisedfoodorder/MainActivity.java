package com.katic.centralisedfoodorder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    boolean doubleBackToExitPressedOnce = false;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private EditText mEmail;
    private EditText mPassword;
    private Button registrationButton;
    private Button noLoginButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        //Povezivanje s objektima na maketi
        mEmail = (EditText) findViewById(R.id.mailText);
        mPassword = (EditText) findViewById(R.id.passwordText);
        mEmail.setInputType(InputType.TYPE_CLASS_TEXT);

        registrationButton = (Button) findViewById(R.id.btnRegistration);
        noLoginButton = (Button) findViewById(R.id.btnNoLogin);
        loginButton = (Button) findViewById(R.id.btnLogin);

        //Definiranje funkcija koje se izvršavaju pritiskanjem na gumbe
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(mEmail.getText().toString(), mPassword.getText().toString());
            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        noLoginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //Povezivanje s Firebase bazom podataka
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    if(!user.isAnonymous()) {
                        showProgressDialog();
                        Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    //Metoda za prijavu s ulaznim parametrima
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        //Prijava pomoću e-maila i lozinke
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(MainActivity.this, R.string.login_success,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    //Metoda za prijavu bez ulaznih parametra (neregistrirani korisnik)
    private void signIn() {
        Log.d(TAG, "signIn Anonymously");

        showProgressDialog();
        if(user==null){
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            User mUser = new User();
                            mDatabase.child("users").child(user.getUid()).setValue(mUser);
                            Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
        } else if (user.isAnonymous()) {
            hideProgressDialog();
            Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //Metoda kojom provjeravamo jesu li obavezna polja unešena
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.required));
            valid = false;
        } else {
            mEmail.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.required));
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
                finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
