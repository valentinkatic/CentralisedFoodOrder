package com.katic.centralisedfoodorder.ui.restaurantdetails;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
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
import com.katic.centralisedfoodorder.data.models.Food;
import com.katic.centralisedfoodorder.data.models.Restaurant;
import com.katic.centralisedfoodorder.data.remote.FirebaseHandler;
import com.katic.centralisedfoodorder.ui.PresenterInjector;
import com.katic.centralisedfoodorder.utils.Utils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.katic.centralisedfoodorder.utils.Utils.setBadgeCount;

public class RestaurantDetailsActivity extends AppCompatActivity implements RestaurantDetailsContract.View, FoodAdapter.FoodListener {

    private RestaurantDetailsContract.Presenter mPresenter;

    private RestaurantOfferAdapter mRestaurantOfferAdapter;

    @BindView(R.id.details_view) RelativeLayout mDetailsView;
    @BindView(R.id.iv_restaurant_image) ImageView mIvRestaurantImage;
    @BindView(R.id.tv_restaurant_name) TextView mTvRestaurantName;
    @BindView(R.id.tv_restaurant_address) TextView mTvRestaurantAddress;
    @BindView(R.id.home_screen_pb) LottieAnimationView mProgressBar;
    @BindView(R.id.nsv) NestedScrollView mNestedScrollView;
    @BindView(R.id.tl_food_type) TabLayout mTlFoodType;
    @BindView(R.id.vp_food) ViewPager mVpFood;

    private ActionBar mActionBar;
    private LayerDrawable mMenuIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        ButterKnife.bind(this);

        PresenterInjector.injectRestaurantDetailsPresenter(this);

        initializeUI();
    }

    @OnClick(R.id.tv_restaurant_address) void onAddressPressed(){
        mPresenter.onAddressClicked();
    }

    private int mLastPagerPosition = 0;

    private void initializeUI(){
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRestaurantOfferAdapter = new RestaurantOfferAdapter(this);
        mVpFood.setAdapter(mRestaurantOfferAdapter);
        mVpFood.setCurrentItem(mLastPagerPosition);
        mTlFoodType.setupWithViewPager(mVpFood);

        mVpFood.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mLastPagerPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void showRestaurantDetails(Restaurant restaurant, boolean allowedCart) {
        mDetailsView.setVisibility(View.VISIBLE);

        if (mActionBar != null){
            mActionBar.setTitle(restaurant.getName());
        }

        mTvRestaurantName.setText(restaurant.getName());
        mTvRestaurantAddress.setText(String.format(Locale.getDefault(), "%s, %s", restaurant.getAddress(), restaurant.getCity()));

        StorageReference storageRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child(FirebaseHandler.REF_RESTAURANTS_NODE)
                .child(restaurant.getKey())
                .child(restaurant.getName() + ".png");
        GlideApp.with(getApplicationContext())
                .load(storageRef)
                .into(mIvRestaurantImage);

        mRestaurantOfferAdapter.loadRestaurantOffer(restaurant.getFoodTypeList(), restaurant.getFoodList(), allowedCart);
    }

    @Override
    public void onAmountChanged(Food food, boolean increased) {
//        Toast.makeText(this, "Količina promjenjena za " + food.getTitle() + " na: " + food.getAmount(), Toast.LENGTH_SHORT).show();
        mPresenter.onCartItemAmountChanged(food);
    }

    @Override
    public void cartNotAllowedError() {
        onError(RestaurantDetailsContract.KEY_ERROR_CART_RESTAURANT);
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
    public void startMapsApp(String address) {
        Utils.startMaps(this, address);
    }

    @Override
    public void updateCartIcon(int size) {
        if (mMenuIcon != null) {
            setBadgeCount(this, mMenuIcon, size);
        }
    }

    @Override
    public void onError(int errorCode) {
        switch (errorCode) {
            case RestaurantDetailsContract.KEY_ERROR_CART_RESTAURANT:
                Toast.makeText(this, R.string.no_more_than_one, Toast.LENGTH_SHORT).show();
                break;
            case RestaurantDetailsContract.KEY_ERROR_SIZE_NOT_CHECKED:
                Toast.makeText(this, R.string.size_not_checked, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                break;
        }
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
    public void navigateToActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start(getIntent().getExtras());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_restaurant_details, menu);MenuItem menuCart = menu.findItem(R.id.menu_cart);
        mMenuIcon = (LayerDrawable) menuCart.getIcon();
        setBadgeCount(this, mMenuIcon, 0);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_cart:
                mPresenter.onCartClicked();
                break;
            case R.id.menu_phone:
                mPresenter.onPhoneClicked();
                break;
            case R.id.menu_order_history:
                mPresenter.onOrderHistoryClicked();
                break;
            case R.id.menu_logout:
                mPresenter.signOut(this);
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
