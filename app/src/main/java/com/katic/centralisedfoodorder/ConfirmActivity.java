package com.katic.centralisedfoodorder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.katic.centralisedfoodorder.adapter.ChooseDelieveryAddressAdapter;
import com.katic.centralisedfoodorder.classes.CartItem;
import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.classes.DeliveryAddress;
import com.katic.centralisedfoodorder.classes.GroupItem;
import com.katic.centralisedfoodorder.classes.OrderData;
import com.katic.centralisedfoodorder.classes.Restaurant;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ConfirmActivity extends BaseActivity {

    private static final String TAG = "ConfirmActivity";

    private DatabaseReference mUserReference;
    private DatabaseReference mRestaurantReference;
    private DatabaseReference mRestaurantDataReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    private RadioGroup radioGroup;
    private RadioButton delieveryRadio;
    private RadioButton pickupRadio;
    private EditText mLastName;
    private EditText mStreet;
    private EditText mStreetNum;
    private EditText mCity;
    private EditText mPhoneNum;
    private EditText mFloor;
    private EditText mApartmentNum;
    private EditText mLastNamePickup;

    private ArrayList<DeliveryAddress> list = new ArrayList<>();
    private List<GroupItem> cart = new ArrayList<>();
    private List<GroupItem> orderHistory = new ArrayList<>();
    private Dialog chooseDialog;
    private String comment;
    private String resAddress;
    private String resCity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        comment = getIntent().getStringExtra("comment");

        //Povezivanje s objektima na maketi
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        delieveryRadio = (RadioButton) findViewById(R.id.delieveryRadio);
        pickupRadio = (RadioButton) findViewById(R.id.pickupRadio);
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
                Log.d(TAG, "confirm:" + mLastName.getText().toString());if(delieveryRadio.isChecked() || pickupRadio.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmActivity.this);
                    builder.setMessage(getText(R.string.orderConfirm)).setPositiveButton(getText(R.string.yes), dialogClickListener)
                            .setNegativeButton(getText(R.string.no), dialogClickListener).show();
                } else
                    Toast.makeText(ConfirmActivity.this, R.string.pickMethod, Toast.LENGTH_SHORT).show();
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

                chooseDialog.setTitle(R.string.choose_address);
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

                    DeliveryAddress address = new DeliveryAddress(lastName,street,streetNum,city,floor,apartmentNum,phoneNum);
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
        actionBar.setTitle(R.string.confirm);
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
                    mRestaurantDataReference = FirebaseDatabase.getInstance().getReference().child("restaurantData");
                    mRestaurantReference = FirebaseDatabase.getInstance().getReference().child("restaurants");

                    list.clear();

                    //Učitavanje iz baze spremljene adrese korisnika
                    mUserReference.child("deliveryAddress").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                DeliveryAddress da = snapshot.getValue(DeliveryAddress.class);
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

                    //Učitavanje stavki iz baze koje su unešene u košaricu
                    mUserReference.child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            cart.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                GroupItem item = new GroupItem();
                                item.title=snapshot.getKey();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                    CartItem cart = snapshot1.getValue(CartItem.class);
                                    ChildItem child = new ChildItem(cart);
                                    item.items.add(child);
                                }
                                cart.add(item);
                            }
                            mRestaurantReference.addListenerForSingleValueEvent(getAddress);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    orderHistory.clear();
                    mUserReference.child("orderHistory").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                GroupItem item = snapshot.getValue(GroupItem.class);
                                orderHistory.add(item);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //Učitavanje iz baze prezimena koji je spremljen pri Pickup metodi dostave
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

    private void sendDataToDatabase(boolean isDelivery){
        String phoneToken = FirebaseInstanceId.getInstance().getToken();
        mUserReference.child("phoneToken").setValue(phoneToken);
        OrderData orderData;
        if(isDelivery){
            String lastName = mLastName.getText().toString();
            String street = mStreet.getText().toString();
            String streetNum = mStreetNum.getText().toString();
            String city = mCity.getText().toString();
            String floor = mFloor.getText().toString();
            String apartmentNum = mApartmentNum.getText().toString();
            String phoneNum = mPhoneNum.getText().toString();

            DeliveryAddress address = new DeliveryAddress(lastName,street,streetNum,city,floor,apartmentNum,phoneNum);

            orderData = new OrderData(
                    phoneToken, cart.get(0).items, address, isDelivery, comment
            );
        } else {
            String lastNamePickup = mLastNamePickup.getText().toString();
            orderData = new OrderData(
                    phoneToken, cart.get(0).items, isDelivery, lastNamePickup, comment
            );
        }

        mRestaurantDataReference.child(cart.get(0).title).push().setValue(orderData);
    }

    ValueEventListener getAddress = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                Restaurant res = snapshot.getValue(Restaurant.class);
                if(res.name.equals(cart.get(0).title)) {
                    resAddress = res.address;
                    resCity = res.city;
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void startIntent(){
        Intent intent = new Intent(ConfirmActivity.this, OrderHistoryActivity.class);
        intent.putExtra(TAG, true);
        startActivity(intent);
        finishAffinity();
    }

    private void removeCart(){
        for(int i=0; i<orderHistory.size(); i++){
            GroupItem current = orderHistory.get(i);
            for (int j=0; j<current.items.size(); j++){
                current.items.get(j).quantity=1;
            }
            cart.add(current);
        }

        cart.get(0).address = resAddress;
        cart.get(0).city = resCity;

        Calendar currentDay = Calendar.getInstance();
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        cart.get(0).orderTime=df.format(currentDay.getTime());
        mUserReference.child("orderHistory").setValue(cart);
        mUserReference.child("cart").setValue(null);
    }

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    if (delieveryRadio.isChecked()){
                        if (!validateForm(true)) return;
                        sendDataToDatabase(true);
                        removeCart();
                        startIntent();
                    }
                    else if (pickupRadio.isChecked()) {
                        if (!validateForm(false)) return;
                        setLastNamePickup(mLastNamePickup.getText().toString());
                        sendDataToDatabase(false);
                        removeCart();
                        startIntent();
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    //Metoda za dodavanje i brisanje adresa
    public void addAddress(List<DeliveryAddress> addresses, boolean add){
        mUserReference.child("deliveryAddress").setValue(addresses);
        if(add)
        Toast.makeText(this, R.string.address_save, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, R.string.address_delete, Toast.LENGTH_SHORT).show();
    }

    //Metoda koja sprema upisano prezime prilikom pickup narudžbe
    private void setLastNamePickup(String string){
        mUserReference.child("lastNamePickup").setValue(string);
    }

    //Metoda za postavljanje spremljenih podataka za adresu iz baze u tekstualne okvire
    public void setAddress(List<DeliveryAddress> addresses, int i){
        mLastName.setText(addresses.get(i).lastName);
        mStreet.setText(addresses.get(i).street);
        mStreetNum.setText(addresses.get(i).streetNumber);
        mCity.setText(addresses.get(i).city);
        mPhoneNum.setText(addresses.get(i).phoneNumber);
        mApartmentNum.setText(addresses.get(i).apartmentNumber);
        mFloor.setText(addresses.get(i).floor);
        chooseDialog.dismiss();
        Toast.makeText(this, R.string.address_set, Toast.LENGTH_SHORT).show();
        mUserReference.child("deliveryAddress").setValue(addresses);
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
        menu.getItem(1).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                return true;
            case R.id.orderHistory:
                Intent historyIntent = new Intent(ConfirmActivity.this, OrderHistoryActivity.class);
                startActivity(historyIntent);
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
