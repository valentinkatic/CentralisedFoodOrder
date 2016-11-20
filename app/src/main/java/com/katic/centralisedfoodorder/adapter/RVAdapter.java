package com.katic.centralisedfoodorder.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.Restaurant;
import com.katic.centralisedfoodorder.RestaurantActivity;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RestaurantViewHolder> {

    public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener

    {
        CardView cv;
        TextView restaurantName;
        TextView restaurantAddress;
        ImageView restaurantPhoto;
        boolean restaurantPressed = false;

        private SparseBooleanArray selectedItems = new SparseBooleanArray();

        RestaurantViewHolder(View itemView) {
            super(itemView);
            itemView.setOnTouchListener(this);
            cv = (CardView) itemView.findViewById(R.id.cv);
            restaurantName = (TextView) itemView.findViewById(R.id.restaurantName);
            restaurantAddress = (TextView) itemView.findViewById(R.id.restaurantAddress);
            restaurantPhoto = (ImageView) itemView.findViewById(R.id.restaurantPhoto);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    selectedItems.put(getAdapterPosition(), true);
                    v.setSelected(true);
                    restaurantPressed = true;
                    break;
                case MotionEvent.ACTION_UP:
                    selectedItems.delete(getAdapterPosition());
                    v.setSelected(false);
                    if (restaurantPressed) {
                        restaurantPressed = false;
                        Intent intent = new Intent(context, RestaurantActivity.class);
                        context.startActivity(intent);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    selectedItems.delete(getAdapterPosition());
                    v.setSelected(false);
                    break;

            }
            return true;
        }
    }

    List<Restaurant> restaurants;
    Context context;

    public RVAdapter(List<Restaurant> restaurants, Context context) {
        this.restaurants = restaurants;
        this.context = context;
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
