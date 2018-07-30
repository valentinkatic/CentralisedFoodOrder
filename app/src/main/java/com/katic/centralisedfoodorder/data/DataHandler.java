package com.katic.centralisedfoodorder.data;

import com.katic.centralisedfoodorder.data.models.Cart;
import com.katic.centralisedfoodorder.data.models.DeliveryAddress;
import com.katic.centralisedfoodorder.data.models.Restaurant;

import java.util.List;

/**
 * Data layer abstraction. All data related communication should happen via this interface. This
 * is the only point of interaction with shared preferences, local sqlite database, firebase and
 * network
 */

public interface DataHandler {

    void fetchRestaurants(Callback<List<Restaurant>> callback);

    void fetchRestaurantById(String restaurantId, Callback<Restaurant> callback);

    void fetchUserAddresses();

    void setUserInfo(Callback<Void> callback);

    void updateUserName(String userName, Callback<Void> callback);

    void saveUserName(String userName);

    String getUserName();

    void saveUserEmail(String userEmail);

    String getUserEmail();

    void saveUserLastNamePickup(String lastNamePickup);

    String getUserLastNamePickup();

    List<DeliveryAddress> getUserAddresses();

    void saveUserPhoneTokenLocally();

    String getUserPhoneToken();

    void saveUserAddresses(List<DeliveryAddress> addresses, Callback<Void> callback);

    void updateRestaurantBookmarkStatus(String restaurantIdentifier, boolean isBookmarked, Callback<Void> callback);

    void updateUserCart(Cart cart, Callback<Void> callback);

    void getMyBookmarks(Callback<List<String>> callback);

    void getMyCart(Callback<Cart> callback);

    void getMyOrderHistory(Callback<List<Cart>> callback);

    void removeOrder(String orderKey, Callback<Void> callback);

    void sendOrder(Cart cart, Callback<Void> callback);

    void destroy();
    /**
     * Generic callback interface for passing response to caller.
     * <p>
     * TODO: Replace all such callbacks with reactive programing, just pass observables
     *
     * @param <T> Type of expected response
     */
    interface Callback<T> {
        /**
         * Gets invoked when call was successful
         *
         * @param result result of the operation
         */
        void onResponse(T result);

        /**
         * Gets invoked when there is an error in the operation
         */
        void onError();
    }
}
