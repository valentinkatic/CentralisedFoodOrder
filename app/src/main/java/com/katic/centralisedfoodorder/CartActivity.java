package com.katic.centralisedfoodorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.katic.centralisedfoodorder.adapter.CartExpandableListAdapter;
import com.katic.centralisedfoodorder.classes.CartItem;
import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.classes.GroupItem;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BaseActivity {

    private static final String TAG = "CartActivity";
    public static List<GroupItem> cart = new ArrayList<>();

    private DatabaseReference mDatabase;
    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    CartExpandableListAdapter adapter;
    ExpandableListView expListView;
    TextView subtotalNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Povezivanje s objektima na maketi
        expListView = (ExpandableListView) findViewById(R.id.cartView);

        subtotalNum = (TextView) findViewById(R.id.subtotalNumber);
        Button checkoutBtn = (Button) findViewById(R.id.checkoutBtn);
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, ConfirmActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("restaurants");

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
                            adapter.notifyDataSetChanged();
                            setSubtotal();
                            for(int i=0; i<adapter.getGroupCount(); i++)
                                expListView.expandGroup(i);
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
        actionBar.setTitle("Cart");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(4);
        actionBar.collapseActionView();

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                if (expListView.isGroupExpanded(groupPosition)) {
                    cart.get(groupPosition).clickedGroup = false;
                    expListView.collapseGroup(groupPosition);
                } else {
                    cart.get(groupPosition).clickedGroup = true;
                    expListView.expandGroup(groupPosition);
                }
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        for(int i=0; i<cart.size(); i++)
            if(cart.get(i).clickedGroup)
                expListView.expandGroup(i);
        super.onResume();
    }

    //Metoda kojom ažuriramo stavke u košarici
    public void addToCart(String string, List<GroupItem> cart){
        List<CartItem> cartItem = new ArrayList<>();
        for(int i=0; i<cart.size(); i++) {
            if (cart.get(i).title.equals(string)) {
                for (int j = 0; j < cart.get(i).items.size(); j++) {
                    ChildItem current = cart.get(i).items.get(j);
                    CartItem currentItem = new CartItem(current.title, current.ingredients, current.price, current.type, current.quantity);
                    cartItem.add(currentItem);
                }
                if (cart.get(i).items.size()==0) this.cart.remove(i);
            }
        }
        mUserReference.child("cart").child(string).setValue(cartItem);
        adapter.notifyDataSetChanged();
        setSubtotal();
    }

    //Inicijalizacija adaptera
    private void initializeAdapter(){
        adapter = new CartExpandableListAdapter(this, cart);
        expListView.setAdapter(adapter);
    }

    //Metoda za računanje ukupnog iznosa cijena
    private void setSubtotal(){
        float subtotal=0;
        for(int i=0; i<cart.size(); i++)
            for (int j=0; j<cart.get(i).items.size(); j++) {
                ChildItem current = cart.get(i).items.get(j);
                subtotal += current.quantity*current.price;
            }
        subtotalNum.setText(String.format("%.2f", subtotal) + " kn");
    }

    @Override
    protected void onStart() {
        super.onStart();

        initializeAdapter();
        setSubtotal();
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
