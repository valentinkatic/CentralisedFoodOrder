package com.katic.centralisedfoodorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.katic.centralisedfoodorder.adapter.CartExpandableListAdapter;
import com.katic.centralisedfoodorder.classes.Cart;
import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.classes.GroupItem;
import com.katic.centralisedfoodorder.classes.Restaurant;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BaseActivity {

    private static final String TAG = "CartActivity";

    public Cart cart = new Cart();

    private DatabaseReference mDatabase;
    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    CartExpandableListAdapter adapter;
    ExpandableListView expListView;

    private List<GroupItem> resItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        expListView = (ExpandableListView) findViewById(R.id.cartView);

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

                    mUserReference.child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                cart = snapshot.getValue(Cart.class);

                                mDatabase.child(cart.ID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        GroupItem item = new GroupItem();
                                        Restaurant currentRes = dataSnapshot.getValue(Restaurant.class);
                                        item.title=currentRes.name;

                                        DataSnapshot currentItem=dataSnapshot.child("food_type").child(cart.markedFoodGroup).child(cart.markedFoodChild);

                                        ChildItem child = new ChildItem();
                                        child.title = currentItem.getKey();
                                        child.invisible = currentItem.getValue().toString();
                                        item.items.add(child);

                                        resItems.add(item);

                                        adapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //Toast.makeText(CartActivity.this, items.get(0).items.get(0).title, Toast.LENGTH_SHORT).show();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cart");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(4);
        actionBar.collapseActionView();




        //items = RestaurantActivity.items;


        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                if (expListView.isGroupExpanded(groupPosition)) {
                    resItems.get(groupPosition).clickedGroup = false;
                    expListView.collapseGroup(groupPosition);
                } else {
                    resItems.get(groupPosition).clickedGroup = true;
                    expListView.expandGroup(groupPosition);
                }
                return true;
            }
        });

    }

    private void initializeAdapter(){
        adapter = new CartExpandableListAdapter(this, resItems);
        expListView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Toast.makeText(this, Integer.toString(resItems.size()), Toast.LENGTH_SHORT).show();

        initializeAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        menu.getItem(0).setIcon(R.drawable.checkout);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                //Intent checkout = new Intent(RestaurantActivity.this, CartActivity.class);
                //startActivity(checkout);
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
                return true;
            default:
                finish();
                return true;
        }
    }

}
