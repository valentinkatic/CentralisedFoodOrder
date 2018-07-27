package com.katic.centralisedfoodorder.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.katic.centralisedfoodorder.data.DataHandler;
import com.katic.centralisedfoodorder.data.DataHandlerProvider;
import com.katic.centralisedfoodorder.data.models.Cart;
import com.katic.centralisedfoodorder.data.models.Restaurant;
import com.katic.centralisedfoodorder.ui.cart.CartActivity;
import com.katic.centralisedfoodorder.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class HomePresenter implements HomeContract.Presenter {

    private DataHandler mDataHandler;
    private HomeContract.View mView;

    private List<Restaurant> mRestaurants;

    public HomePresenter(HomeContract.View view) {
        this.mView = view;
        this.mDataHandler = DataHandlerProvider.provide();

        mRestaurants = new ArrayList<>();

        // This should be the last statement
        this.mView.setPresenter(this);
    }

    @Override
    public void onRestaurantClicked(Restaurant restaurant) {
        mView.navigateToRestaurantDetails(restaurant);
    }

    @Override
    public void onBookmarkStatusChange(Restaurant restaurant) {
        mDataHandler.updateRestaurantBookmarkStatus(restaurant.getKey(), restaurant.isBookmarked(), new DataHandler.Callback<Void>() {
            @Override
            public void onResponse(Void result) {

            }

            @Override
            public void onError() {

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
    public void start(@Nullable Bundle extras) {
        mView.showLoading();
        mDataHandler.fetchRestaurants(new DataHandler.Callback<List<Restaurant>>() {
            @Override
            public void onResponse(List<Restaurant> result) {
                mRestaurants.clear();
                mRestaurants.addAll(result);
                mView.loadRestaurants(result);
                mView.hideLoading();

                mDataHandler.getMyCart(new DataHandler.Callback<Cart>() {
                    @Override
                    public void onResponse(Cart cart) {
                        mView.updateCartIcon(cart == null ? 0 : cart.getCartItems().size());
                    }

                    @Override
                    public void onError() {
                        mView.updateCartIcon(0);
                    }
                });
            }

            @Override
            public void onError() {
                mView.onRestaurantLoadError();
                mView.hideLoading();
            }
        });
    }

    @Override
    public void signOut(Activity context) {
        mDataHandler.destroy();
        Utils.signOut(context);
    }

    @Override
    public void destroy() {
        this.mView = null;
        this.mDataHandler = null;
    }
}
