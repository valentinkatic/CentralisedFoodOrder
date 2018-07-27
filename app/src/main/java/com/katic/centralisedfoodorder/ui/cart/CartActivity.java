package com.katic.centralisedfoodorder.ui.cart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.data.models.Cart;
import com.katic.centralisedfoodorder.data.models.CartItem;
import com.katic.centralisedfoodorder.ui.PresenterInjector;
import com.katic.centralisedfoodorder.ui.signin.SignInActivity;
import com.katic.centralisedfoodorder.utils.Utils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CartActivity extends AppCompatActivity implements CartContract.View, CartAdapter.CartListener{

    private CartContract.Presenter mPresenter;

    @BindView(R.id.ll_cart_view) LinearLayout mLlCartView;
    @BindView(R.id.tv_restaurant_name) TextView mTvRestaurantName;
    @BindView(R.id.rv_cart) RecyclerView mCartRecyclerView;
    @BindView(R.id.tv_subtotal) TextView mTvSubtotal;
    @BindView(R.id.rl_empty_cart) RelativeLayout mRlEmptyCart;

    private CartAdapter mCartAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_new);
        ButterKnife.bind(this);

        PresenterInjector.injectCartPresenter(this);

        initializeUI();

        mPresenter.start(getIntent().getExtras());
    }

    private void initializeUI(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.cart);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mCartRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                linearLayoutManager.getOrientation());
        mCartRecyclerView.addItemDecoration(dividerItemDecoration);

        mCartAdapter = new CartAdapter(this);
        mCartRecyclerView.setAdapter(mCartAdapter);
    }

    @Override
    public void loadCart(Cart cart) {

        mRlEmptyCart.setVisibility(View.GONE);
        mLlCartView.setVisibility(View.VISIBLE);

        mTvRestaurantName.setText(cart.getRestaurantName());
        mCartAdapter.setCartItems(cart.getCartItems());

    }

    @OnClick(R.id.btn_checkout) void checkout(){
        mPresenter.onCheckoutPressed();
    }

    @Override
    public void onCartItemRemoved(CartItem cartItem) {
        mPresenter.removeCartItem(cartItem);
    }

    @Override
    public void onAmountChanged(CartItem cartItem) {
        mPresenter.updateCartItemAmount(cartItem);
    }

    @Override
    public void onError() {
        Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmptyCart() {
        mLlCartView.setVisibility(View.GONE);
        mRlEmptyCart.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateSubtotal(double value) {
        mTvSubtotal.setText(String.format(Locale.getDefault(), "%s: %.2f kn", getString(R.string.subtotal), value));
    }

    @Override
    public void navigateToActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    @Override
    public void dismissView() {
        finish();
    }

    @Override
    public void setPresenter(CartContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start(getIntent().getExtras());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_order_history:
                mPresenter.onOrderHistoryPressed();
                break;
            case R.id.menu_logout:
                mPresenter.signOut(this);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        dismissView();
    }
}
