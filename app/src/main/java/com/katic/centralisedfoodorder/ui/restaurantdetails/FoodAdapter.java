package com.katic.centralisedfoodorder.ui.restaurantdetails;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.data.models.Food;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder>{

    private List<Food> mFoodList;
    private FoodListener mFoodListener;

    public FoodAdapter(List<Food> foodList, FoodListener foodListener){
        this.mFoodList = foodList;
        this.mFoodListener = foodListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_food_title) TextView tvFoodTitle;
        @BindView(R.id.tv_food_ingredients) TextView tvFoodIngredients;
        @BindView(R.id.tv_food_price) TextView tvFoodPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int position){
            final Food food = mFoodList.get(position);

            tvFoodTitle.setText(food.getTitle());
            if (food.getIngredients()!=null) {
                tvFoodIngredients.setVisibility(View.VISIBLE);
                tvFoodIngredients.setText(food.getIngredients());
            }
            if (food.getPizza()!=null){
                tvFoodPrice.setVisibility(View.GONE);
            }
            if (food.getPrice() != 0){
                tvFoodPrice.setVisibility(View.VISIBLE);
                tvFoodPrice.setText(String.format(Locale.getDefault(), "%.2f kn", food.getPrice()));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFoodListener.onFoodClick(food);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mFoodList == null ? 0 : mFoodList.size();
    }

        public interface FoodListener {
        void onFoodClick(Food food);
    }
}
