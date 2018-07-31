package com.katic.centralisedfoodorder.data.remote;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.katic.centralisedfoodorder.data.DataHandler;
import com.katic.centralisedfoodorder.data.models.Cart;
import com.katic.centralisedfoodorder.data.models.DeliveryAddress;
import com.katic.centralisedfoodorder.data.models.FilterData;
import com.katic.centralisedfoodorder.data.models.Food;
import com.katic.centralisedfoodorder.data.models.Restaurant;
import com.katic.centralisedfoodorder.data.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All the firebase related stuff in this class and its parent directory. Firebase dependencies
 * should not propagate outside of this package.
 */
public class FirebaseHandlerImpl implements FirebaseHandler {

    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_PHONE_TOKEN = "phoneToken";
    private static final String KEY_USER_BOOKMARKS = "bookmarks";
    private static final String KEY_USER_CART = "cart";
    private static final String KEY_USER_DELIVERY_ADDRESSES = "delivery_addresses";
    private static final String KEY_USER_ORDER_HISTORY = "order_history";
    private static final String KEY_ORDER_DATE = "order_date";

    private DatabaseReference mUsersRef;
    private DatabaseReference mRestaurantsRef;
    private DatabaseReference mRestaurantDataRef;
    private DatabaseReference mFilterDataRef;

    private List<ValueEventListener> mValueListeners;
    private HashMap<DatabaseReference, ValueEventListener> mFirebaseListeners = new HashMap<>();

    // Private variables
    private FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

    FirebaseHandlerImpl() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        firebaseDatabase.setPersistenceEnabled(true);
        DatabaseReference rootRef = firebaseDatabase.getReference();

        mValueListeners = new ArrayList<>();

        mUsersRef = rootRef.child(REF_USERS_NODE);
        mRestaurantsRef = rootRef.child(REF_RESTAURANTS_NODE);
        mRestaurantDataRef = rootRef.child(REF_RESTAURANT_DATA_NODE);
        mFilterDataRef = rootRef.child(REF_FILTER_DATA_NODE);
    }

    @Override
    public void fetchRestaurants(final Callback<List<Restaurant>> callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Restaurant> restaurantList = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Restaurant restaurant = getRestaurantFromSnapshot(childSnapshot);
                    if (restaurant != null) {
                        restaurantList.add(restaurant);
                    }
                }
                callback.onResponse(restaurantList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError();
                databaseError.toException().printStackTrace();
            }
        };

//        Query quizzesRefQuery = mQuizzesRef.orderByChild(KEY_LAST_MODIFIED);

        // TODO: Implement pagination here.
