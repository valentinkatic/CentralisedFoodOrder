package com.katic.centralisedfoodorder.ui.restaurantdetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.katic.centralisedfoodorder.data.DataHandler;
import com.katic.centralisedfoodorder.data.DataHandlerProvider;
import com.katic.centralisedfoodorder.data.models.Cart;
import com.katic.centralisedfoodorder.data.models.CartItem;
import com.katic.centralisedfoodorder.data.models.Food;
import com.katic.centralisedfoodorder.data.models.Pizza;
import com.katic.centralisedfoodorder.data.models.Restaurant;
import com.katic.centralisedfoodorder.ui.cart.CartActivity;

import java.util.ArrayList;

public class RestaurantDetailsPresenter implements RestaurantDetailsContract.Presenter{

    public static final String TAG = RestaurantDetailsPresenter.class.getSimpleName();

    private RestaurantDetailsContract.View mView;
    private DataHandler mDataHandler;

    private String mCurrentRestaurantKey;
    private Restaurant mRestaurant;
    private Cart mCart;

    public RestaurantDetailsPresenter(RestaurantDetailsContract.View mView) {
        this.mView = mView;
        this.mDataHandler = DataHandlerProvider.provide();

        mView.setPresenter(this);
    }

    @Override
    public void onCartItemAmountChanged(Food food) {
        if (mCart == null){
            mCart = new Cart();
            mCart.setRestaurantKey(mCurrentRestaurantKey);
            mCart.setRestaurantName(mRestaurant.getName());
            mCart.setCartItems(new ArrayList<CartItem>());
        }
        if (!mCart.getRestaurantKey().equals(mCurrentRestaurantKey)){
            mView.onError(RestaurantDetailsContract.KEY_ERROR_CART_RESTAURANT);
            return;
        }
        boolean isInCart = false;
        Pizza checkedPizza = null;

        if (food.getPizza() != null && food.getPizza().size() > 0){
            for (Pizza pizza: food.getPizza()){
                if (pizza.isChecked()){
                    checkedPizza = pizza;
                    break;
                }
            }
            if (checkedPizza == null){
                mView.onError(RestaurantDetailsContract.KEY_ERROR_SIZE_NOT_CHECKED);
                return;
            }
        }

        for (CartItem cartItem: mCart.getCartItems()){
            if (cartItem.getTitle().equals(food.getTitle()) && cartItem.getType().equals(food.getFoodType())){
                if (checkedPizza == null) {
                    isInCart = true;
                    if (food.getAmount() == 0) {
                        mCart.getCartItems().remove(cartItem);
                        if (mCart.getCartItems() == null || mCart.getCartItems().size() == 0) {
                            mCart = null;
                        }
                    } else {
                        cartItem.setAmount(food.getAmount());
                    }
                    break;
                } else {
                    if (cartItem.getSize().equals(checkedPizza.getSize())){
                        isInCart = true;
                        if (checkedPizza.getAmount() == 0) {
                            mCart.getCartItems().remove(cartItem);
                            if (mCart.getCartItems() == null || mCart.getCartItems().size() == 0) {
                                mCart = null;
                            }
                        } else {
                            cartItem.setAmount(checkedPizza.getAmount());
                        }
                        break;
                    }
                }
            }
        }
        if (!isInCart){
            CartItem cartItem = new CartItem();
            if (checkedPizza != null) {
                cartItem.setPrice(checkedPizza.getPrice());
                cartItem.setSize(checkedPizza.getSize());
                cartItem.setAmount(checkedPizza.getAmount());
            } else {
                cartItem.setPrice(food.getPrice());
                cartItem.setAmount(food.getAmount());
            }
            cartItem.setTitle(food.getTitle());
            cartItem.setType(food.getFoodType());
            mCart.getCartItems().add(cartItem);
        }
        mDataHandler.updateUserCart(mCart, new DataHandler.Callback<Void>() {
            @Override
            public void onResponse(Void result) {
                Log.d(TAG, "successfully added to cart");
            }

            @Override
            public void onError() {
                Log.d(TAG, "failed adding to cart");

            }
        });
        mView.updateCartIcon(mCart == null ? 0 : mCart.getCartItems().size());
    }

    @Override
    public void onPhoneClicked() {
        if (mRestaurant.getPhone() != null && mRestaurant.getPhone().trim().length() > 0){
            mView.dialPhone(mRestaurant.getPhone());
        } else {
            mView.onError(RestaurantDetailsContract.KEY_ERROR_UNKNOWN);
        }
    }

    @Override
    public void start(@Nullable Bundle extras) {
        if (extras == null || !extras.containsKey(RestaurantDetailsContract.KEY_RESTAURANT_ID)) {
            mView.showInvalidInput();
            return;
        }

        this.mCurrentRestaurantKey = extras.getString(RestaurantDetailsContract.KEY_RESTAURANT_ID);

        mView.showLoading();
        mDataHandler.fetchRestaurantById(mCurrentRestaurantKey, new DataHandler.Callback<Restaurant>() {
            @Override
            public void onResponse(Restaurant restaurant) {
                mRestaurant = restaurant;

                if (restaurant == null){
                    mView.onError(RestaurantDetailsContract.KEY_ERROR_UNKNOWN);
                    return;
                }

                mDataHandler.getMyCart(new DataHandler.Callback<Cart>() {
                    @Override
                    public void onResponse(Cart cart) {
                        mCart = cart;
                        if (mCart == null || mCart.getRestaurantKey().equals(mCurrentRestaurantKey)){
                            mView.showRestaurantDetails(mRestaurant, true);
                        } else {
                            mView.showRestaurantDetails(mRestaurant, false);
                        }
                        mView.updateCartIcon(mCart == null ? 0 : mCart.getCartItems().size());
                        mView.hideLoading();
                    }

                    @Override
                    public void onError() {
                        mView.showRestaurantDetails(mRestaurant, false);
                        mView.hideLoading();
                    }
                });
            }

            @Override
            public void onError() {
                mView.onError(RestaurantDetailsContract.KEY_ERROR_UNKNOWN);
                mView.hideLoading();
            }
        });

    }

    @Override
    public void onCartClicked() {
        mView.navigateToActivity(CartActivity.class);
    }

    @Override
    public void onOrderHistoryClicked() {

    }

    @Override
    public void onLogout() {

    }

    @Override
    public void destroy() {
        this.mView = null;
        this.mDataHandler = null;
    }
}
