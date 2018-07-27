package com.katic.centralisedfoodorder.ui.home;

import com.katic.centralisedfoodorder.data.models.Restaurant;
import com.katic.centralisedfoodorder.ui.BasePresenter;
import com.katic.centralisedfoodorder.ui.BaseView;

import java.util.List;

public interface HomeContract {

    interface View extends BaseView<Presenter> {

        void loadRestaurants(List<Restaurant> restaurants);

        void onRestaurantLoadError();

        void navigateToRestaurantDetails(Restaurant restaurant);

        void updateCartIcon(int size);

        void handleEmptyView();

    }

    interface Presenter extends BasePresenter {

        void onRestaurantClicked(Restaurant restaurant);

        void onBookmarkStatusChange(Restaurant restaurant);

        void onLogoutClicked();
    }

}
