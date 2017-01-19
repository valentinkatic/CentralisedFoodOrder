package com.katic.centralisedfoodorder;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.katic.centralisedfoodorder.adapter.ChooseDelieveryAddressAdapter;
import com.katic.centralisedfoodorder.classes.DelieveryAddress;

import java.util.ArrayList;
import java.util.List;

public class ConfirmActivity extends BaseActivity {

    private static final String TAG = "ConfirmActivity";

    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    private RadioGroup radioGroup;
    private RadioButton delieveryRadio;
    private EditText mLastName;
    private EditText mStreet;
    private EditText mStreetNum;
    private EditText mCity;
    private EditText mPhoneNum;
    private EditText mFloor;
    private EditText mApartmentNum;
    private EditText mLastNamePickup;

    private ArrayList<DelieveryAddress> list = new ArrayList<>();
    private Dialog chooseDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        //Povezivanje s objektima na maketi
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        delieveryRadio = (RadioButton) findViewById(R.id.delieveryRadio);
        final RelativeLayout delieveryLayout = (RelativeLayout) findViewById(R.id.delieveryLayout);
        final RelativeLayout pickupLayout = (RelativeLayout) findViewById(R.id.pickupLayout);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.delieveryRadio){
                    delieveryLayout.setVisibility(View.VISIBLE);
                    pickupLayout.setVisibility(View.GONE);
                    mLastNamePickup.setError(null);
                } else if (i == R.id.pickupRadio) {
                    delieveryLayout.setVisibility(View.GONE);
                    pickupLayout.setVisibility(View.VISIBLE);
                    mLastName.setError(null);
                    mStreet.setError(null);
                    mStreetNum.setError(null);
                    mCity.setError(null);
                    mPhoneNum.setError(null);
                }
            }
        });

        mLastName = (EditText) findViewById(R.id.lastNameEdit);
        mStreet = (EditText) findViewById(R.id.streetEdit);
        mStreetNum  = (EditText) findViewById(R.id.streetNumberEdit);
        mCity = (EditText) findViewById(R.id.cityEdit) ;
        mPhoneNum = (EditText) findViewById(R.id.phoneNumberEdit);
        mFloor = (EditText) findViewById(R.id.floorEdit);
        mApartmentNum = (EditText) findViewById(R.id.apartmentNumberEdit);
        mLastNamePickup = (EditText) findViewById(R.id.lastNamePickupEdit);

        Button confirmBtn = (Button) findViewById(R.id.confirmBtn);
        Button addressChoose = (Button) findViewById(R.id.addressChoose);
        Button addressAdd = (Button) findViewById(R.id.addressAdd);
        Button reset = (Button) findViewById(R.id.reset);

        //Metode koje se izvršavaju prilikom klika na gumb
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "confirm:" + mLastName.getText().toString());
                if (delieveryRadio.isChecked()){
                    if (!validateForm(true)) return;
                    Toast.makeText(ConfirmActivity.this, "U izradi", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!validateForm(false)) return;
                    setLastNamePickup(mLastNamePickup.getText().toString());
                    Toast.makeText(ConfirmActivity.this, "U izradi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addressChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDialog = new Dialog(ConfirmActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_main, null);
                ListView lv = (ListView) dialogView.findViewById(R.id.custom_list);

                ChooseDelieveryAddressAdapter clad = new ChooseDelieveryAddressAdapter(ConfirmActivity.this, list);

                lv.setAdapter(clad);

                chooseDialog.setTitle("Izaberi adresu");
                chooseDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
                chooseDialog.setContentView(dialogView);

                chooseDialog.show();
            }
        });

        addressAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateForm(true)) {
                    return;
                } else
                {
                    String lastName = mLastName.getText().toString();
                    String street = mStreet.getText().toString();
                    String streetNum = mStreetNum.getText().toString();
                    String city = mCity.getText().toString();
                    String floor = mFloor.getText().toString();
                    String apartmentNum = mApartmentNum.getText().toString();
                    String phoneNum = mPhoneNum.getText().toString();

                    DelieveryAddress address = new DelieveryAddress(lastName,street,streetNum,city,floor,apartmentNum,phoneNum);
                    for(int i=0; i<list.size(); i++){
                        list.get(i).defaultAddress=false;
                    }

                    list.add(address);
                    addAddress(list, true);
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLastName.setText("");
                mStreet.setText("");
                mStreetNum.setText("");
                mCity.setText("");
                mPhoneNum.setText("");
                mApartmentNum.setText("");
                mFloor.setText("");
            }
        });

        //Postavljanje naslova Action Baru
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Confirm");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(4);
        actionBar.collapseActionView();

        mAuth = FirebaseAuth.getInstance();

        //Povezivanje s Firebase bazom podataka
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    mUserReference = FirebaseDatabase.getInstance().getReference()
                            .child("users").child(user.getUid());

                    list.clear();

                    //Učitavanje iz baze spremljene adrese korisnika
                    mUserReference.child("delieveryAddress").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                DelieveryAddress da = snapshot.getValue(DelieveryAddress.class);
                                list.add(da);
                                if (da.defaultAddress) {
                                    mLastName.setText(da.lastName);
                                    mStreet.setText(da.street);
                                    mStreetNum.setText(da.streetNumber);
                                    mCity.setText(da.city);
                                    mPhoneNum.setText(da.phoneNumber);
                                    mApartmentNum.setText(da.apartmentNumber);
                                    mFloor.setText(da.floor);
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mUserReference.child("lastNamePickup").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue()!=null)
                            mLastNamePickup.setText(dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);

    }



    //Metoda za dodavanje i brisanje adresa
    public void addAddress(List<DelieveryAddress> addresses, boolean add){
        mUserReference.child("delieveryAddress").setValue(addresses);
        if(add)
        Toast.makeText(this, "Uspješno ste spremili adresu!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Uspješno ste obrisali adresu!", Toast.LENGTH_SHORT).show();
    }

    //Metoda koja sprema upisano prezime prilikom pickup narudžbe
    private void setLastNamePickup(String string){
        mUserReference.child("lastNamePickup").setValue(string);
    }

    //Metoda za postavljanje spremljenih podataka za adresu iz baze u tekstualne okvire
    public void setAddress(List<DelieveryAddress> addresses, int i){
        mLastName.setText(addresses.get(i).lastName);
        mStreet.setText(addresses.get(i).street);
        mStreetNum.setText(addresses.get(i).streetNumber);
        mCity.setText(addresses.get(i).city);
        mPhoneNum.setText(addresses.get(i).phoneNumber);
        mApartmentNum.setText(addresses.get(i).apartmentNumber);
        mFloor.setText(addresses.get(i).floor);
        chooseDialog.dismiss();
        Toast.makeText(this, "Adresa postavljena!", Toast.LENGTH_SHORT).show();
        mUserReference.child("delieveryAddress").setValue(addresses);
    }


    //Metoda kojom provjeravamo jesu li obavezna polja unešena
    private boolean validateForm(boolean choice) {
        boolean valid = true;

        if (choice) {
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

            String city = mCity.getText().toString();
            if (TextUtils.isEmpty(city)) {
                mCity.setError(getString(R.string.required));
                valid = false;
            } else {
                mCity.setError(null);
            }

            String phoneNum = mPhoneNum.getText().toString();
            if (TextUtils.isEmpty(phoneNum)) {
                mPhoneNum.setError(getString(R.string.required));
                valid = false;
            } else {
                mPhoneNum.setError(null);
            }
        } else {
            String lastName = mLastNamePickup.getText().toString();
            if (TextUtils.isEmpty(lastName)) {
                mLastNamePickup.setError(getString(R.string.required));
                valid = false;
            } else {
                mLastNamePickup.setError(null);
            }
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