//        if (limitToFirst > 0) {
//            quizzesRefQuery.limitToFirst(limitToFirst);
//        }
//        quizzesRefQuery.addValueEventListener(listener);
        mRestaurantsRef.addListenerForSingleValueEvent(listener);
        mValueListeners.add(listener);
        mFirebaseListeners.put(mRestaurantsRef, listener);
    }

    @Override
    public void fetchRestaurantById(String restaurantId, final Callback<Restaurant> callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Restaurant restaurant = getRestaurantFromSnapshot(snapshot);
                if (restaurant != null) {
                    callback.onResponse(restaurant);
                } else {
                    callback.onError();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError();
            }
        };

        mRestaurantsRef.child(restaurantId).addListenerForSingleValueEvent(listener);
        mValueListeners.add(listener);
        mFirebaseListeners.put(mRestaurantsRef.child(restaurantId), listener);
    }

    @Override
    public void updateUserName(String userName, final Callback<Void> callback) {
        updateUserProperty(KEY_USER_NAME, userName, callback);
    }

    @Override
    public void updateUserAddresses(List<DeliveryAddress> addresses, final Callback<Void> callback) {
        try {
            if (mCurrentUser == null) {
                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            }
            if (mCurrentUser == null){
                callback.onError();
                return;
            }

            mUsersRef.child(mCurrentUser.getUid()).child(KEY_USER_DELIVERY_ADDRESSES).setValue(addresses)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            callback.onResponse(null);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onError();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            callback.onError();
        }
    }

    @Override
    public void fetchUserInfo(String userIdentifier, final Callback<User> callback) {
        if (mCurrentUser == null) {
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        if (userIdentifier == null && mCurrentUser != null) {
            userIdentifier = mCurrentUser.getUid();
        } else {
            callback.onError();
            return;
        }

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                callback.onResponse(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError();
            }
        };

        mUsersRef.child(userIdentifier).addListenerForSingleValueEvent(listener);
        mValueListeners.add(listener);
        mFirebaseListeners.put(mUsersRef.child(userIdentifier), listener);
    }

    @Override
    public void fetchUserAddresses(String userIdentifier, final Callback<List<DeliveryAddress>> callback) {
        if (mCurrentUser == null) {
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        if (userIdentifier == null && mCurrentUser != null) {
            userIdentifier = mCurrentUser.getUid();
        } else {
            callback.onError();
            return;
        }

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DeliveryAddress> addresses = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    DeliveryAddress address = childSnapshot.getValue(DeliveryAddress.class);
                    if (address != null) {
                        addresses.add(address);
                    }
                }
                callback.onResponse(addresses);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError();
            }
        };

        mUsersRef.child(userIdentifier).child(KEY_USER_DELIVERY_ADDRESSES).addListenerForSingleValueEvent(listener);
        mValueListeners.add(listener);
        mFirebaseListeners.put(mUsersRef.child(userIdentifier), listener);
    }

    @Override
    public void setUserInfo(User currentUser, final Callback<Void> callback) {
        // Here we are not using setValue directly as that will overwrite the entire object and
        // we want to save bookmarks and attempted quizzes. Hence calling updateChildren

        final Map<String, Object> userData = new HashMap<>();
        userData.put(KEY_USER_EMAIL, currentUser.getEmail());
        userData.put(KEY_USER_NAME, currentUser.getName());
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        userData.put(KEY_PHONE_TOKEN, instanceIdResult.getToken());

                        setUserInfo(userData, callback);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setUserInfo(userData, callback);
                    }
                });
    }

    @Override
    public void fetchUserPhoneToken(String userIdentifier, final Callback<String> callback) {
        if (mCurrentUser == null) {
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        if (userIdentifier == null && mCurrentUser != null) {
            userIdentifier = mCurrentUser.getUid();
        } else {
            callback.onError();
            return;
        }

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String phoneToken = snapshot.getValue(String.class);
                callback.onResponse(phoneToken);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError();
            }
        };

        mUsersRef.child(userIdentifier).child(KEY_PHONE_TOKEN).addListenerForSingleValueEvent(listener);
        mValueListeners.add(listener);
        mFirebaseListeners.put(mUsersRef.child(userIdentifier).child(KEY_PHONE_TOKEN), listener);
    }

    private void setUserInfo(Map<String, Object> userData, final Callback<Void> callback) {
        if (mCurrentUser == null) {
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        }
        if (mCurrentUser == null) {
            callback.onError();
            return;
        }

        mUsersRef.child(mCurrentUser.getUid()).updateChildren(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onResponse(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onError();
                    }
                });
    }

    @Override
    public void updateRestaurantBookmarkStatus(String restaurantIdentifier, boolean isBookmarked, final Callback<Void> callback) {
        if (mCurrentUser == null) {
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        }
        if (mCurrentUser == null) {
            callback.onError();
            return;
        }

        mUsersRef.child(mCurrentUser.getUid()).child(KEY_USER_BOOKMARKS).child(restaurantIdentifier)
                .setValue(isBookmarked)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onResponse(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onError();
                    }
                });
    }

    @Override
    public void updateUserCart(Cart cart, final Callback<Void> callback) {
        if (mCurrentUser == null) {
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        }
        if (mCurrentUser == null) {
            callback.onError();
            return;
        }

        mUsersRef.child(mCurrentUser.getUid()).child(KEY_USER_CART)
                .setValue(cart)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onResponse(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onError();
                    }
                });
    }

    @Override
    public void getMyBookmarks(final Callback<List<String>> callback) {
        if (mCurrentUser == null) {
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> bookmarks = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    try {
                        boolean isAdded = (boolean) childSnapshot.getValue();
                        if (isAdded) {
                            bookmarks.add(childSnapshot.getKey());
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                callback.onResponse(bookmarks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError();
            }
        };

        mUsersRef.child(mCurrentUser.getUid()).child(KEY_USER_BOOKMARKS)
                .addListenerForSingleValueEvent(listener);
        mFirebaseListeners.put(mUsersRef.child(mCurrentUser.getUid()).child(KEY_USER_BOOKMARKS), listener);
    }

    @Override
    public void getMyCart(final Callback<Cart> callback) {
        if (mCurrentUser == null) {
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Cart cart = snapshot.getValue(Cart.class);
                callback.onResponse(cart);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError();
            }
        };

        mUsersRef.child(mCurrentUser.getUid()).child(KEY_USER_CART)
                .addListenerForSingleValueEvent(listener);
        mFirebaseListeners.put(mUsersRef.child(mCurrentUser.getUid()).child(KEY_USER_CART), listener);
    }

    @Override
    public void getMyOrderHistory(final Callback<List<Cart>> callback) {
        if (mCurrentUser == null) {
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Cart> orderHistory = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Cart order = childSnapshot.getValue(Cart.class);
                    if (order != null) {
                        order.setOrderKey(childSnapshot.getKey());
                        orderHistory.add(order);
                    }
                }
                callback.onResponse(orderHistory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError();
            }
        };

        mUsersRef.child(mCurrentUser.getUid()).child(KEY_USER_ORDER_HISTORY).orderByChild(KEY_ORDER_DATE)
                .addListenerForSingleValueEvent(listener);
        mFirebaseListeners.put(mUsersRef.child(mCurrentUser.getUid()).child(KEY_USER_ORDER_HISTORY), listener);
    }

    @Override
    public void removeOrder(String orderKey, final Callback<Void> callback) {
        if (orderKey != null) {
            mUsersRef.child(mCurrentUser.getUid()).child(KEY_USER_ORDER_HISTORY).child(orderKey).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            callback.onResponse(null);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onError();
                        }
                    });
        }
    }

    @Override
    public void sendOrder(final Cart cart, final Callback<Void> callback) {
        final String key = mRestaurantDataRef.child(cart.getRestaurantKey()).push().getKey();
        if (key == null){
            callback.onError();
            return;
        }
        mRestaurantDataRef.child(cart.getRestaurantKey()).child(key)
                .setValue(cart)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mUsersRef.child(mCurrentUser.getUid()).child(KEY_USER_ORDER_HISTORY).child(key).setValue(cart);
                        mUsersRef.child(mCurrentUser.getUid()).child(KEY_USER_CART).setValue(null);
                        callback.onResponse(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onError();
                    }
                });
    }

    @Override
    public void fetchFilterData(final Callback<List<FilterData>> callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FilterData> filterDataList = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    FilterData filter = childSnapshot.getValue(FilterData.class);
                    if (filter != null) {
                        filterDataList.add(filter);
                    }
                }
                callback.onResponse(filterDataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError();
            }
        };

        mFilterDataRef.addListenerForSingleValueEvent(listener);
        mFirebaseListeners.put(mFilterDataRef, listener);
    }

    @Override
    public void destroy() {
        // Remove all listeners
//        for (ValueEventListener listener : mValueListeners) {
//            mRestaurantsRef.removeEventListener(listener);
//            mUsersRef.removeEventListener(listener);
//        }
//        for (Map.Entry<DatabaseReference, ValueEventListener> entry: mFirebaseListeners.entrySet()){
//            entry.getKey().removeEventListener(entry.getValue());
//        }
    }

    private Restaurant getRestaurantFromSnapshot(DataSnapshot snapshot) {
        try {
            Restaurant singleRestaurant = snapshot.getValue(Restaurant.class);
            if (singleRestaurant != null && singleRestaurant.getName() != null) {
                singleRestaurant.setKey(snapshot.getKey());

                singleRestaurant.setFoodTypeList(new ArrayList<String>());
                for (String foodType : singleRestaurant.getFoodList().keySet()) {
                    singleRestaurant.getFoodTypeList().add(foodType);
                    for (String food : singleRestaurant.getFoodList().get(foodType).keySet()) {
                        singleRestaurant.getFoodList().get(foodType).get(food).setTitle(food);
                        singleRestaurant.getFoodList().get(foodType).get(food).setFoodType(foodType);
                    }
                }

                return singleRestaurant;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateUserProperty(String property, String value, final Callback<Void> callback) {
        try {
            if (mCurrentUser == null) {
                mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            }
            if (mCurrentUser == null){
                callback.onError();
                return;
            }

            mUsersRef.child(mCurrentUser.getUid()).child(property).setValue(value)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            callback.onResponse(null);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onError();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            callback.onError();
        }
    }
}
