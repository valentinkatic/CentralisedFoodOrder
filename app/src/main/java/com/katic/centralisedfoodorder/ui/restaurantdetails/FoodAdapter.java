package com.katic.centralisedfoodorder.ui.restaurantdetails;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.data.models.Food;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<Food> mFoodList;
    private FoodListener mFoodListener;
    private boolean mCartAllowed;

    protected FoodAdapter(List<Food> foodList, FoodListener foodListener, boolean cartAllowed) {
        this.mFoodList = foodList;
        this.mFoodListener = foodListener;
        this.mCartAllowed = cartAllowed;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_food_title) TextView tvFoodTitle;
        @BindView(R.id.tv_food_ingredients) TextView tvFoodIngredients;
        @BindView(R.id.tv_food_price) TextView tvFoodPrice;
        @BindView(R.id.rl_quantity) RelativeLayout rlQuantity;
        @BindView(R.id.tv_amount) TextView tvAmount;
        Food food;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final int position) {
            food = mFoodList.get(position);

            tvFoodTitle.setText(food.getTitle());
            if (food.getIngredients() != null) {
                tvFoodIngredients.setVisibility(View.VISIBLE);
                tvFoodIngredients.setText(food.getIngredients());
            }
            if (food.getPizza() != null) {
                tvFoodPrice.setVisibility(View.GONE);
            }
            if (food.getPrice() != 0) {
                tvFoodPrice.setVisibility(View.VISIBLE);
                tvFoodPrice.setText(String.format(Locale.getDefault(), "%.2f kn", food.getPrice()));
            }
            tvAmount.setText(String.format(Locale.getDefault(), "%d", food.getAmount()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rlQuantity.setVisibility(rlQuantity.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    mFoodListener.onFoodClick(food);
                }
            });
        }

        @OnClick(R.id.iv_minus) void reduceAmount() {
            if (mCartAllowed) {
                if (food.getAmount() > 0) {
                    food.setAmount(food.getAmount() - 1);
                    tvAmount.setText(String.format(Locale.getDefault(), "%d", food.getAmount()));
                    if (food.getAmount() == 0) {
                        food.setAddedToCart(false);
                    }
                    mFoodListener.onAmountChanged(food, false);
                }
            } else {
                mFoodListener.cartNotAllowedError();
            }
        }

        @OnClick(R.id.iv_plus) void increaseAmount() {
            if (mCartAllowed) {
                if (food.getAmount() != 10) {
                    food.setAmount(food.getAmount() + 1);
                    tvAmount.setText(String.format(Locale.getDefault(), "%d", food.getAmount()));
                    food.setAddedToCart(true);
                    mFoodListener.onAmountChanged(food, true);
                }
            } else {
                mFoodListener.cartNotAllowedError();
            }
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

        void onAmountChanged(Food food, boolean increased);

        void cartNotAllowedError();

    }
}
