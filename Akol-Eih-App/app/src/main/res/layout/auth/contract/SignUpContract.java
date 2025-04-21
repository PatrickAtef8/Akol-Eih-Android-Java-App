package com.example.playing_akoleh.auth.contract;

public interface SignUpContract {
    interface View {
        void showLoading();
        void hideLoading();
        void onSignUpSuccess();
        void onSignUpError(String message);
    }


}
