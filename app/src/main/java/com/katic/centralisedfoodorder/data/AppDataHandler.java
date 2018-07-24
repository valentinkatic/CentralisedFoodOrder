package com.katic.centralisedfoodorder.data;

import android.content.Context;

import com.katic.centralisedfoodorder.application.AppClass;
import com.katic.centralisedfoodorder.data.models.Restaurant;
import com.katic.centralisedfoodorder.data.models.User;
import com.katic.centralisedfoodorder.data.remote.FirebaseHandler;
import com.katic.centralisedfoodorder.data.remote.FirebaseProvider;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class AppDataHandler implements DataHandler {

    private static AppDataHandler INSTANCE = null;

//    private PrefsHelper mPreferences;
//    private DBHandler mDBHandler;
    private FirebaseHandler mFirebaseHandler;

    private AppDataHandler() {
        Context context = AppClass.getAppContext();
//        mPreferences = PrefsHelper.getInstance(context);
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
                            if (result.getBookmarks() != null && result.getBookmarks().containsKey(restaurant.getName())) {
                                restaurant.setBookmarked(result.getBookmarks().get(restaurant.getName()));
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
            public void onResponse(Restaurant fetchedRestaurant) {
                callback.onResponse(fetchedRestaurant);
                mFirebaseHandler.destroy();
            }

            @Override
            public void onError() {
                callback.onError();
            }
        });
    }

    @Override
    public void setUserInfo(Callback<Void> callback) {
//        User currentUser = new User();
//        currentUser.setImage(mPreferences.getUserPic());
//        currentUser.setName(mPreferences.getUserName());
//        currentUser.setEmail(mPreferences.getUserEmail());
//        currentUser.setSlackHandle(mPreferences.getSlackHandle());
//        currentUser.setStatus(mPreferences.getUserStatus());
//        currentUser.setTrack(mPreferences.getUserTrack());
//
//        mFirebaseHandler.setUserInfo(currentUser, new FirebaseCallback<>(callback));
    }

    @Override
    public void getMyBookmarks(Callback<List<String>> callback) {
        mFirebaseHandler.getMyBookmarks(new FirebaseCallback<>(callback));
    }

    @Override
    public boolean isLoggedIn() {
        return false;
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
