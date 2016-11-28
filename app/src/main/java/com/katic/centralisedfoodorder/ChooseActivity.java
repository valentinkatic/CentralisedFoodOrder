package com.katic.centralisedfoodorder;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.katic.centralisedfoodorder.adapter.HorizontalListView;
import com.katic.centralisedfoodorder.adapter.RVAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.katic.centralisedfoodorder.R.id.tabHost;

public class ChooseActivity extends BaseActivity {

    private static final String TAG = "ChooseActivity";

    private boolean doubleBackToExitPressedOnce = false;

    private DatabaseReference mDatabase;
    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private FirebaseStorage storageRef;
    private StorageReference pathReference;

    public static List<Restaurant> restaurants;
    public static List<Long> bookmarks;
    HorizontalListView listview;
    HorizontalListView listview2;
    private RecyclerView rv;
    private RecyclerView rv2;
    private TabHost host;
    private ImageView image;



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
                Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("restaurants");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Izbor restorana");
        actionBar.setElevation(4);
        actionBar.collapseActionView();


        host = (TabHost)findViewById(tabHost);
        host.setup();


        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("TAB_1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("All");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("TAB_2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Bookmarks");
        host.addTab(spec);

        listview = (HorizontalListView) findViewById(R.id.listview);
        //listview.setAdapter(new HAdapter());

        listview2 = (HorizontalListView) findViewById(R.id.listview2);
        //listview2.setAdapter(new HAdapter());
        listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                initializeAdapter();
                mUserReference.child("bookmarks").setValue(bookmarks);
            }
        });

        rv = (RecyclerView) findViewById(R.id.restaurantList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        rv2 = (RecyclerView) findViewById(R.id.restaurantList2);
        LinearLayoutManager llm2 = new LinearLayoutManager(this);
        rv2.setLayoutManager(llm2);

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            public void onTabChanged(String tabId) {
                initializeAdapter();
                mUserReference.child("bookmarks").setValue(bookmarks);
            }
        });

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

                    mUserReference.child("bookmarks").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            bookmarks.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Long item = (Long) snapshot.getValue();
                                bookmarks.add(item);
                            }
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
    }

    private void initializeAdapter(){
        rv.setAdapter(new RVAdapter(this, false));
        rv2.setAdapter(new RVAdapter(this, true));

    }

    private static String[] dataObjects = new String[]{
            "Pizza", "pizza",
            "Meksicka", "mexican",
            "Talijanska", "italian",
            "Kineska", "chinese",
            "Indijska", "indian",
            "#6", ""
    };

    private class HAdapter extends BaseAdapter {

        public HAdapter() {
            super();
        }

        public int getCount() {
            return dataObjects.length / 2;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, null);
            TextView title = (TextView) retval.findViewById(R.id.title);
            title.setText(dataObjects[position * 2]);
            String string = dataObjects[position * 2 + 1];
            image = (ImageView) retval.findViewById(R.id.image);
            pathReference = storageRef.getReference("restaurants/"+string+".png");
            Glide.with(getApplicationContext())
                    .using(new FirebaseImageLoader())
                    .load(pathReference)
                    .into(image);
            return retval;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        restaurants = new ArrayList<>();
        bookmarks = new ArrayList<>();

        mAuth.addAuthStateListener(mAuthListener);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                restaurants.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant currentRes = snapshot.getValue(Restaurant.class);
                    restaurants.add(currentRes);
                }
                initializeAdapter();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listview.setAdapter(new HAdapter());
                listview2.setAdapter(new HAdapter());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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

        if(user.isAnonymous()){
            Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}
