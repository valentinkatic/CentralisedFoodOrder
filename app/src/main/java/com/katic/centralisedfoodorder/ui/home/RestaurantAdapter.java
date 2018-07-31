package com.katic.centralisedfoodorder.ui.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.data.models.Restaurant;
import com.katic.centralisedfoodorder.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private final static String TAG = RestaurantAdapter.class.getSimpleName();

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.restaurantName) TextView restaurantName;
        @BindView(R.id.restaurantAddress) TextView restaurantAddress;
        @BindView(R.id.restaurantPhoto) ImageView restaurantPhoto;
        @BindView(R.id.bookmark) ImageView bookmark;

        Restaurant current;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        void bind(int position){
            current = mRestaurants.get(position);
            Context context = itemView.getContext();

            restaurantName.setText(current.getName());
            restaurantAddress.setText(String.format(Locale.getDefault(), "%s, %s", current.getAddress(), current.getCity()));
            GlideApp.with(context)
                    .load(current.getPhotoURL())
                    .into(restaurantPhoto);

            if (!current.isBookmarked()) {
                bookmark.setImageResource(R.drawable.btn_pressed_off);
            } else {
                bookmark.setImageResource(R.drawable.btn_pressed_on);
            }

            bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current.setBookmarked(!current.isBookmarked());
                    if (!current.isBookmarked()) {
                        bookmark.setImageResource(R.drawable.btn_pressed_off);
                    } else {
                        bookmark.setImageResource(R.drawable.btn_pressed_on);
                    }
                    mRestaurantItemListener.onBookmarkStatusChanged(current);
                }
            });
        }

        @Override
        public void onClick(View v) {
            mRestaurantItemListener.onRestaurantClicked(current);
        }
    }

    private List<Restaurant> mRestaurants;
    private RestaurantItemListener mRestaurantItemListener;

    public RestaurantAdapter(RestaurantItemListener restaurantItemListener) {
        mRestaurants = new ArrayList<>();
        mRestaurantItemListener = restaurantItemListener;
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_restaurant, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    public void loadRestaurants(@NonNull List<Restaurant> restaurants){
        mRestaurants.clear();
        mRestaurants.addAll(restaurants);
        notifyDataSetChanged();
    }

    public interface RestaurantItemListener {
        void onRestaurantClicked(Restaurant restaurant);

        void onBookmarkStatusChanged(Restaurant restaurant);
    }
}
