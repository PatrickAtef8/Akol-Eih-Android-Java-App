package com.example.playing_akoleh.auth.contract;

public interface LoginContract {
    interface View {
        void showLoading();
        void hideLoading();
        void onLoginSuccess();
        void onLoginError(String message);
    }


}
