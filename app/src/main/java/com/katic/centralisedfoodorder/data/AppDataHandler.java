package com.katic.centralisedfoodorder.data;

import android.content.Context;

import com.katic.centralisedfoodorder.application.AppClass;
import com.katic.centralisedfoodorder.data.models.Cart;
import com.katic.centralisedfoodorder.data.models.CartItem;
import com.katic.centralisedfoodorder.data.models.Pizza;
import com.katic.centralisedfoodorder.data.models.Restaurant;
import com.katic.centralisedfoodorder.data.models.User;
import com.katic.centralisedfoodorder.data.remote.FirebaseHandler;
import com.katic.centralisedfoodorder.data.remote.FirebaseProvider;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class AppDataHandler implements DataHandler {

    private static AppDataHandler INSTANCE = null;

    private PrefsHelper mPreferences;
//    private DBHandler mDBHandler;
    private FirebaseHandler mFirebaseHandler;

    private AppDataHandler() {
        Context context = AppClass.getAppContext();
        mPreferences = PrefsHelper.getInstance(context);
//        mDBHandler = DBHandler.getInstance(context);
        mFirebaseHandler = FirebaseProvider.provide();
    }

    static AppDataHandler getInstance() {
        if (INSTANCE == null) {
            synchronized (AppDataHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppDataHandler();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void fetchRestaurants(Callback<List<Restaurant>> callback) {
        // Fetch all the quizzes
        mFirebaseHandler.fetchRestaurants(new FirebaseCallback<List<Restaurant>>(callback) {
            @Override
            public void onResponse(final List<Restaurant> restaurants) {

                // Fetch user info to get bookmarks and attempted quizzes
                mFirebaseHandler.fetchUserInfo(null, new FirebaseHandler.Callback<User>() {
                    @Override
                    public void onResponse(User result) {
                        // Mark attempted quizzes
                        for (Restaurant restaurant : restaurants) {
                            if (result.getBookmarks() != null && result.getBookmarks().containsKey(restaurant.getKey())) {
                                restaurant.setBookmarked(result.getBookmarks().get(restaurant.getKey()));
                            }
                        }

                        callback.onResponse(restaurants);
                        mFirebaseHandler.destroy();
                    }

                    @Override
                    public void onError() {
                        callback.onError();
                    }
                });
            }
        });
    }

    @Override
    public void fetchRestaurantById(String restaurantId, Callback<Restaurant> callback) {
        mFirebaseHandler.fetchRestaurantById(restaurantId, new FirebaseCallback<Restaurant>(callback) {

            @Override
            public void onResponse(final Restaurant restaurant) {

                mFirebaseHandler.fetchUserInfo(null, new FirebaseHandler.Callback<User>() {
                    @Override
                    public void onResponse(User result) {
                        if (result.getBookmarks() != null && result.getBookmarks().containsKey(restaurant.getKey())) {
                            restaurant.setBookmarked(result.getBookmarks().get(restaurant.getKey()));
                        }
                        if (result.getCart() != null && result.getCart().getRestaurantKey().equals(restaurant.getKey()) && result.getCart().getCartItems() != null){
                            for (CartItem cartItem: result.getCart().getCartItems()){
                                if (cartItem.getSize() != null && cartItem.getSize().trim().length() > 0){
                                    for (Pizza pizza: restaurant.getFoodList().get(cartItem.getType()).get(cartItem.getTitle()).getPizza()){
                                        if (cartItem.getSize().equals(pizza.getSize())){
                                            pizza.setAddedToCart(true);
                                            pizza.setAmount(cartItem.getAmount());
                                            break;
                                        }
                                    }
                                } else {
                                    restaurant.getFoodList().get(cartItem.getType()).get(cartItem.getTitle()).setAddedToCart(true);
                                    restaurant.getFoodList().get(cartItem.getType()).get(cartItem.getTitle()).setAmount(cartItem.getAmount());
                                }
                            }
                        }

                        callback.onResponse(restaurant);
                        mFirebaseHandler.destroy();
                    }

                    @Override
                    public void onError() {
                        callback.onResponse(restaurant);
                        mFirebaseHandler.destroy();
                    }
                });
            }

            @Override
            public void onError() {
                callback.onError();
            }
        });
    }

    @Override
    public void updateUserName(String userName, Callback<Void> callback) {
        mFirebaseHandler.updateUserName(userName, new FirebaseCallback<>(callback));
    }

    @Override
    public void setUserInfo(Callback<Void> callback) {
        User currentUser = new User();
        currentUser.setName(mPreferences.getUserName());
        currentUser.setEmail(mPreferences.getUserEmail());

        mFirebaseHandler.setUserInfo(currentUser, new FirebaseCallback<>(callback));
    }

    @Override
    public void updateRestaurantBookmarkStatus(String restaurantIdentifier, boolean isBookmarked, Callback<Void> callback) {
        mFirebaseHandler.updateRestaurantBookmarkStatus(restaurantIdentifier, isBookmarked, new FirebaseCallback<>(callback));
    }

    @Override
    public void updateUserCart(Cart cart, Callback<Void> callback) {
        mFirebaseHandler.updateUserCart(cart, new FirebaseCallback<>(callback));
    }

    @Override
    public void getMyBookmarks(Callback<List<String>> callback) {
        mFirebaseHandler.getMyBookmarks(new FirebaseCallback<>(callback));
    }

    @Override
    public void getMyCart(Callback<Cart> callback) {
        mFirebaseHandler.getMyCart(new FirebaseCallback<>(callback));
    }

    @Override
    public void saveUserName(String userName) {
        mPreferences.setUserName(userName);
    }

    @Override
    public String getUserName() {
        return mPreferences.getUserName();
    }

    @Override
    public void saveUserEmail(String userEmail) {
        mPreferences.setUserEmail(userEmail);
    }

    @Override
    public String getUserEmail() {
        return mPreferences.getUserEmail();
    }

    @Override
    public void destroy() {
        mFirebaseHandler.destroy();
    }

    /**
     * internal class for converting {@link FirebaseHandler} Callback to {@link DataHandler} Callback
     *
     * @param <T> type of response that is expected
     */
    class FirebaseCallback<T> implements FirebaseHandler.Callback<T> {
        DataHandler.Callback<T> callback;

        FirebaseCallback(DataHandler.Callback<T> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(T result) {
            this.callback.onResponse(result);
        }

        @Override
        public void onError() {
            this.callback.onError();
        }
    }
}
