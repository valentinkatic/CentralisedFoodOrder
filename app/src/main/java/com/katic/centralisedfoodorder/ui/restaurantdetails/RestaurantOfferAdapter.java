package com.katic.centralisedfoodorder.ui.restaurantdetails;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.data.models.Food;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by valentin.katic on 8.6.2017..
 */

public class RestaurantOfferAdapter extends PagerAdapter{

    private static final String TAG = RestaurantOfferAdapter.class.getSimpleName();

    private List<String> mFoodTypeList;
    private Map<String, Map<String, Food>> mFoodList;
    private FoodAdapter.FoodListener mFoodListener;
    private boolean mAllowedCart;

    @BindView(R.id.rv_food_list) RecyclerView mRvFoodList;

    public RestaurantOfferAdapter(FoodAdapter.FoodListener foodListener) {
        this.mFoodListener = foodListener;
        this.mFoodTypeList = new ArrayList<>();
    }

    private HashMap<Integer, FoodAdapter> mAdapterMap = new HashMap<>();

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.view_food_list, container, false);
        ButterKnife.bind(this, view);

        ArrayList<Food> foodList = new ArrayList<>();

        if (mFoodTypeList != null && mFoodList!=null && mFoodList.get(mFoodTypeList.get(position)).keySet().size() > 0) {
            foodList = new ArrayList<>(mFoodList.get(mFoodTypeList.get(position)).values());
        }

        mRvFoodList.setLayoutManager(new GridLayoutManager(container.getContext(), 1, LinearLayoutManager.VERTICAL, false));

        FoodAdapter foodAdapter = new FoodAdapter(mFoodListener, mAllowedCart);
        foodAdapter.swapEntries(foodList);
        mRvFoodList.setAdapter(foodAdapter);

        container.addView(view);
        mAdapterMap.put(position, foodAdapter);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        mAdapterMap.remove(position);
    }

    @Override
    public int getCount() {
        return mFoodTypeList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFoodTypeList.get(position);
    }

    public void loadRestaurantOffer(List<String> foodTypeList, Map<String, Map<String, Food>> foodList, boolean allowedCart){
        this.mFoodTypeList = foodTypeList;
        this.mFoodList = foodList;
        this.mAllowedCart = allowedCart;
        notifyDataSetChanged();
        for (Map.Entry<Integer, FoodAdapter> entry: mAdapterMap.entrySet()){
            int position = entry.getKey();
            if (mFoodTypeList != null && mFoodList!=null && mFoodList.get(mFoodTypeList.get(position)).keySet().size() > 0) {
                List<Food> list = new ArrayList<>(mFoodList.get(mFoodTypeList.get(position)).values());
                entry.getValue().swapEntries(list);
            }
        }
    }

}
