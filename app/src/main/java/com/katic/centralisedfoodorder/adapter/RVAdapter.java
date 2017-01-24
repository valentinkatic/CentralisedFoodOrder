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

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.katic.centralisedfoodorder.ChooseActivity;
import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.classes.Restaurant;
import com.katic.centralisedfoodorder.RestaurantActivity;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RestaurantViewHolder> {

    public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener

    {
        CardView cv;
        TextView restaurantName;
        TextView restaurantAddress;
        ImageView restaurantPhoto;
        ImageView bookmark;
        boolean restaurantPressed = false;
        Long pos;

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
                        checkID();
                        intent.putExtra(ID, pos);
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

        private void checkID(){
            pos = res.get(getAdapterPosition()).restaurantID;
            if (bookmarks) {
                pos = res.get(marks.get(getAdapterPosition())).restaurantID;
            }
        }
    }

    public final static String ID = "com.katic.centralisedfoodorded.ID";

    private FirebaseStorage storageRef = FirebaseStorage.getInstance();
    private StorageReference pathReference;

    private List<Restaurant> res = ChooseActivity.restaurants;
    private List<Long> mBookmarks = ChooseActivity.bookmarks;
    private List<Integer> marks = new ArrayList<>();
    private boolean bookmarks;
    private Context context;

    public RVAdapter(Context context, final boolean bookmarks, boolean filtered) {
        this.context = context;
        this.bookmarks = bookmarks;
        if (filtered) res = ChooseActivity.restaurantsFilter;
    }

    @Override
    public int getItemCount() {
        if (bookmarks) {
            int bookmarked = 0;
            for (int i = 0; i < res.size() ; i++) {
                if (res.get(i).bookmarked) {
                    bookmarked++;
                    marks.add(i);
                }
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
            Restaurant current = res.get(i);
            restaurantViewHolder.restaurantName.setText(current.name);
            restaurantViewHolder.restaurantAddress.setText(current.address);
            pathReference = storageRef.getReference("restaurants/"+current.restaurantID+"/"+current.name+".png");
            Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(pathReference)
                .into(restaurantViewHolder.restaurantPhoto);
            if (!current.bookmarked) {
                restaurantViewHolder.bookmark.setImageResource(R.drawable.btn_pressed_off);
            } else {
                restaurantViewHolder.bookmark.setImageResource(R.drawable.btn_pressed_on);
            }
            restaurantViewHolder.bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = restaurantViewHolder.getAdapterPosition();
                    if (bookmarks) {pos = marks.get(pos);}
                    if (!res.get(pos).bookmarked) {
                        restaurantViewHolder.bookmark.setImageResource(R.drawable.btn_pressed_on);
                        res.get(pos).bookmarked=true;
                        mBookmarks.add(res.get(pos).restaurantID);
                    } else {
                        restaurantViewHolder.bookmark.setImageResource(R.drawable.btn_pressed_off);
                        res.get(pos).bookmarked=false;
                        for(int i=0; i<mBookmarks.size(); i++){
                            if(mBookmarks.get(i)==res.get(pos).restaurantID)mBookmarks.remove(i);
                        }
                    }
                    ((ChooseActivity)context).refresh(mBookmarks);
                }
            });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
