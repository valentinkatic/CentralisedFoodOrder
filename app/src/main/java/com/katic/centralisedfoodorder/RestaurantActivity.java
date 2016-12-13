package com.katic.centralisedfoodorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.katic.centralisedfoodorder.adapter.AnimatedExpandableListView;
import com.katic.centralisedfoodorder.adapter.AnimatedListAdapter;
import com.katic.centralisedfoodorder.adapter.RVAdapter;
import com.katic.centralisedfoodorder.classes.CartItem;
import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.classes.GroupItem;
import com.katic.centralisedfoodorder.classes.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity {

    private static final String TAG = "RestaurantActivity";
    public static List<GroupItem> items = new ArrayList<>();
    public static List<GroupItem> cart = new ArrayList<>();

    private Menu menu;

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

    private ImageView imgView;
    private TextView titleView;
    private TextView addressView;

    private AnimatedExpandableListView listView;
    private AnimatedListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        resID = getIntent().getLongExtra(RVAdapter.ID, 0);

        imgView = (ImageView) findViewById(R.id.restaurantImageView);
        titleView = (TextView) findViewById(R.id.restaurantName);
        addressView = (TextView) findViewById(R.id.restaurantAddress);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Long.toString(resID)).child("food_type");

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
                            getReference("restaurants/"+current.restaurantID+"/"+current.name+"_large.png");
                    Glide.with(getApplicationContext())
                            .using(new FirebaseImageLoader())
                            .load(pathReference)
                            .into(imgView);

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

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GroupItem item = new GroupItem();
                    item.title = snapshot.getKey();

                    for (DataSnapshot children : snapshot.getChildren()){
                        ChildItem child = children.getValue(ChildItem.class);
                        item.items.add(child);
                    }

                    items.add(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void addToCart(String string, List<GroupItem> cart){
        List<CartItem> cartItem = new ArrayList<>();
        for(int i=0; i<cart.size(); i++)
            if (cart.get(i).title.equals(string)){
                for(int j=0; j<cart.get(i).items.size(); j++){
                    ChildItem current = cart.get(i).items.get(j);
                    CartItem currentItem = new CartItem(current.title, current.ingredients, current.price, current.type);
                    cartItem.add(currentItem);
                }
            }
        mUserReference.child("cart").child(string).setValue(cartItem);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        adapter = new AnimatedListAdapter(this, title);
        adapter.setData(items);

        listView = (AnimatedExpandableListView) findViewById(R.id.animatedList);
        listView.setAdapter(adapter);

        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (listView.isGroupExpanded(groupPosition)) {
                    items.get(groupPosition).clickedGroup = false;
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    items.get(groupPosition).clickedGroup = true;
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        this.menu=menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                Intent checkout = new Intent(RestaurantActivity.this, CartActivity.class);
                startActivity(checkout);
                return true;
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

    @Override
    public void onBackPressed() {
        finish();
    }
}
