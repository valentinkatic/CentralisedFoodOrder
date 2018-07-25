package com.katic.centralisedfoodorder.data.remote;

import com.katic.centralisedfoodorder.data.models.Restaurant;
import com.katic.centralisedfoodorder.data.models.User;

import java.util.List;

/**
 * The only point of interaction with firebase remote database. This contract is kept separate from
 * implementation for loose coupling
 * <p>
 * TODO change description after implementation
 */
public interface FirebaseHandler {

    String REF_FILTER_DATA_NODE = "filterData";
    String REF_RESTAURANT_DATA_NODE = "restaurantData";
    String REF_RESTAURANTS_NODE = "restaurantsTest";
    String REF_USERS_NODE = "usersTest";

    void fetchRestaurants(Callback<List<Restaurant>> callback);

    void fetchRestaurantById(String restaurantId, Callback<Restaurant> callback);

    void updateUserName(String userName, final Callback<Void> callback);

    void fetchUserInfo(String userIdentifier, Callback<User> callback);

    void setUserInfo(User currentUser, Callback<Void> callback);

    void updateRestaurantBookmarkStatus(String quizIdentifier, boolean isBookmarked, Callback<Void> callback);

    void getMyBookmarks(Callback<List<String>> callback);

    void destroy();

    /**
     * Generic callback interface for passing response to caller.
     * <p>
     * TODO Replace all such callbacks with reactive programing, just pass observables
     *
     * @param <T> Type of expected response
     */
    interface Callback<T> {
        void onResponse(T result);

        void onError();
    }
}
