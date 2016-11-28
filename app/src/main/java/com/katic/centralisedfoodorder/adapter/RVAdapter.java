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
import android.widget.Toast;

import com.katic.centralisedfoodorder.ChooseActivity;
import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.Restaurant;
import com.katic.centralisedfoodorder.RestaurantActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static android.net.Uri.parse;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RestaurantViewHolder> {

    public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener

    {
        CardView cv;
        TextView restaurantName;
        TextView restaurantAddress;
        ImageView restaurantPhoto;
        ImageView bookmark;
        boolean restaurantPressed = false;

        private SparseBooleanArray selectedItems = new SparseBooleanArray();

        RestaurantViewHolder(View itemView) {
            super(itemView);
            itemView.setOnTouchListener(this);
            cv = (CardView) itemView.findViewById(R.id.cv);
            restaurantName = (TextView) itemView.findViewById(R.id.restaurantName);
            restaurantAddress = (TextView) itemView.findViewById(R.id.restaurantAddress);
            restaurantPhoto = (ImageView) itemView.findViewById(R.id.restaurantPhoto);
            bookmark = (ImageView) itemView.findViewById(R.id.bookmark);
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

    private List<Restaurant> res = ChooseActivity.restaurants;
    boolean bookmarks;
    Context context;
    private List<Integer> marks = new ArrayList<>();

    public RVAdapter(Context context, boolean bookmarks) {
        this.context = context;
        this.bookmarks = bookmarks;
    }

    @Override
    public int getItemCount() {
        if (bookmarks) {
            int bookmarked = 0;
            for (int i = 0; i < res.size() ; i++) {
                /*if (res.get(i).isBookmarked()) {
                    bookmarked++;
                    marks.add(i);
                }*/
            }
            return bookmarked;
        } else
        return res.size();
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RestaurantViewHolder rvh = null;
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_restaurant, viewGroup, false);
        rvh = new RestaurantViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(final RestaurantViewHolder restaurantViewHolder, int i) {
        if (bookmarks) {i = marks.get(i);}
            restaurantViewHolder.restaurantName.setText(res.get(i).name);
            restaurantViewHolder.restaurantAddress.setText(res.get(i).address);
            //restaurantViewHolder.restaurantPhoto.setImageURI(parse(res.get(i).photoId));
            /*if (!res.get(i).isBookmarked()) {
                restaurantViewHolder.bookmark.setImageResource(R.drawable.btn_pressed_off);
            } else {
                restaurantViewHolder.bookmark.setImageResource(R.drawable.btn_pressed_on);
            }
            restaurantViewHolder.bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = restaurantViewHolder.getAdapterPosition();
                    if (bookmarks) {pos = marks.get(pos);}
                    if (!res.get(pos).isBookmarked()) {
                        restaurantViewHolder.bookmark.setImageResource(R.drawable.btn_pressed_on);
                        res.get(pos).setBookmarked(true);
                    } else {
                        restaurantViewHolder.bookmark.setImageResource(R.drawable.btn_pressed_off);
                        res.get(pos).setBookmarked(false);
                    }
                }
            });*/
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
