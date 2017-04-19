package com.katic.centralisedfoodorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.util.Locale;

public class CartActivity extends BaseActivity {

    private static final String TAG = "CartActivity";
    private List<GroupItem> cart = getCart();

    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    private CartExpandableListAdapter adapter;
    private ExpandableListView expListView;
    private TextView subtotalNum;
    private LinearLayout withItems;
    private RelativeLayout withoutItems;
    private EditText mComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        withItems = (LinearLayout) findViewById(R.id.layoutWithItems);
        withoutItems = (RelativeLayout) findViewById(R.id.layoutWithoutItems);
        mComment = (EditText) findViewById(R.id.commentEdit);

        //Povezivanje s objektima na maketi
        expListView = (ExpandableListView) findViewById(R.id.cartView);

        subtotalNum = (TextView) findViewById(R.id.subtotalNumber);
        Button checkoutBtn = (Button) findViewById(R.id.checkoutBtn);
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cart.size()>1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    builder.setMessage(R.string.no_more_than_one)
                            .setTitle(R.string.error)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Intent intent = new Intent(CartActivity.this, ConfirmActivity.class);
                    intent.putExtra("comment", mComment.getText().toString());
                    startActivity(intent);
                }
            }
        });

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

                    //Učitavanje stavki iz baze koje su unešene u košaricu
                    mUserReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mUserReference.child("cart").addValueEventListener(cartValueListener);
                            cart=getCart();

                            adapter.notifyDataSetChanged();
                            setSubtotal();
                            for(int i=0; i<adapter.getGroupCount(); i++)
                                expListView.expandGroup(i);

                            if (cart.size()!=0)
                                withItems.setVisibility(View.VISIBLE);
                            else if (cart.size()==0)
                                withoutItems.setVisibility(View.VISIBLE);
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
        actionBar.setTitle(R.string.cart);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(4);
        actionBar.collapseActionView();

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                if (expListView.isGroupExpanded(groupPosition)) {
                    cart.get(groupPosition).setClickedGroup(false);
                    expListView.collapseGroup(groupPosition);
                } else {
                    cart.get(groupPosition).setClickedGroup(true);
                    expListView.expandGroup(groupPosition);
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        if (cart.size()!=0) {
            withItems.setVisibility(View.VISIBLE);
            withoutItems.setVisibility(View.GONE);
        }
        else if (cart.size()==0) {
            withoutItems.setVisibility(View.VISIBLE);
            withItems.setVisibility(View.GONE);
        }
        for(int i=0; i<adapter.getGroupCount(); i++)
            expListView.expandGroup(i);

        super.onResume();
    }

    //Metoda kojom ažuriramo stavke u košarici
    public void addToCart(String string, List<GroupItem> cart){
        List<CartItem> cartItem = new ArrayList<>();
        for(int i=0; i<cart.size(); i++) {
            if (cart.get(i).getTitle().equals(string)) {
                for (int j = 0; j < cart.get(i).getItems().size(); j++) {
                    ChildItem current = cart.get(i).getItems().get(j);
                    CartItem currentItem = new CartItem(current.getTitle(), current.getIngredients(), current.getPrice(), current.getType(), current.getQuantity());
                    cartItem.add(currentItem);
                }
                if (cart.get(i).getItems().size()==0) {
                    this.cart.remove(i);
                }
            }
        }
        if (cart.size()==0) {
            withItems.setVisibility(View.GONE);
            withoutItems.setVisibility(View.VISIBLE);
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
            for (int j=0; j<cart.get(i).getItems().size(); j++) {
                ChildItem current = cart.get(i).getItems().get(j);
                subtotal += current.getQuantity()*current.getPrice();
            }
        subtotalNum.setText(String.format(Locale.getDefault(), "%.2f kn", subtotal));
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
        menu.getItem(1).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                return true;
            case R.id.orderHistory:
                Intent historyIntent = new Intent(CartActivity.this, OrderHistoryActivity.class);
                startActivity(historyIntent);
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
