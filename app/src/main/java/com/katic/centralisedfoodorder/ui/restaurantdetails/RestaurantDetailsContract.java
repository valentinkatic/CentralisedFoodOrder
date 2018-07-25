package com.katic.centralisedfoodorder.ui.restaurantdetails;

import com.katic.centralisedfoodorder.data.models.Food;
import com.katic.centralisedfoodorder.data.models.Restaurant;
import com.katic.centralisedfoodorder.ui.BasePresenter;
import com.katic.centralisedfoodorder.ui.BaseView;

public interface RestaurantDetailsContract {

    String KEY_RESTAURANT_ID = "restaurant_id";

    interface View extends BaseView<Presenter> {

        void showRestaurantDetails(Restaurant restaurant);

        void showInvalidInput();

        void dialPhone(String phoneNumber);

        void onError();

        void dismissView();

    }

    interface Presenter extends BasePresenter {

        void onFoodClicked(Food food);

        void onPhoneClicked(String phoneNumber);

    }
}
