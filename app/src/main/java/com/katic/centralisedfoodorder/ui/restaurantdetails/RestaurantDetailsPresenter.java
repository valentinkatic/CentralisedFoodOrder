package com.katic.centralisedfoodorder.ui.restaurantdetails;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.katic.centralisedfoodorder.data.DataHandler;
import com.katic.centralisedfoodorder.data.DataHandlerProvider;
import com.katic.centralisedfoodorder.data.models.Food;
import com.katic.centralisedfoodorder.data.models.Restaurant;

public class RestaurantDetailsPresenter implements RestaurantDetailsContract.Presenter{

    private RestaurantDetailsContract.View mView;
    private DataHandler mDataHandler;

    private String mCurrentRestaurantId;

    public RestaurantDetailsPresenter(RestaurantDetailsContract.View mView) {
        this.mView = mView;
        this.mDataHandler = DataHandlerProvider.provide();

        mView.setPresenter(this);
    }

    @Override
    public void onFoodClicked(Food food) {

    }

    @Override
    public void onPhoneClicked(String phoneNumber) {
        if (phoneNumber != null && phoneNumber.trim().length() > 0){
            mView.dialPhone(phoneNumber);
        } else {
            mView.onError();
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
                mView.showRestaurantDetails(result);
                mView.hideLoading();
            }

            @Override
            public void onError() {
                mView.onError();
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
