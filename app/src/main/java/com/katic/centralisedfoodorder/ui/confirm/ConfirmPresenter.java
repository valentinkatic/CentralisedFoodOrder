package com.katic.centralisedfoodorder.ui.confirm;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.katic.centralisedfoodorder.data.DataHandler;
import com.katic.centralisedfoodorder.data.DataHandlerProvider;
import com.katic.centralisedfoodorder.data.models.Cart;
import com.katic.centralisedfoodorder.data.models.DeliveryAddress;
import com.katic.centralisedfoodorder.ui.orderhistory.OrderHistoryActivity;
import com.katic.centralisedfoodorder.utils.Utils;

import java.util.Calendar;
import java.util.List;

public class ConfirmPresenter implements ConfirmContract.Presenter {

    private ConfirmContract.View mView;
    private DataHandler mDataHandler;

    private List<DeliveryAddress> mDeliveryAddresses;
    private Cart mCart;

    public ConfirmPresenter(ConfirmContract.View mView) {
        this.mView = mView;
        this.mDataHandler = DataHandlerProvider.provide();

        this.mView.setPresenter(this);
    }

    @Override
    public void onAddressSelectClicked() {
        mView.startSelectAddressDialog(mDeliveryAddresses);
    }

    @Override
    public void onAddressSelected(final DeliveryAddress address) {
        setDefaultAddress(address, new DataHandler.Callback<Void>() {
            @Override
            public void onResponse(Void result) {
                mView.loadDefaultAddress(address);
                mView.showMessage(ConfirmContract.KEY_MESSAGE_ADDRESS_SET);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void onAddressSaveClicked(DeliveryAddress address) {
        mDeliveryAddresses.add(address);
        setDefaultAddress(address, new DataHandler.Callback<Void>() {
            @Override
            public void onResponse(Void result) {
                mView.showMessage(ConfirmContract.KEY_MESSAGE_ADDRESS_SAVED);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void setDefaultAddress(DeliveryAddress address, DataHandler.Callback<Void> callback) {
        for (DeliveryAddress da : mDeliveryAddresses){
            if (da.equals(address)){
                da.setDefault(true);
            } else {
                da.setDefault(false);
            }
        }
        mDataHandler.saveUserAddresses(mDeliveryAddresses, callback);
    }

    @Override
    public void removeAddress(DeliveryAddress address) {
        mDeliveryAddresses.remove(address);
        mDataHandler.saveUserAddresses(mDeliveryAddresses, new DataHandler.Callback<Void>() {
            @Override
            public void onResponse(Void result) {
                mView.showMessage(ConfirmContract.KEY_MESSAGE_ADDRESS_REMOVED);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void confirmDelivery(DeliveryAddress address) {
        mCart.setDeliveryAddress(address);
        mCart.setPhoneToken(mDataHandler.getUserPhoneToken());
        mCart.setOrderDate(Utils.getISO8601Date(Calendar.getInstance().getTimeInMillis()));
        mCart.setDelivery(true);
        mDataHandler.sendOrder(mCart, new DataHandler.Callback<Void>() {
            @Override
            public void onResponse(Void result) {
                mView.showMessage(ConfirmContract.KEY_MESSAGE_ORDER_SENT);
                mView.navigateToActivity(OrderHistoryActivity.class, true);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void confirmPickup(String lastName) {
        mCart.setLastNamePickup(lastName);
        mCart.setPhoneToken(mDataHandler.getUserPhoneToken());
        mCart.setOrderDate(Utils.getISO8601Date(Calendar.getInstance().getTimeInMillis()));
        mCart.setDelivery(false);
        mDataHandler.sendOrder(mCart, new DataHandler.Callback<Void>() {
            @Override
            public void onResponse(Void result) {
                mView.showMessage(ConfirmContract.KEY_MESSAGE_ORDER_SENT);
                mView.navigateToActivity(OrderHistoryActivity.class, true);
            }

            @Override
            public void onError() {

            }
        });
        mDataHandler.saveUserLastNamePickup(lastName);
    }

    @Override
    public void start(@Nullable Bundle extras) {
        mDataHandler.getMyCart(new DataHandler.Callback<Cart>() {
            @Override
            public void onResponse(Cart result) {
                if (result == null){
                    return;
                }
                mCart = result;
            }

            @Override
            public void onError() {

            }
        });

        mDeliveryAddresses = mDataHandler.getUserAddresses();
        for (DeliveryAddress address: mDeliveryAddresses){
            if (address.isDefault()){
                mView.loadDefaultAddress(address);
                break;
            }
        }

        mView.loadLastNamePickup(mDataHandler.getUserLastNamePickup());
    }

    @Override
    public void destroy() {
        mView = null;
        mDataHandler = null;
    }

    @Override
    public void onOrderHistoryClicked() {
        mView.navigateToActivity(OrderHistoryActivity.class, false);
    }

    @Override
    public void signOut(Activity context) {
        mDataHandler.destroy();
        Utils.signOut(context);
    }
}
