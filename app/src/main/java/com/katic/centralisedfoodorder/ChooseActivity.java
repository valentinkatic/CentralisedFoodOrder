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
import com.katic.centralisedfoodorder.classes.CartItem;
import com.katic.centralisedfoodorder.classes.ChildItem;
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

    public static List<Restaurant> restaurants;
    public static List<Restaurant> restaurantsFilter = new ArrayList<>();
    public static List<Long> bookmarks;
    HorizontalListView listview;
    HorizontalListView listview2;
    private RecyclerView rv;
    private RecyclerView rv2;
    private TabHost host;
    private ImageView image;

    private ArrayList<Integer> items;
    private boolean filtered;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        MenuItem menuItem = menu.findItem(R.id.cart);
        if(count!=0)
            menuItem.setIcon(buildCounterDrawable(count, R.drawable.ic_full_cart));
        else menuItem.setIcon(R.drawable.empty_cart);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                if (count!=0) {
                    Intent checkout = new Intent(ChooseActivity.this, CartActivity.class);
                    startActivity(checkout);
                } else
                    Toast.makeText(this, R.string.empty_cart, Toast.LENGTH_SHORT).show();
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
        items = new ArrayList<>();

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
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(items.size()!=0){
                for (int j=items.size()-1; j>=0; j--){
                    if(items.get(j)==i) {
                        items.remove(j);
                        break;
                    } else {
                        items.add(i);
                        break;
                    }
                }} else
                items.add(i);
                if(items.size()==0) filtered=false; else filtered=true;
                refresh(bookmarks);
            }
        });

        listview2 = (HorizontalListView) findViewById(R.id.listview2);
        listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(items.size()!=0){
                    for (int j=items.size()-1; j>=0; j--){
                        if(items.get(j)==i) {
                            items.remove(j);
                            break;
                        } else {
                            items.add(i);
                            break;
                        }
                    }} else
                    items.add(i);
                if(items.size()==0) filtered=false; else filtered=true;
                refresh(bookmarks);
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
                refresh(bookmarks);
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

                            mDatabase.addValueEventListener(new ValueEventListener() {
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

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mUserReference.child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
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


    }

    public void refresh(List<Long> bookmarks){
        restaurantsFilter.clear();
        for (int i=0; i<restaurants.size(); i++) {
            int count=0;
            for (int j = 0; j < restaurants.get(i).food_type.size(); j++){
                for (int z = 0; z < items.size(); z++) {
                    for (int y = 2; y < dataObjects.length; y += 3) {
                        if (items.get(z) == Integer.parseInt(dataObjects[y])) {
                            if (restaurants.get(i).food_type.get(j).equals(dataObjects[y-1]))
                            count++;
                        }
                    }
                }
            }
            if (count==items.size()) restaurantsFilter.add(restaurants.get(i));
        }
        initializeAdapter();
        mUserReference.child("bookmarks").setValue(bookmarks);
    }

    private void initializeAdapter(){
        rv.setAdapter(new RVAdapter(this, false, filtered));
        rv2.setAdapter(new RVAdapter(this, true, filtered));
    }

    private static String[] dataObjects = new String[]{
            "Pizza", "pizza", "0",
            "Meksicka", "mexican", "1",
            "Talijanska", "italian", "2",
            "Kineska", "chinese", "3",
            "Vegetarijanska", "vegetarian", "4"
    };

    private class HAdapter extends BaseAdapter {

        public HAdapter() {
            super();
        }

        public int getCount() {
            return dataObjects.length / 3;
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
            title.setText(dataObjects[position * 3]);
            String string = dataObjects[position * 3 + 1];
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

        mAuth.addAuthStateListener(mAuthListener);

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
