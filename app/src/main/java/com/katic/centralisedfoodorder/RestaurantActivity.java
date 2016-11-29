package com.katic.centralisedfoodorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.katic.centralisedfoodorder.adapter.ExpandableListAdapter;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.katic.centralisedfoodorder.adapter.RVAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity {

    private static final String TAG = "RestaurantActivity";

    private DatabaseReference mDatabase;
    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private FirebaseStorage storageRef;
    private StorageReference pathReference;

    private List<Restaurant> res = ChooseActivity.restaurants;
    private Restaurant current;

    private Long resID;
    private String title="";
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private ImageView imgView;
    private TextView titleView;
    private TextView addressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        resID = getIntent().getLongExtra(RVAdapter.ID, 0);

        imgView = (ImageView) findViewById(R.id.imageView);
        titleView = (TextView) findViewById(R.id.restaurantName);
        addressView = (TextView) findViewById(R.id.restaurantAddress);
        expListView = (ExpandableListView) findViewById(R.id.expandableListView);

        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("restaurants");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    mUserReference = FirebaseDatabase.getInstance().getReference()
                            .child("users").child(user.getUid());

                    storageRef = FirebaseStorage.getInstance();
                    pathReference = storageRef.
                            getReference("restaurants/"+current.restaurantID+"/"+current.name+".png");
                    Glide.with(getApplicationContext())
                            .using(new FirebaseImageLoader())
                            .load(pathReference)
                            .into(imgView);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        for (int i=0; i<res.size(); i++){
            if (res.get(i).getRestaurantID()==resID) {
                current=res.get(i);
                title=current.getName();
                titleView.setText(title);
                addressView.setText(current.getAddress());
            }
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(4);
        actionBar.collapseActionView();

    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding header data
        listDataHeader.add("Izbor restorana");
        listDataHeader.add("Izbor kuhinje");

        // Adding child data
        List<String> izborRestorana = new ArrayList<>();
        izborRestorana.add("Corner");
        izborRestorana.add("Galija");
        izborRestorana.add("Karaka");
        izborRestorana.add("Lipov Hlad");
        izborRestorana.add("Rustika");

        List<String> izborKuhinje = new ArrayList<>();
        izborKuhinje.add("Kineska");
        izborKuhinje.add("Talijanska");
        izborKuhinje.add("Meksička");
        izborKuhinje.add("Veganska");

        listDataChild.put(listDataHeader.get(0), izborRestorana); // Header, Child data
        listDataChild.put(listDataHeader.get(1), izborKuhinje);
    }

    @Override
    protected void onStart() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(RestaurantActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
                return true;
            default:
                finish();
                return true;
        }

    }

}
