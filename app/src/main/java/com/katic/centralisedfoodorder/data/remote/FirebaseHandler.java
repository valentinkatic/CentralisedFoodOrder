package com.katic.centralisedfoodorder.data.remote;

import com.katic.centralisedfoodorder.data.models.Cart;
import com.katic.centralisedfoodorder.data.models.DeliveryAddress;
import com.katic.centralisedfoodorder.data.models.FilterData;
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

    String REF_FILTER_DATA_NODE = "filter_data";
    String REF_RESTAURANT_DATA_NODE = "restaurant_data";
    String REF_RESTAURANTS_NODE = "restaurants";
    String REF_USERS_NODE = "users";

    void fetchRestaurants(Callback<List<Restaurant>> callback);

    void fetchRestaurantById(String restaurantId, Callback<Restaurant> callback);

    void updateUserName(String userName, final Callback<Void> callback);

    void fetchUserInfo(String userIdentifier, Callback<User> callback);

    void fetchUserAddresses(String userIdentifier, Callback<List<DeliveryAddress>> callback);

    void setUserInfo(User currentUser, Callback<Void> callback);

    void fetchUserPhoneToken(String userIdentifier, Callback<String> callback);

    void updateRestaurantBookmarkStatus(String quizIdentifier, boolean isBookmarked, Callback<Void> callback);

    void updateUserCart(Cart cart, Callback<Void> callback);

    void updateUserAddresses(List<DeliveryAddress> addresses, Callback<Void> callback);

    void getMyBookmarks(Callback<List<String>> callback);

    void getMyCart(Callback<Cart> callback);

    void getMyOrderHistory(Callback<List<Cart>> callback);

    void removeOrder(String orderKey, Callback<Void> callback);

    void sendOrder(Cart cart, Callback<Void> callback);

    void fetchFilterData(Callback<List<FilterData>> callback);

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
