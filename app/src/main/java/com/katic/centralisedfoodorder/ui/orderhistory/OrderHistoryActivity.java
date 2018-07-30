package com.katic.centralisedfoodorder.ui.orderhistory;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.data.models.Cart;
import com.katic.centralisedfoodorder.ui.PresenterInjector;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.katic.centralisedfoodorder.utils.Utils.setBadgeCount;

public class OrderHistoryActivity extends AppCompatActivity implements OrderHistoryContract.View, OrderHistoryAdapter.OrderHistoryListener{

    private OrderHistoryContract.Presenter mPresenter;

    @BindView(R.id.elv_order_history) ExpandableListView mOrderHistoryExpandableListView;
    @BindView(R.id.tv_empty_order_history) TextView mTvEmptyOrderHistory;

    private LayerDrawable mMenuIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        ButterKnife.bind(this);

        initializeUi();

        PresenterInjector.injectOrderHistoryPresenter(this);

        mPresenter.start(getIntent().getExtras());
    }

    private void initializeUi(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.orderHistory);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void loadOrderHistory(List<Cart> orderHistory) {
        mTvEmptyOrderHistory.setVisibility(View.GONE);
        mOrderHistoryExpandableListView.setVisibility(View.VISIBLE);

        OrderHistoryAdapter adapter = new OrderHistoryAdapter(this, orderHistory, this);
        mOrderHistoryExpandableListView.setAdapter(adapter);
    }

    @Override
    public void handleEmptyView() {
        mTvEmptyOrderHistory.setVisibility(View.VISIBLE);
        mOrderHistoryExpandableListView.setVisibility(View.GONE);
    }

    @Override
    public void removeOrder(Cart order) {
        mPresenter.removeOrder(order);
    }

    @Override
    public void addToCart(Cart order) {
        mPresenter.addToCart(order);
    }

    @Override
    public void dismissView() {
        finish();
    }

    @Override
    public void setPresenter(OrderHistoryContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void navigateToActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        finish();
    }

    @Override
    public void updateCartIcon(int size) {
        if (mMenuIcon != null) {
            setBadgeCount(this, mMenuIcon, size);
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        MenuItem menuCart = menu.findItem(R.id.menu_cart);
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
            case R.id.menu_logout:
                mPresenter.signOut(this);
                return true;
            case android.R.id.home:
                dismissView();
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
