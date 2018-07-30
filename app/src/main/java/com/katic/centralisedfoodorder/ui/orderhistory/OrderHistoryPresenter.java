package com.katic.centralisedfoodorder.ui.orderhistory;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.katic.centralisedfoodorder.data.DataHandler;
import com.katic.centralisedfoodorder.data.DataHandlerProvider;
import com.katic.centralisedfoodorder.data.models.Cart;
import com.katic.centralisedfoodorder.ui.cart.CartActivity;
import com.katic.centralisedfoodorder.utils.Utils;

import java.util.List;

public class OrderHistoryPresenter implements OrderHistoryContract.Presenter {

    private OrderHistoryContract.View mView;
    private DataHandler mDataHandler;

    private List<Cart> mOrderHistory;

    public OrderHistoryPresenter(OrderHistoryContract.View mView) {
        this.mView = mView;
        mDataHandler = DataHandlerProvider.provide();

        mView.setPresenter(this);
    }

    @Override
    public void start(@Nullable Bundle extras) {
        mDataHandler.getMyOrderHistory(new DataHandler.Callback<List<Cart>>() {
            @Override
            public void onResponse(List<Cart> result) {
                mOrderHistory = result;
                if (result == null || result.size() == 0){
                    mView.handleEmptyView();
                    return;
                }
                mView.loadOrderHistory(result);
            }

            @Override
            public void onError() {

            }
        });

        mDataHandler.getMyCart(new DataHandler.Callback<Cart>() {
            @Override
            public void onResponse(Cart result) {
                if (result != null && result.getCartItems() != null) {
                    mView.updateCartIcon(result.getCartItems().size());
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void removeOrder(Cart order) {
        mOrderHistory.remove(order);
        mDataHandler.removeOrder(order.getOrderKey(), new DataHandler.Callback<Void>() {
            @Override
            public void onResponse(Void result) {

            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void addToCart(Cart order) {
        final Cart cart = Cart.copyCart(order);
        mDataHandler.updateUserCart(cart, new DataHandler.Callback<Void>() {
            @Override
            public void onResponse(Void result) {
                mView.updateCartIcon(cart.getCartItems().size());
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void destroy() {
        this.mView = null;
        this.mDataHandler = null;
    }

    @Override
    public void onOrderHistoryClicked() {
        //do nothing
    }

    @Override
    public void onCartClicked() {
        mView.navigateToActivity(CartActivity.class);
    }

    @Override
    public void signOut(Activity context) {
        mDataHandler.destroy();
        Utils.signOut(context);
    }
}
