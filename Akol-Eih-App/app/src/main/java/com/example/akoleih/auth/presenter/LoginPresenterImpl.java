package com.example.akoleih.auth.presenter;

import com.example.akoleih.auth.model.AuthCallback;
import com.example.akoleih.auth.model.AuthRepository;
import com.example.akoleih.auth.view.LoginView;

public class LoginPresenterImpl implements LoginPresenter {

    private final LoginView loginView;
    private final AuthRepository authRepository;

    public LoginPresenterImpl(LoginView loginView, AuthRepository authRepository) {
        this.loginView = loginView;
        this.authRepository = authRepository;
    }

    @Override
    public void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            loginView.onLoginError("Email and password must not be empty.");
            return;
        }

        loginView.showLoading();
        authRepository.login(email, password, new AuthCallback() {
            @Override
            public void onSuccess() {
                loginView.hideLoading();
                loginView.onLoginSuccess();
            }

            @Override
            public void onFailure(String message) {
                loginView.hideLoading();
                loginView.onLoginError(message);
            }
        });
    }
}