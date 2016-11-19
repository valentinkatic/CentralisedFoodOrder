package com.katic.centralisedfoodorder.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.Restaurant;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RestaurantViewHolder> {

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView restaurantName;
        TextView restaurantAddress;
        ImageView restaurantPhoto;

        RestaurantViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            restaurantName = (TextView) itemView.findViewById(R.id.restaurantName);
            restaurantAddress = (TextView) itemView.findViewById(R.id.restaurantAddress);
            restaurantPhoto = (ImageView) itemView.findViewById(R.id.restaurantPhoto);
        }
    }

    List<Restaurant> restaurants;

    public RVAdapter(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_restaurant, viewGroup, false);
        RestaurantViewHolder rvh = new RestaurantViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(RestaurantViewHolder restaurantViewHolder, int i) {
        restaurantViewHolder.restaurantName.setText(restaurants.get(i).name);
        restaurantViewHolder.restaurantAddress.setText(restaurants.get(i).address);
        restaurantViewHolder.restaurantPhoto.setImageResource(restaurants.get(i).photoId);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
