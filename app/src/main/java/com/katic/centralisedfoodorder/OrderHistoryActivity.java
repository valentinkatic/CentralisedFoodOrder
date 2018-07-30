package com.katic.centralisedfoodorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.katic.centralisedfoodorder.adapter.OrderHistoryExpandableListAdapter;
import com.katic.centralisedfoodorder.classes.CartItem;
import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.classes.GroupItem;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends BaseActivity {

    private static final String TAG = "OrderHistoryActivity";
    private boolean orderCompleted;
    public static List<GroupItem> orderHistory = getOrderHistory();

    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    OrderHistoryExpandableListAdapter adapter;
    ExpandableListView orderHistoryView;
    TextView orderHistoryEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        orderCompleted = getIntent().getBooleanExtra("ConfirmActivity", false);

        orderHistoryView = (ExpandableListView) findViewById(R.id.elv_order_history);
        orderHistoryEmpty = (TextView) findViewById(R.id.tv_empty_order_history);

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

                    mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mUserReference.child("orderHistory").addValueEventListener(orderHistoryListener);
                            orderHistory = getOrderHistory();
                            adapter.notifyDataSetChanged();
                            if (orderHistory.size()>0) orderHistoryView.setVisibility(View.VISIBLE);
                            else orderHistoryEmpty.setVisibility(View.VISIBLE);
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

        //Postavljanje naslova Action Baru
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.orderHistory);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(4);
        actionBar.collapseActionView();

    }

    //Inicijalizacija adaptera
    private void initializeAdapter(){
        adapter = new OrderHistoryExpandableListAdapter(this, orderHistory);
        orderHistoryView.setAdapter(adapter);
    }

    //Metoda za brisanje stavke iz povijesti
    public void removeFromHistory(List<GroupItem> orderHistory){
        mUserReference.child("orderHistory").setValue(orderHistory);
        adapter.notifyDataSetChanged();
        if(orderHistory.size()==0){
            orderHistoryEmpty.setVisibility(View.VISIBLE);
            orderHistoryView.setVisibility(View.GONE);
        }
    }

    //Metoda za dodavanje stare narudžbe u košaricu
    public void addToCart(GroupItem cart){
        List<CartItem> cartItem = new ArrayList<>();
        for(int i=0; i<cart.getItems().size(); i++){
            ChildItem current = cart.getItems().get(i);
            CartItem currentItem = new CartItem(current.getTitle(), current.getIngredients(), current.getPrice(), current.getType(), current.getQuantity());
            cartItem.add(currentItem);
        }
        mUserReference.child("cart").child(cart.getTitle()).setValue(cartItem);
        Intent intent = new Intent(OrderHistoryActivity.this, CartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        initializeAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(OrderHistoryActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
                return true;
            default:
                if(orderCompleted){
                    Intent returnIntent = new Intent(OrderHistoryActivity.this, MainActivity.class);
                    startActivity(returnIntent);
                }
                finish();
                return true;
        }
    }

}
