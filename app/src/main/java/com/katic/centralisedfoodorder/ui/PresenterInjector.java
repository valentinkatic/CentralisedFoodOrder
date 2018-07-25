package com.katic.centralisedfoodorder.ui;

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

//    public static void injectProfilePresenter(ProfileContract.View profileView) {
//        new ProfilePresenter(profileView);
//    }

    public static void injectHomePresenter(HomeContract.View homeView) {
        new HomePresenter(homeView);
    }

//    public static void injectQuizDiscussionPresenter(QuizDiscussionContract.View quizDiscussionView) {
//        new QuizDiscussionPresenter(quizDiscussionView);
//    }

//    public static void injectNotificationPresenter(NotificationContract.View notificationView) {
//        new NotificationPresenter(notificationView);
//    }

    public static void injectRestaurantDetailsPresenter(RestaurantDetailsContract.View restaurantDetailsView) {
        new RestaurantDetailsPresenter(restaurantDetailsView);
    }

//    public static void injectQuizAttemptPresenter(AttemptQuizContract.View view) {
//        new AttemptQuizPresenter(view);
//    }
}
