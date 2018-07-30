package com.katic.centralisedfoodorder.ui.cart;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.data.DataHandler;
import com.katic.centralisedfoodorder.data.DataHandlerProvider;
import com.katic.centralisedfoodorder.data.models.Cart;
import com.katic.centralisedfoodorder.data.models.CartItem;
import com.katic.centralisedfoodorder.ui.confirm.ConfirmActivity;
import com.katic.centralisedfoodorder.utils.Utils;

import java.util.Locale;

public class CartPresenter implements CartContract.Presenter {

    private CartContract.View mView;
    private DataHandler mDataHandler;

    private Cart mCart;

    public CartPresenter(CartContract.View mView) {
        this.mView = mView;
        this.mDataHandler = DataHandlerProvider.provide();

        mView.setPresenter(this);
    }

    @Override
    public void start(@Nullable Bundle extras) {
        mView.showLoading();
        mDataHandler.getMyCart(new DataHandler.Callback<Cart>() {
            @Override
            public void onResponse(Cart result) {
                mCart = result;
                if (result == null){
                    mView.showEmptyCart();
                } else {
                    mView.loadCart(result);
                    updateSubtotal();
                }
                mView.hideLoading();
            }

            @Override
            public void onError() {
                mView.onError();
                mView.hideLoading();
            }
        });
    }

    @Override
    public void updateCartItemAmount(CartItem cartItem) {
        for (CartItem ci: mCart.getCartItems()){
            if (ci.equals(cartItem)) {
                ci.setAmount(cartItem.getAmount());
                break;
            }
        }
        mDataHandler.updateUserCart(mCart, new DataHandler.Callback<Void>() {
            @Override
            public void onResponse(Void result) {
                updateSubtotal();
            }

            @Override
            public void onError() {
                mView.onError();
            }
        });
    }

    @Override
    public void removeCartItem(CartItem cartItem) {
        for (CartItem ci: mCart.getCartItems()){
            if (ci.equals(cartItem)) {
                mCart.getCartItems().remove(ci);
                break;
            }
        }
        if (mCart.getCartItems().size() == 0){
            mCart = null;
            mView.showEmptyCart();
        }
        mDataHandler.updateUserCart(mCart, new DataHandler.Callback<Void>() {
            @Override
            public void onResponse(Void result) {
                updateSubtotal();
            }

            @Override
            public void onError() {
                mView.onError();
            }
        });
    }

    private void updateSubtotal(){
        double subtotal = 0;
        if (mCart != null) {
            for (CartItem cartItem : mCart.getCartItems()) {
                subtotal += cartItem.getAmount() * cartItem.getPrice();
            }
        }
        mView.updateSubtotal(subtotal);
    }

    @Override
    public void onCheckoutPressed() {
        mView.navigateToActivity(ConfirmActivity.class);
    }

    @Override
    public void onOrderHistoryClicked() {

    }

    @Override
    public void signOut(Activity context) {
        mDataHandler.destroy();
        Utils.signOut(context);
    }

    @Override
    public void destroy() {
        this.mView = null;
        this.mDataHandler = null;
    }
}
