package com.katic.centralisedfoodorder.ui.signin;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.katic.centralisedfoodorder.data.DataHandler;
import com.katic.centralisedfoodorder.data.DataHandlerProvider;

public class SignInPresenter implements SignInContract.Presenter {

    private SignInContract.View mView;
    private DataHandler mDataHandler;

    public SignInPresenter(SignInContract.View view) {
        this.mView = view;
        this.mDataHandler = DataHandlerProvider.provide();
        view.setPresenter(this);
    }

    @Override
    public void handleLoginRequest() {
        mView.showLoading();
        mView.startSignIn();
    }

    @Override
    public void handleLoginSuccess(String email, String displayName) {
        mDataHandler.saveUserEmail(email);
        mDataHandler.saveUserName(displayName);
        mDataHandler.setUserInfo(new DataHandler.Callback<Void>() {
            @Override
            public void onResponse(Void result) {
                mView.hideLoading();
                mView.loginSuccess();
            }

            @Override
            public void onError() {
                mView.hideLoading();
            }
        });
    }

    @Override
    public void handleLoginFailure(int statusCode, String message) {
        mView.hideLoading();
        mView.loginFailure(statusCode, message);
    }

    @Override
    public void start(@Nullable Bundle extras) {
        // Do nothing on start
    }

    @Override
    public void destroy() {
        this.mView = null;
    }
}
