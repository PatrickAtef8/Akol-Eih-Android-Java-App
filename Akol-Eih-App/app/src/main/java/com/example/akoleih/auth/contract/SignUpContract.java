package com.example.akoleih.auth.contract;

import android.app.Activity;
import android.content.Intent;

public interface SignUpContract {
    interface View {
        void showLoading();
        void hideLoading();
        void onSignUpSuccess();
        void onSignUpError(String message);
    }


}