package com.katic.centralisedfoodorder.ui.restaurantdetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.katic.centralisedfoodorder.GlideApp;
import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.adapter.AnimatedExpandableListView;
import com.katic.centralisedfoodorder.data.models.Restaurant;
import com.katic.centralisedfoodorder.data.remote.FirebaseHandler;
import com.katic.centralisedfoodorder.ui.PresenterInjector;
import com.katic.centralisedfoodorder.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantDetailsActivity extends AppCompatActivity implements RestaurantDetailsContract.View {

    private RestaurantDetailsContract.Presenter mPresenter;

    private Restaurant mRestaurant;

    @BindView(R.id.details_view) RelativeLayout mDetailsView;
    @BindView(R.id.iv_restaurant_image) ImageView mIvRestaurantImage;
    @BindView(R.id.tv_restaurant_name) TextView mTvRestaurantName;
    @BindView(R.id.tv_restaurant_address) TextView mTvRestaurantAddress;
    @BindView(R.id.lv_food_list) AnimatedExpandableListView mTvListView;
    @BindView(R.id.home_screen_pb) LottieAnimationView mProgressBar;

    private ActionBar mActionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        ButterKnife.bind(this);

        PresenterInjector.injectRestaurantDetailsPresenter(this);

        initializeUI();
    }

    private void initializeUI(){
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void showRestaurantDetails(Restaurant restaurant) {
        if (restaurant != null){
            mDetailsView.setVisibility(View.VISIBLE);
        }

        this.mRestaurant = restaurant;
        if (mActionBar != null){
            mActionBar.setTitle(restaurant.getName());
        }

        mTvRestaurantName.setText(restaurant.getName());
        mTvRestaurantAddress.setText(restaurant.getAddress());

        StorageReference storageRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child(FirebaseHandler.REF_RESTAURANTS_NODE)
                .child(restaurant.getKey())
                .child(restaurant.getName() + ".png");
        GlideApp.with(getApplicationContext())
                .load(storageRef)
                .into(mIvRestaurantImage);

    }

    @Override
    public void showInvalidInput() {
        Toast.makeText(this, getString(R.string.invalid_input_provided),
                Toast.LENGTH_SHORT).show();
        dismissView();
    }

    @Override
    public void dialPhone(String phoneNumber) {
        Utils.dialNumber(this, phoneNumber);
    }

    @Override
    public void onError() {
        Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismissView() {
        RestaurantDetailsActivity.this.finish();
        RestaurantDetailsActivity.this.overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }

    @Override
    public void setPresenter(RestaurantDetailsContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start(getIntent().getExtras());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_restaurant_details, menu);
        MenuItem cartMenu = menu.findItem(R.id.menu_cart);
        cartMenu.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_cart:
                break;
            case R.id.menu_phone:
                mPresenter.onPhoneClicked(mRestaurant.getPhone());
                break;
            case R.id.menu_order_history:
                break;
            case R.id.menu_logout:
                break;
            case android.R.id.home:
                this.dismissView();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showLoading() {
        mDetailsView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        dismissView();
    }
}
