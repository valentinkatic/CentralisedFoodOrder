package com.katic.centralisedfoodorder.ui.confirm;

import android.os.Bundle;

import com.katic.centralisedfoodorder.data.DataHandler;
import com.katic.centralisedfoodorder.data.models.DeliveryAddress;
import com.katic.centralisedfoodorder.ui.BasePresenter;
import com.katic.centralisedfoodorder.ui.BaseView;

import java.util.List;

public interface ConfirmContract {

    int KEY_MESSAGE_ADDRESS_SAVED = 400;
    int KEY_MESSAGE_ADDRESS_REMOVED = 401;
    int KEY_MESSAGE_ADDRESS_SET = 402;
    int KEY_MESSAGE_CHOOSE_ORDER_TYPE = 403;
    int KEY_MESSAGE_ORDER_SENT = 404;

    interface View extends BaseView<Presenter> {

        void loadDefaultAddress(DeliveryAddress address);

        void loadLastNamePickup(String lastName);

        void onDeliveryChecked();

        void onPickupChecked();

        boolean validateInputForm();

        void navigateToActivity(Class activity, boolean orderSent);

        void startSelectAddressDialog(List<DeliveryAddress> addresses);

        void showToastMessage(int messageID);

        void dismissView();

    }

    interface Presenter extends BasePresenter {

        void onAddressSelectClicked();

        void onAddressSelected(DeliveryAddress address);

        void onAddressSaveClicked(DeliveryAddress address);

        void setDefaultAddress(DeliveryAddress address, DataHandler.Callback<Void> callback);

        void removeAddress(DeliveryAddress address);

        void confirmDelivery(DeliveryAddress address);

        void confirmPickup(String lastName);

    }

}
