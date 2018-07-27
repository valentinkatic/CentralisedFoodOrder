package com.katic.centralisedfoodorder.ui;

import com.katic.centralisedfoodorder.ui.cart.CartContract;
import com.katic.centralisedfoodorder.ui.cart.CartPresenter;
import com.katic.centralisedfoodorder.ui.home.HomeContract;
import com.katic.centralisedfoodorder.ui.home.HomePresenter;
import com.katic.centralisedfoodorder.ui.restaurantdetails.RestaurantDetailsContract;
import com.katic.centralisedfoodorder.ui.restaurantdetails.RestaurantDetailsPresenter;
import com.katic.centralisedfoodorder.ui.signin.SignInContract;
import com.katic.centralisedfoodorder.ui.signin.SignInPresenter;

/**
 * Encapsulates creation of all Presenters
 */
public class PresenterInjector {

    public static void injectSignInPresenter(SignInContract.View signInView) {
        new SignInPresenter(signInView);
    }

    public static void injectHomePresenter(HomeContract.View homeView) {
        new HomePresenter(homeView);
    }

    public static void injectRestaurantDetailsPresenter(RestaurantDetailsContract.View restaurantDetailsView) {
        new RestaurantDetailsPresenter(restaurantDetailsView);
    }

    public static void injectCartPresenter(CartContract.View cartView) {
        new CartPresenter(cartView);
    }

}
