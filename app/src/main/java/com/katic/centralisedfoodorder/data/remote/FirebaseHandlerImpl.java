package com.katic.centralisedfoodorder.data.remote;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.katic.centralisedfoodorder.data.models.Restaurant;
import com.katic.centralisedfoodorder.data.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All the firebase related stuff in this class and its parent directory. Firebase dependencies
 * should not propagate outside of this package.
 * <p>
 * TODO change description after implementation
 */
public class FirebaseHandlerImpl implements FirebaseHandler{

    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_PHONE_TOKEN = "phoneToken";
    private static final String KEY_USER_BOOKMARKS = "bookmarks";

    private DatabaseReference mUsersRef;
    private DatabaseReference mRestaurantsRef;

    private List<ValueEventListener> mValueListeners;

    // Private variables
    private FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

    FirebaseHandlerImpl() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();

        mValueListeners = new ArrayList<>();

        mUsersRef = rootRef.child(REF_USERS_NODE);
        mRestaurantsRef = rootRef.child(REF_RESTAURANTS_NODE);
    }

    @Override
    public void fetchRestaurants(final Callback<List<Restaurant>> callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot != null) {
                    List<Restaurant> restaurantList = new ArrayList<>();
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        try {
                            Restaurant singleRestaurant = childSnapshot.getValue(Restaurant.class);
                            if (singleRestaurant != null) {
                                singleRestaurant.setName(childSnapshot.getKey());
                                restaurantList.add(singleRestaurant);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onResponse(restaurantList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError();
            }
        };

//        Query quizzesRefQuery = mQuizzesRef.orderByChild(KEY_LAST_MODIFIED);

        // TODO: Implement pagination here.
//        if (limitToFirst > 0) {
//            quizzesRefQuery.limitToFirst(limitToFirst);
//        }
//        quizzesRefQuery.addValueEventListener(listener);
        mRestaurantsRef.addValueEventListener(listener);
        mValueListeners.add(listener);
    }

    @Override
    public void fetchRestaurantById(String restaurantId, final Callback<Restaurant> callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot != null) {
                    Restaurant singleRestaurant = snapshot.getValue(Restaurant.class);
                    if (singleRestaurant != null) {
                        singleRestaurant.setName(snapshot.getKey());
                        callback.onResponse(singleRestaurant);
                    } else {
                        callback.onError();
                    }
                } else {
                    callback.onError();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError();
            }
        };

        mRestaurantsRef.child(restaurantId).addValueEventListener(listener);
        mValueListeners.add(listener);
    }

    @Override
    public void fetchUserInfo(String userIdentifier, final Callback<User> callback) {
        if (mCurrentUser == null) {
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        if (userIdentifier == null) {
            userIdentifier = mCurrentUser.getUid();
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

        mUsersRef.child(userIdentifier).addValueEventListener(listener);
        mValueListeners.add(listener);
    }

    @Override
    public void setUserInfo(User currentUser, final Callback<Void> callback) {
        // Here we are not using setValue directly as that will overwrite the entire object and
        // we want to save bookmarks and attempted quizzes. Hence calling updateChildren

        Map<String, Object> userData = new HashMap<>();
        userData.put(KEY_USER_EMAIL, currentUser.getEmail());
        userData.put(KEY_PHONE_TOKEN, FirebaseInstanceId.getInstance().getToken());

        if (mCurrentUser == null) {
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
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
    public void getMyBookmarks(final Callback<List<String>> callback) {
        if (mCurrentUser == null) {
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot != null) {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError();
            }
        };

        mUsersRef.child(mCurrentUser.getUid()).child(KEY_USER_BOOKMARKS)
                .addValueEventListener(listener);
    }

    @Override
    public void destroy() {
        // Remove all listeners
        for (ValueEventListener listener : mValueListeners) {
            mRestaurantsRef.removeEventListener(listener);
            mUsersRef.removeEventListener(listener);
        }
    }
}
