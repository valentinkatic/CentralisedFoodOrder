package com.katic.centralisedfoodorder.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.data.models.Restaurant;
import com.katic.centralisedfoodorder.ui.PresenterInjector;
import com.katic.centralisedfoodorder.utils.Connectivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements HomeContract.View, RestaurantAdapter.RestaurantItemListener {

    public static final String TAG = HomeActivity.class.getSimpleName();

    private static final int BACK_PRESS_DURATION = 3000;

    private HomeContract.Presenter mPresenter;

    private RestaurantAdapter adapter;

    @BindView(R.id.refresh_homescreen) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_restaurants) RecyclerView mRestaurantsRecyclerView;
    @BindView(R.id.empty_view) TextView mTVNoData;
    @BindView(R.id.home_screen_pb) LottieAnimationView progressBar;

    boolean mTwiceClicked = false;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        PresenterInjector.injectHomePresenter(this);

        initializeUI();

        if (Connectivity.isNetworkAvailable(this)) {
            mPresenter.start(getIntent().getExtras());
        } else {
            noInternetMessage();
        }

        setUpSwipeRefresh();
    }

    private void initializeUI(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRestaurantsRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                linearLayoutManager.getOrientation());
        mRestaurantsRecyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new RestaurantAdapter(this);
        mRestaurantsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onRestaurantClicked(Restaurant restaurant) {
        Log.d(TAG, "clicked restaurant: " + restaurant.getName());
        mPresenter.onRestaurantClicked(restaurant);
    }

    @Override
    public void onBookmarkStatusChanged(Restaurant restaurant) {
        Log.d(TAG, "bookmark status changed for restaurant: " + restaurant.getName());
        mPresenter.onBookmarkStatusChange(restaurant);
    }

    @Override
    public void loadRestaurants(List<Restaurant> restaurants) {
        if (!restaurants.isEmpty()) {
            mTVNoData.setVisibility(View.GONE);
        }
        mRestaurantsRecyclerView.setVisibility(View.VISIBLE);
        adapter.loadRestaurants(restaurants);
    }

    @Override
    public void onRestaurantLoadError() {
        Toast.makeText(this, "Restaurant load error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleEmptyView() {
        mTVNoData.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void noInternetMessage() {
        mRestaurantsRecyclerView.setVisibility(View.GONE);
        mTVNoData.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void setUpSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.bnv_color, R.color.blue_jeans,
                R.color.ufo_green, R.color.vivid_tangelo);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.start(getIntent().getExtras());

                swipeRefreshLayout.setRefreshing(true);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                            showSnackBar(R.string.refreshed);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, BACK_PRESS_DURATION);
            }
        });
    }

    private void showSnackBar(int string) {
        String msg = getResources().getString(string);
        mSnackbar = Snackbar.make(findViewById(R.id.parent_view), msg, Snackbar.LENGTH_LONG);
        final View snackbarView = mSnackbar.getView();
        TextView tvSnackbar = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        tvSnackbar.setTextColor(getResources().getColor(R.color.colorAccent));
        snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        mSnackbar.show();

        snackbarView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                snackbarView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mTwiceClicked) {
            finishAffinity();
            mSnackbar.dismiss();
        } else {
            mTwiceClicked = true;
            showSnackBar(R.string.home_back_btn_msg);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTwiceClicked = false;
                }
            }, BACK_PRESS_DURATION);
        }
    }

}
