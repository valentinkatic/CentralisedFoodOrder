package com.katic.centralisedfoodorder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.classes.FilterData;
import com.katic.centralisedfoodorder.classes.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends PagerAdapter {

    private static final String TAG = "FragmentAdapter";

    private LayoutInflater mLayoutInflater;
    private ArrayList<String> tabTitles;
    private List<FilterData> filterData;
    private List<Restaurant> restaurants;

    private RecyclerView filterView;
    private RecyclerView restaurantView;

    private Context context;
    private boolean bookmarks;

    public FragmentAdapter(Context context, ArrayList<String> tabTitles, List<FilterData> filterData, List<Restaurant> restaurants) {
        mLayoutInflater = LayoutInflater.from(context);
        this.tabTitles = tabTitles;
        this.filterData = filterData;
        this.restaurants = restaurants;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.fragment_page, container, false);
        context = itemView.getContext();

        filterView = (RecyclerView) itemView.findViewById(R.id.listview);
        restaurantView = (RecyclerView) itemView.findViewById(R.id.restaurantList);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(context);

        filterView.setLayoutManager(horizontalLayoutManager);
        restaurantView.setLayoutManager(verticalLayoutManager);

        bookmarks = (position != 0);

        filterView.setAdapter(new HorizontalAdapter(context, filterData));
        initializeAdapter();

        container.addView(itemView);
        return itemView;
    }

    private void initializeAdapter(){
        restaurantView.setAdapter(new RVAdapter(context, restaurants, bookmarks));
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void notifyDataSetChanged() {
        restaurantView.getAdapter().notifyItemRangeChanged(0, restaurantView.getAdapter().getItemCount());
        super.notifyDataSetChanged();
    }
}

