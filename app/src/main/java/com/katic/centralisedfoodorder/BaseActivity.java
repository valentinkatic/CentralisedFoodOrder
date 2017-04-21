package com.katic.centralisedfoodorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.katic.centralisedfoodorder.classes.CartItem;
import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.classes.DeliveryAddress;
import com.katic.centralisedfoodorder.classes.FilterData;
import com.katic.centralisedfoodorder.classes.GroupItem;
import com.katic.centralisedfoodorder.classes.Restaurant;
import com.katic.centralisedfoodorder.classes.User;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    protected static FirebaseAuth mAuth;
    protected static FirebaseAuth.AuthStateListener mAuthListener;
    protected static DatabaseReference mDatabase;
    protected static DatabaseReference mUserReference;
    protected static FirebaseUser user;

    protected static ValueEventListener restaurantsValueListener;
    protected static ValueEventListener bookmarksValueListener;
    protected static ValueEventListener cartValueListener;
    protected static ValueEventListener filterDataValueListener;
    protected static ValueEventListener orderHistoryListener;

    private static List<Restaurant> restaurants;
    private static List<Restaurant> restaurantsFilter;
    private static List<Restaurant> restaurantsFilterBookmarks;
    private static List<FilterData> filterData;
    private static List<Long> bookmarks;
    private static List<GroupItem> cart;
    private static List<GroupItem> orderHistory;
    private static int count = 0;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        showProgressDialog();

        filterData = new ArrayList<>();
        bookmarks = new ArrayList<>();
        restaurants = new ArrayList<>();
        cart = new ArrayList<>();
        orderHistory = new ArrayList<>();

        //Listener za podatke filteriranja vrsta jela
        filterDataValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filterData.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FilterData data = snapshot.getValue(FilterData.class);

                    filterData.add(data);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //Listener za restorane
        restaurantsValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                restaurants.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant currentRes = snapshot.getValue(Restaurant.class);

                    for (int i = 0; i<bookmarks.size(); i++){
                        if (bookmarks.get(i)==currentRes.getRestaurantID())
                            currentRes.setBookmarked(true);
                    }

                    restaurants.add(currentRes);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //Listener za bookmarkove
        bookmarksValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookmarks.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Long item = (Long) snapshot.getValue();
                    bookmarks.add(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //Listener za stavke u košarici
        cartValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cart.clear();
                count=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GroupItem item = new GroupItem();
                    item.setTitle(snapshot.getKey());
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        CartItem cart = snapshot1.getValue(CartItem.class);
                        ChildItem child = new ChildItem(cart);
                        item.getItems().add(child);
                        count++;
                    }
                    cart.add(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //Listener za povijest narudžbi
        orderHistoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderHistory.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GroupItem item = snapshot.getValue(GroupItem.class);
                    orderHistory.add(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    mUserReference = FirebaseDatabase.getInstance().getReference()
                            .child("users").child(getUid());

                    mDatabase.child("filterData").addValueEventListener(filterDataValueListener);
                    mUserReference.child("bookmarks").addValueEventListener(bookmarksValueListener);
                    mDatabase.child("restaurants").addValueEventListener(restaurantsValueListener);
                    mUserReference.child("cart").addValueEventListener(cartValueListener);
                    mUserReference.child("orderHistory").addValueEventListener(orderHistoryListener);

                    hideProgressDialog();
                }

                else

                    hideProgressDialog();
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    protected void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getText(R.string.loading));
        }

        mProgressDialog.show();
    }

    protected void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static List<Long> getBookmarks(){
        return bookmarks;
    }

    public static List<Restaurant> getRestaurants(){
        return restaurants;
    }

    public static List<GroupItem> getCart(){
        return cart;
    }

    public static int getCount(){
        return count;
    }

    public static void setCount(int i){
        count = i;
    }

    public static List<FilterData> getFilterData(){
        return filterData;
    }

    public static List<GroupItem> getOrderHistory(){
        return orderHistory;
    }

    //Prijava pomoću e-maila i lozinke
    protected void signInWithEmailAndPassword(String email, String password, final String TAG) {
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(getApplicationContext(), R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();

                        if (task.isSuccessful()) {

                            Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(), R.string.login_success,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Prijava neregistiranog korisnika
    protected void signInAnonymously(final String TAG){
        showProgressDialog();
        if(user==null){
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                            hideProgressDialog();

                            if (task.isSuccessful()) {
                                User mUser = new User();
                                mDatabase.child("users").child(getUid()).setValue(mUser);
                                Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInAnonymously", task.getException());
                                Toast.makeText(getApplicationContext(), R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else if (user.isAnonymous()) {
            hideProgressDialog();
            Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //Metoda za kreiranje korisnika s e-mailom i lozinkom
    protected void createUser(final User user, final String password, final String TAG){
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), R.string.auth_success,
                                    Toast.LENGTH_SHORT).show();
                            mDatabase.child("users").child(getUid()).setValue(user);
                            Intent intent = new Intent(getApplicationContext(), ChooseActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    }
                });
    }



}
