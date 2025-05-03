package com.example.akoleih.auth.view;

public interface LoginView {
    void showLoading();
    void hideLoading();
    void onLoginSuccess();
    void onLoginError(String message);
}

