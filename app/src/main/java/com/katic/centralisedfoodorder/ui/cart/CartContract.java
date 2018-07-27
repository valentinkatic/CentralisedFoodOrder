package com.katic.centralisedfoodorder.ui.cart;

import com.katic.centralisedfoodorder.data.models.Cart;
import com.katic.centralisedfoodorder.data.models.CartItem;
import com.katic.centralisedfoodorder.ui.BasePresenter;
import com.katic.centralisedfoodorder.ui.BaseView;

public interface CartContract {

    interface View extends BaseView<Presenter> {

        void loadCart(Cart cart);

        void onError();

        void showEmptyCart();

        void updateSubtotal(double value);

        void navigateToActivity(Class activity);

        void dismissView();

    }

    interface Presenter extends BasePresenter {

        void onCheckoutPressed();

        void onOrderHistoryPressed();

        void updateCartItemAmount(CartItem cartItem);

        void removeCartItem(CartItem cartItem);

    }

}
