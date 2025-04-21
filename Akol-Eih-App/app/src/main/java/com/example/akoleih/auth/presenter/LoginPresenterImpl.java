package com.example.akoleih.auth.presenter;

import com.example.akoleih.auth.contract.LoginContract;
import com.example.akoleih.auth.model.AuthCallback;
import com.example.akoleih.auth.model.AuthRepository;

public class LoginPresenterImpl implements LoginPresenter {

    private final LoginContract.View view;
    private final AuthRepository authRepository;

    public LoginPresenterImpl(LoginContract.View view, AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    @Override
    public void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            view.onLoginError("Email and password must not be empty.");
            return;
        }

        view.showLoading();
        authRepository.login(email, password, new AuthCallback() {
            @Override
            public void onSuccess() {
                view.hideLoading();
                view.onLoginSuccess();
            }

            @Override
            public void onFailure(String message) {
                view.hideLoading();
                view.onLoginError(message);
            }
        });
    }
}