package com.katic.centralisedfoodorder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
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

public class RestaurantActivity extends BaseActivity {

    private static final String TAG = "RestaurantActivity";
    public static List<GroupItem> items = new ArrayList<>();
    public static List<GroupItem> cart = new ArrayList<>();
    private int count = 0;

    private DatabaseReference mDatabase;
    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private FirebaseStorage storageRef;
    private StorageReference pathReference;
    private ValueEventListener itemsListener;

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
        Log.d(TAG, "CreatingActivity");

        resID = getIntent().getLongExtra(RVAdapter.ID, 0);

        imgView = (ImageView) findViewById(R.id.restaurantImageView);
        titleView = (TextView) findViewById(R.id.restaurantName);
        addressView = (TextView) findViewById(R.id.restaurantAddress);
        listView = (AnimatedExpandableListView) findViewById(R.id.animatedList);

        addressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantActivity.this);
                builder.
                        setMessage(getText(R.string.instructions))
                        .setNegativeButton(getText(R.string.no), dialogClickListener)
                        .setPositiveButton(getText(R.string.yes), dialogClickListener)
                        .show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Long.toString(resID)).child("food_list");

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

                    //Postavljanje slike restorana
                    storageRef = FirebaseStorage.getInstance();
                    pathReference = storageRef.
                            getReference("restaurants/"+current.restaurantID+"/"+current.name+"_large.png");
                    Glide.with(getApplicationContext())
                            .using(new FirebaseImageLoader())
                            .load(pathReference)
                            .into(imgView);

                    ////Učitavanje stavki koje su u košarici i njihovo prebrojavanje za potrebe prikaza ikone košarice
                    mUserReference.child("cart").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            cart.clear();
                            count=0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                GroupItem item = new GroupItem();
                                item.title=snapshot.getKey();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                    CartItem cart = snapshot1.getValue(CartItem.class);
                                    ChildItem child = new ChildItem(cart);
                                    item.items.add(child);
                                    count++;
                                }
                                cart.add(item);
                            }
                            adapter.notifyDataSetChanged();
                            invalidateOptionsMenu();
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
            if (res.get(i).restaurantID==resID) {
                current=res.get(i);
                title=current.name;
                titleView.setText(title);
                addressView.setText(current.address);
            }
        }

        //Postavljanje naslova Action Baru
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(4);
        actionBar.collapseActionView();

        //Učitavanje iz baze jelovnik koji restoran nudi
        itemsListener = new ValueEventListener() {
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
                initializeAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    MapMethod();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    private void MapMethod() {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+ current.address+" "+current.city +"&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    //Metoda za ažuriranje stavki u košarici
    public void addToCart(String string, List<GroupItem> cart){
        List<CartItem> cartItem = new ArrayList<>();
        count=0;
        for(int i=0; i<cart.size(); i++) {
            if (cart.get(i).title.equals(string)) {
                for (int j = 0; j < cart.get(i).items.size(); j++) {
                    ChildItem current = cart.get(i).items.get(j);
                    CartItem currentItem = new CartItem(current.title, current.ingredients, current.price, current.type, current.quantity);
                    cartItem.add(currentItem);
                }
            }
            for (int j = 0; j < cart.get(i).items.size(); j++) count++;
        }
        mUserReference.child("cart").child(string).setValue(cartItem);
        invalidateOptionsMenu();
    }

    //Metoda za pozivanje restorana
    private void makeCall(){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        String phone = "tel:" + current.phone;
        callIntent.setData(Uri.parse(phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        startActivity(Intent.createChooser(callIntent, "Izaberite klijenta :"));
    }

    private void initializeAdapter(){
        adapter = new AnimatedListAdapter(this, title);
        adapter.setData(items);

        listView.setAdapter(adapter);

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
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        mDatabase.addListenerForSingleValueEvent(itemsListener);

    }

    @Override
    protected void onResume() {
       /*for(int i=0; i<items.size(); i++)
            if(items.get(i).clickedGroup)
                listView.expandGroup(i);*/
        super.onResume();
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
        MenuItem cartMenu = menu.findItem(R.id.cart);
        MenuItem phoneMenu = menu.findItem(R.id.phone);
        phoneMenu.setIcon(R.drawable.phone);
        if(user.isAnonymous()){
            cartMenu.setVisible(false);
        } else
        if(count!=0)
            cartMenu.setIcon(buildCounterDrawable(count, R.drawable.ic_full_cart));
        else cartMenu.setIcon(R.drawable.empty_cart);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.phone:
                makeCall();
                return true;
            case R.id.cart:
                Intent checkout = new Intent(RestaurantActivity.this, CartActivity.class);
                startActivity(checkout);
                return true;
            case R.id.orderHistory:
                Intent historyIntent = new Intent(RestaurantActivity.this, OrderHistoryActivity.class);
                startActivity(historyIntent);
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

    //Metoda koja mijenja ikonu košarici s obzirom na količinu stavki u njoj
    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
