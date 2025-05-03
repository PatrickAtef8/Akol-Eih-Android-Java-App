package com.example.akoleih.auth.view;

public interface SignUpView {
    void showLoading();
    void hideLoading();
    void onSignUpSuccess();
    void onSignUpError(String message);
}
