package com.katic.centralisedfoodorder.ui.restaurantdetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.katic.centralisedfoodorder.data.DataHandler;
import com.katic.centralisedfoodorder.data.DataHandlerProvider;
import com.katic.centralisedfoodorder.data.models.Cart;
import com.katic.centralisedfoodorder.data.models.CartItem;
import com.katic.centralisedfoodorder.data.models.Food;
import com.katic.centralisedfoodorder.data.models.Restaurant;

import java.util.ArrayList;

public class RestaurantDetailsPresenter implements RestaurantDetailsContract.Presenter{

    public static final String TAG = RestaurantDetailsPresenter.class.getSimpleName();

    private RestaurantDetailsContract.View mView;
    private DataHandler mDataHandler;

    private String mCurrentRestaurantId;
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
            mCart.setRestaurantName(mRestaurant.getName());
            mCart.setCartItems(new ArrayList<CartItem>());
        }
        if (!mCart.getRestaurantName().equals(mRestaurant.getName())){
            mView.onError(RestaurantDetailsContract.KEY_ERROR_CART_RESTAURANT);
            return;
        }
        boolean isInCart = false;
        for (CartItem cartItem: mCart.getCartItems()){
            if (cartItem.getTitle().equals(food.getTitle()) && cartItem.getType().equals(food.getFoodType())){
                isInCart = true;
                if (food.getAmount() == 0){
                    mCart.getCartItems().remove(cartItem);
                    if (mCart.getCartItems() == null || mCart.getCartItems().size() == 0){
                        mCart = null;
                    }
                } else {
                    cartItem.setAmount(food.getAmount());
                }
                break;
            }
        }
        if (!isInCart){
            CartItem cartItem = new CartItem();
            cartItem.setAmount(food.getAmount());
            cartItem.setPrice(food.getPrice());
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
    }

    @Override
    public void onPhoneClicked(String phoneNumber) {
        if (phoneNumber != null && phoneNumber.trim().length() > 0){
            mView.dialPhone(phoneNumber);
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

        this.mCurrentRestaurantId = extras.getString(RestaurantDetailsContract.KEY_RESTAURANT_ID);

        mView.showLoading();
        mDataHandler.fetchRestaurantById(mCurrentRestaurantId, new DataHandler.Callback<Restaurant>() {
            @Override
            public void onResponse(Restaurant result) {
                mRestaurant = result;

                mDataHandler.getMyCart(new DataHandler.Callback<Cart>() {
                    @Override
                    public void onResponse(Cart result) {
                        mCart = result;
                        if (mCart == null || mCart.getRestaurantName().equals(mRestaurant.getName())){
                            mView.showRestaurantDetails(mRestaurant, true);
                        } else {
                            mView.showRestaurantDetails(mRestaurant, false);
                        }
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
    public void destroy() {
        this.mView = null;
        this.mDataHandler = null;
    }
}
