package com.katic.centralisedfoodorder;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.katic.centralisedfoodorder.adapter.HorizontalListView;
import com.katic.centralisedfoodorder.adapter.RVAdapter;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.katic.centralisedfoodorder.R.id.tabHost;

public class ChooseActivity extends BaseActivity {

    private DatabaseReference mDatabase;

    public static List<Restaurant> restaurants;
    private RecyclerView rv;
    private RecyclerView rv2;
    private TabHost host;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        mDatabase = FirebaseDatabase.getInstance().getReference();

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

        HorizontalListView listview = (HorizontalListView) findViewById(R.id.listview);
        listview.setAdapter(new HAdapter());

        HorizontalListView listview2 = (HorizontalListView) findViewById(R.id.listview2);
        listview2.setAdapter(new HAdapter());
        listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                initializeAdapter();
            }
        });

        rv = (RecyclerView) findViewById(R.id.restaurantList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        rv2 = (RecyclerView) findViewById(R.id.restaurantList2);
        LinearLayoutManager llm2 = new LinearLayoutManager(this);
        rv2.setLayoutManager(llm2);

        initializeData();
        initializeAdapter();

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            public void onTabChanged(String tabId) {
                initializeAdapter();
            }
        });

        //writeNewRestaurant(4, "ime", "adresa", 34253);
    }

    private void writeNewRestaurant(int resId, String name, String address, int photoId) {
        Restaurant res = new Restaurant(resId, name, address, photoId);

        mDatabase.child("restaurants").child(Integer.toString(resId)).setValue(res);
    }

    private void initializeData() {
        restaurants = new ArrayList<>();
        restaurants.add(new Restaurant(1, "Karaka", "Kneza Trpimira 16", R.drawable.karaka));
            restaurants.add(new Restaurant(2, "Rustika", "Ul. Pavla Pejačevića 32", R.drawable.rustika));
        restaurants.add(new Restaurant(3, "Oliva", "Kninska ul. 24", R.drawable.oliva));
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
            int resID = getResources().getIdentifier(string, "drawable", getPackageName());
            ImageView image = (ImageView) retval.findViewById(R.id.image);
            image.setImageResource(resID);

            return retval;
        }

    }


}
