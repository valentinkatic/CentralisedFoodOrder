package com.katic.centralisedfoodorder.ui.orderhistory;

import com.katic.centralisedfoodorder.data.models.Cart;
import com.katic.centralisedfoodorder.ui.BasePresenter;
import com.katic.centralisedfoodorder.ui.BaseView;

import java.util.List;

public interface OrderHistoryContract {

    interface View extends BaseView<Presenter> {

        void loadOrderHistory(List<Cart> orderHistory);

        void handleEmptyView();

        void navigateToActivity(Class activity);

        void updateCartIcon(int size);

        void dismissView();

    }

    interface Presenter extends BasePresenter {

        void onCartClicked();

        void removeOrder(Cart order);

        void addToCart(Cart order);

    }

}
