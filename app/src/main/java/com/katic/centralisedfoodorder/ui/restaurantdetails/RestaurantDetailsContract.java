package com.katic.centralisedfoodorder.ui.restaurantdetails;

import com.katic.centralisedfoodorder.data.models.Food;
import com.katic.centralisedfoodorder.data.models.Restaurant;
import com.katic.centralisedfoodorder.ui.BasePresenter;
import com.katic.centralisedfoodorder.ui.BaseView;

public interface RestaurantDetailsContract {

    String KEY_RESTAURANT_ID = "restaurant_id";

    int KEY_ERROR_UNKNOWN = 300;
    int KEY_ERROR_CART_RESTAURANT = 301;
    int KEY_ERROR_SIZE_NOT_CHECKED = 302;

    interface View extends BaseView<Presenter> {

        void showRestaurantDetails(Restaurant restaurant, boolean allowedCart);

        void showInvalidInput();

        void dialPhone(String phoneNumber);

        void updateCartIcon(int size);

        void navigateToActivity(Class activity);

        void onError(int errorCode);

        void dismissView();

    }

    interface Presenter extends BasePresenter {

        void onCartItemAmountChanged(Food food);

        void onPhoneClicked();

        void onCartClicked();

        void onOrderHistoryClicked();

        void onLogout();

    }
}
