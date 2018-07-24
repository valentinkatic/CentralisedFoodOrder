package com.katic.centralisedfoodorder.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.katic.centralisedfoodorder.GlideApp;
import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.classes.FilterData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private List<FilterData> filterData = Collections.emptyList();

    private ArrayList<Integer> items = new ArrayList<>();
    private ArrayList<Integer> bookmarkedItems = new ArrayList<>();
    private boolean filtered;
    private boolean bookmarksFiltered;

    private Context context;
    private boolean bookmarks;
    private FirebaseStorage storageRef = FirebaseStorage.getInstance();
    private StorageReference pathReference;

    public HorizontalAdapter(Context context, List<FilterData> filterData) {
        this.filterData = filterData;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView title;
        LinearLayout itemLayout;
        public MyViewHolder(View view) {
            super(view);
            imageView= view.findViewById(R.id.image);
            title= view.findViewById(R.id.title);
            itemLayout= view.findViewById(R.id.itemLayout);
        }

    }



    @Override
    public HorizontalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, parent, false);

        return new HorizontalAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HorizontalAdapter.MyViewHolder holder, final int position) {

        //Učitavanje slika iz baze u horizontalnu listu
        pathReference = storageRef.getReference("filterPhotos/"+ filterData.get(position).getId() +".png");
        GlideApp.with(context)
                .load(pathReference)
                .into(holder.imageView);

        holder.title.setText(filterData.get(position).getTitle());
        holder.itemLayout.setSelected(selectedItems.get(position, false));

        //Spremanje pozicije kliknutog filtera u SparseBooleanArray
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (selectedItems.get(position, false)) {
                    selectedItems.delete(position);
                    holder.itemLayout.setSelected(false);
                } else {
                    selectedItems.put(position, true);
                    holder.itemLayout.setSelected(true);
                }

                if (!bookmarks) {
                    items = editFilter(items, position);
                    if (items.size()!=0) filtered=true;
                    else filtered = false;
//                    ((ChooseActivity)context).refresh(items, false, filtered);
                } else if (bookmarks) {
                    bookmarkedItems = editFilter(bookmarkedItems, position);
                    if (bookmarkedItems.size()!=0) bookmarksFiltered=true;
                    else bookmarksFiltered = false;
//                    ((ChooseActivity)context).refresh(bookmarkedItems, true, bookmarksFiltered);
                }
            }
        });

    }

    private ArrayList<Integer> editFilter(ArrayList<Integer> items, Integer position){
        if (items.size() != 0) {
            for (int j = items.size() - 1; j >= 0; j--) {
                if (items.get(j) == position) {
                    items.remove(j);
                    break;
                } else {
                    items.add(position);
                    break;
                }
            }
        } else
            items.add(position);
        return items;
    }

    @Override
    public int getItemCount()
    {
        return filterData.size();
    }
}