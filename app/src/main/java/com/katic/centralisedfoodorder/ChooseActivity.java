package com.katic.centralisedfoodorder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.katic.centralisedfoodorder.adapter.HorizontalAdapter;
import com.katic.centralisedfoodorder.adapter.RVAdapter;
import com.katic.centralisedfoodorder.classes.CartItem;
import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.classes.FilterData;
import com.katic.centralisedfoodorder.classes.GroupItem;
import com.katic.centralisedfoodorder.classes.Restaurant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.katic.centralisedfoodorder.R.id.tabHost;

public class ChooseActivity extends BaseActivity {

    private static final String TAG = "ChooseActivity";

    private boolean doubleBackToExitPressedOnce = false;
    public static List<GroupItem> cart = new ArrayList<>();
    private int count = 0;

    private DatabaseReference mDatabase;
    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    public static List<Restaurant> restaurants = new ArrayList<>();
    public static List<Restaurant> restaurantsFilter = new ArrayList<>();
    public static List<Restaurant> restaurantsFilterBookmarks = new ArrayList<>();
    public static List<Long> bookmarks;
    private List<FilterData> filterData = new ArrayList<>();

    private RecyclerView listview;
    private RecyclerView listview2;
    private RecyclerView rv;
    private RecyclerView rv2;
    private TabHost host;

    private boolean filtered = false;
    private boolean bookmarksFiltered = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        MenuItem cartMenu = menu.findItem(R.id.cart);
        MenuItem phoneMenu = menu.findItem(R.id.phone);
        phoneMenu.setVisible(false);
        if(user.isAnonymous()) {
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
            case R.id.cart:
                Intent checkout = new Intent(ChooseActivity.this, CartActivity.class);
                startActivity(checkout);
                return true;
            case R.id.orderHistory:
                Intent historyIntent = new Intent(ChooseActivity.this, OrderHistoryActivity.class);
                startActivity(historyIntent);
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        showProgressDialog();

        bookmarks = new ArrayList<>();
        //filterData = fill_with_data();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Postavljanje naslova Action Baru
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.choose_restaurant);
        actionBar.setElevation(4);
        actionBar.collapseActionView();

        //Pozivanje metode za postavljanje tabova
        setupTabs();

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

                    //Učitavanje iz baze ID-ova restorana koji su bookmark-ani
                    mUserReference.child("bookmarks").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            bookmarks.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Long item = (Long) snapshot.getValue();
                                bookmarks.add(item);
                            }

                            //Učitavanje vrijednosti za sve restorane i postavljanje oznake ako su prethodno bookmark-ani
                            mDatabase.child("restaurants").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    restaurants.clear();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Restaurant currentRes = snapshot.getValue(Restaurant.class);

                                        for (int i = 0; i<bookmarks.size(); i++){
                                            if (bookmarks.get(i)==currentRes.getRestaurantID())
                                                currentRes.setBookmarked(true);
                                        }

                                        restaurants.add(currentRes);
                                    }
                                    initializeAdapter();
                                    hideProgressDialog();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            mDatabase.child("filterData").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    filterData.clear();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        FilterData data = snapshot.getValue(FilterData.class);

                                        filterData.add(data);
                                    }
                                    initializeHorizontalAdapter();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //Učitavanje stavki koje su u košarici i njihovo prebrojavanje za potrebe prikaza ikone košarice
                    mUserReference.child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            cart.clear();
                            count=0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                GroupItem item = new GroupItem();
                                item.setTitle(snapshot.getKey());
                                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                    CartItem cart = snapshot1.getValue(CartItem.class);
                                    ChildItem child = new ChildItem(cart);
                                    item.getItems().add(child);
                                    count++;
                                }
                                cart.add(item);
                            }
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

        mAuth.addAuthStateListener(mAuthListener);

    }

    //Metoda za postavljanje Tab-ova, povezivanje s maketom i definiranje funkcija prilikom klika na horizontalnu listu
    private void setupTabs(){
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

        listview = (RecyclerView) findViewById(R.id.listview);
        listview2 = (RecyclerView) findViewById(R.id.listview2);

        rv = (RecyclerView) findViewById(R.id.restaurantList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        rv2 = (RecyclerView) findViewById(R.id.restaurantList2);
        LinearLayoutManager llm2 = new LinearLayoutManager(this);
        rv2.setLayoutManager(llm2);

    }

    //Metoda za osvježavanje prikaza restorana prilikom označavanja filtera ili bookmark zvijezdice
    public void refresh(List<Long> bookmarks, boolean bookmarksTag){
        initializeAdapter(bookmarksTag);
        mUserReference.child("bookmarks").setValue(bookmarks);
    }


    public void refresh(ArrayList<Integer> items, boolean bookmarks, boolean filtered){
        if (!bookmarks){
            this.filtered = filtered;
            restaurantsFilter.clear();
            for (int i=0; i<restaurants.size(); i++) {
                int count=0;
                for (int j = 0; j < restaurants.get(i).getFood_type().size(); j++){
                    for (int z = 0; z < items.size(); z++) {
                        for (int y = 0; y < filterData.size(); y ++) {
                            if (items.get(z) == y) {
                                if (restaurants.get(i).getFood_type().get(j).equals(filterData.get(y).getId()))
                                    count++;
                            }
                        }
                    }
                }
                if (count==items.size()) restaurantsFilter.add(restaurants.get(i));
            }
        } else if(bookmarks) {
            bookmarksFiltered = filtered;
            restaurantsFilterBookmarks.clear();
            for (int i = 0; i < restaurants.size(); i++) {
                int count = 0;
                for (int j = 0; j < restaurants.get(i).getFood_type().size(); j++) {
                    for (int z = 0; z < items.size(); z++) {
                        for (int y = 0; y < filterData.size(); y++) {
                            if (items.get(z) == y) {
                                if (restaurants.get(i).getFood_type().get(j).equals(filterData.get(y).getId()))
                                    count++;
                            }
                        }
                    }
                }
                if (count == items.size()) restaurantsFilterBookmarks.add(restaurants.get(i));
            }
        }
        initializeAdapter();
    }

    //Inicijalizacija adaptera za Recycle liste
    private void initializeAdapter(){
        rv.setAdapter(new RVAdapter(this, false, filtered));
        rv2.setAdapter(new RVAdapter(this, true, bookmarksFiltered));
    }

    private void initializeAdapter(boolean bookmarksTag){
        if(bookmarksTag)
        rv.setAdapter(new RVAdapter(this, false, filtered));
        else
        rv2.setAdapter(new RVAdapter(this, true, bookmarksFiltered));
    }

    private void initializeHorizontalAdapter(){
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(ChooseActivity.this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager horizontalLayoutManager2 = new LinearLayoutManager(ChooseActivity.this, LinearLayoutManager.HORIZONTAL, false);

        listview.setLayoutManager(horizontalLayoutManager);
        listview2.setLayoutManager(horizontalLayoutManager2);

        listview.setAdapter(new HorizontalAdapter(filterData, false, ChooseActivity.this));
        listview2.setAdapter(new HorizontalAdapter(filterData, true, ChooseActivity.this));
    }

    @Override
    public void onStart() {
        super.onStart();
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



}
