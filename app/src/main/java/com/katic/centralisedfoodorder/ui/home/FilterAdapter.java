package com.katic.centralisedfoodorder.ui.home;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.katic.centralisedfoodorder.data.models.FilterData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private List<FilterData> mFilterData;
    private FilterListener mFilterListener;

    public FilterAdapter(FilterListener filterListener) {
        this.mFilterData = new ArrayList<>();
        this.mFilterListener = filterListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.parent_view) LinearLayout parentView;
        @BindView(R.id.iv_filter) ImageView ivFilter;
        @BindView(R.id.tv_filter_title) TextView tvFilterTitle;

        SparseBooleanArray selectedItems = new SparseBooleanArray();
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        StorageReference pathReference;
        FilterData filter;
        Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();

            itemView.setOnClickListener(this);
        }

        void bind (int position){
            filter = mFilterData.get(position);

            pathReference = storageRef.getReference("filterPhotos/"+ filter.getId() +".png");
            GlideApp.with(context)
                    .load(pathReference)
                    .into(ivFilter);

            tvFilterTitle.setText(filter.getTitle());
            parentView.setSelected(selectedItems.get(position, false));
        }

        @Override
        public void onClick(View v) {
            if (selectedItems.get(getAdapterPosition(), false)) {
                selectedItems.delete(getAdapterPosition());
                parentView.setSelected(false);
            } else {
                selectedItems.put(getAdapterPosition(), true);
                parentView.setSelected(true);
            }

            mFilterListener.onFilterClicked(filter);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

       holder.bind(position);

    }

    public void setFilters(List<FilterData> filters){
        mFilterData = filters;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return mFilterData.size();
    }

    public interface FilterListener {

        void onFilterClicked(FilterData filter);

    }
}