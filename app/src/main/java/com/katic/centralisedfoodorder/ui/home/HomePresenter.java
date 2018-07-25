package com.katic.centralisedfoodorder.ui.home;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.katic.centralisedfoodorder.data.DataHandler;
import com.katic.centralisedfoodorder.data.DataHandlerProvider;
import com.katic.centralisedfoodorder.data.models.Restaurant;

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
    public void onLogoutClicked() {

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
            }

            @Override
            public void onError() {
                mView.onRestaurantLoadError();
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
