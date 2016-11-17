package com.katic.centralisedfoodorder;

import com.katic.centralisedfoodorder.adapter.HorizontalListView;
import com.katic.centralisedfoodorder.adapter.RVAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChooseActivity extends Activity {

    private List<Restaurant> restaurants;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        HorizontalListView listview = (HorizontalListView) findViewById(R.id.listview);
        listview.setAdapter(new HAdapter());

        rv = (RecyclerView) findViewById(R.id.restaurantList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        initializeData();
        initializeAdapter();

    }

    private void initializeData() {
        restaurants = new ArrayList<>();
        restaurants.add(new Restaurant("Karaka", "Kneza Trpimira 16", R.drawable.karaka));
        restaurants.add(new Restaurant("Rustika", "Ul. Pavla Pejačevića 32", R.drawable.rustika));
        restaurants.add(new Restaurant("Oliva", "Kninska ul. 24", R.drawable.oliva));
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(restaurants);
        rv.setAdapter(adapter);
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