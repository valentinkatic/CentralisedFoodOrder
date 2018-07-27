package com.katic.centralisedfoodorder.ui.restaurantdetails;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.data.models.Food;
import com.katic.centralisedfoodorder.data.models.Pizza;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<Food> mFoodList;
    private FoodListener mFoodListener;
    private boolean mCartAllowed;

    public FoodAdapter(FoodListener foodListener, boolean cartAllowed) {
        this.mFoodListener = foodListener;
        this.mCartAllowed = cartAllowed;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PizzaSizeAdapter.PizzaSizeListener {

        @BindView(R.id.tv_food_title) TextView tvFoodTitle;
        @BindView(R.id.tv_food_ingredients) TextView tvFoodIngredients;
        @BindView(R.id.tv_food_price) TextView tvFoodPrice;
        @BindView(R.id.rl_quantity) RelativeLayout rlQuantity;
        @BindView(R.id.tv_amount) TextView tvAmount;
        @BindView(R.id.rv_pizza_size) RecyclerView rvPizzaSize;
        Food food;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
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
                PizzaSizeAdapter pizzaSizeAdapter = new PizzaSizeAdapter(food.getPizza(), this);
                rvPizzaSize.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false));
                rvPizzaSize.setAdapter(pizzaSizeAdapter);
            } else {
                tvFoodPrice.setVisibility(View.VISIBLE);
            }
            if (food.getPrice() != 0) {
                tvFoodPrice.setVisibility(View.VISIBLE);
                tvFoodPrice.setText(String.format(Locale.getDefault(), "%.2f kn", food.getPrice()));
            }
            tvAmount.setText(String.format(Locale.getDefault(), "%d", food.getAmount()));
        }

        @Override
        public void onClick(View v) {
            rlQuantity.setVisibility(rlQuantity.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            tvFoodIngredients.setMaxLines(rlQuantity.getVisibility() == View.VISIBLE ? Integer.MAX_VALUE : 1);
            if (food.getPizza() != null) {
                rvPizzaSize.setVisibility(rlQuantity.getVisibility());
            }
        }

        @OnClick(R.id.iv_minus) void reduceAmount() {
            if (mCartAllowed) {
                if (food.getPizza() == null || food.getPizza().size() == 0) {
                    if (food.getAmount() > 0) {
                        food.setAmount(food.getAmount() - 1);
                        tvAmount.setText(String.format(Locale.getDefault(), "%d", food.getAmount()));
                        if (food.getAmount() == 0) {
                            food.setAddedToCart(false);
                        }
                        mFoodListener.onAmountChanged(food, false);
                    }
                } else {
                    for (Pizza pizza: food.getPizza()) {
                        if (pizza.isChecked() && pizza.getAmount() > 0){
                            pizza.setAmount(pizza.getAmount() - 1);
                            tvAmount.setText(String.format(Locale.getDefault(), "%d", pizza.getAmount()));
                            if (pizza.getAmount() == 0) {
                                pizza.setAddedToCart(false);
                            }
                            break;
                        }
                    }
                    mFoodListener.onAmountChanged(food, false);
                }
            } else {
                mFoodListener.cartNotAllowedError();
            }
        }

        @OnClick(R.id.iv_plus) void increaseAmount() {
            if (mCartAllowed) {
                if (food.getPizza() == null || food.getPizza().size() == 0) {
                    if (food.getAmount() != 10) {
                        food.setAmount(food.getAmount() + 1);
                        tvAmount.setText(String.format(Locale.getDefault(), "%d", food.getAmount()));
                        food.setAddedToCart(true);
                        mFoodListener.onAmountChanged(food, true);
                    }
                } else {
                    for (Pizza pizza: food.getPizza()) {
                        if (pizza.isChecked() && pizza.getAmount() != 10){
                            pizza.setAmount(pizza.getAmount() + 1);
                            tvAmount.setText(String.format(Locale.getDefault(), "%d", pizza.getAmount()));
                            pizza.setAddedToCart(true);
                            break;
                        }
                    }
                    mFoodListener.onAmountChanged(food, true);
                }
            } else {
                mFoodListener.cartNotAllowedError();
            }
        }

        @Override
        public void onSizeClicked(Pizza pizza) {
            int pizzaIndex = getPizzaIndex(pizza);
            if (pizzaIndex == -1){
                tvAmount.setText(String.format(Locale.getDefault(), "%d", 0));
            } else {
                tvAmount.setText(String.format(Locale.getDefault(), "%d", food.getPizza().get(getPizzaIndex(pizza)).getAmount()));
            }

        }

        private int getPizzaIndex(Pizza pizza){
            if (food.getPizza() == null || food.getPizza().size() == 0){
                return -1;
            }
            for (int i = 0; i < food.getPizza().size(); i++){
                if (food.getPizza().get(i).getSize().equals(pizza.getSize())){
                    return pizza.isChecked() ? i : -1;
                }
            }
            return -1;
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

    public void swapEntries(List<Food> foodList){
        mFoodList = foodList;
        notifyDataSetChanged();
    }

    public interface FoodListener {

        void onAmountChanged(Food food, boolean increased);

        void cartNotAllowedError();

    }
}
