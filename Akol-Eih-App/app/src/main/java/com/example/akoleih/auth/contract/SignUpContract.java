package com.example.akoleih.auth.contract;

public interface SignUpContract {
    interface View {
        void showLoading();
        void hideLoading();
        void onSignUpSuccess();
        void onSignUpError(String message);
    }


}
