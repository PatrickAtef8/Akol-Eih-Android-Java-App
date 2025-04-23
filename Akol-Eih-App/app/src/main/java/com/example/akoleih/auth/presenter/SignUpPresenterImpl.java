package com.example.akoleih.auth.presenter;

import android.app.Activity;
import android.content.Intent;

import com.example.akoleih.auth.contract.SignUpContract;
import com.example.akoleih.auth.model.AuthCallback;
import com.example.akoleih.auth.model.AuthRepository;

public class SignUpPresenterImpl implements SignUpPresenter {
    private static final int RC_GOOGLE_SIGN_IN = 123;
    private final SignUpContract.View view;
    private final AuthRepository authRepository;

    public SignUpPresenterImpl(SignUpContract.View view, AuthRepository authRepository) {
        this.view = view;
        this.authRepository = authRepository;
    }

    @Override
    public void signUp(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            view.onSignUpError("Email and password cannot be empty.");
            return;
        }

        view.showLoading();
        authRepository.signUp(email, password, new AuthCallback() {
            @Override
            public void onSuccess() {
                view.hideLoading();
                view.onSignUpSuccess();
            }

            @Override
            public void onFailure(String message) {
                view.hideLoading();
                view.onSignUpError(message);
            }
        });
    }

    @Override
    public void signInWithGoogle(Activity activity) {
        view.showLoading();
        authRepository.signInWithGoogle(activity, new AuthCallback() {
            @Override
            public void onSuccess() {
                view.hideLoading();
                view.onSignUpSuccess();
            }

            @Override
            public void onFailure(String message) {
                view.hideLoading();
                view.onSignUpError(message);
            }
        });
    }

    @Override
    public void handleGoogleSignInResult(Intent data) {
        authRepository.handleGoogleSignInResult(data, new AuthCallback() {
            @Override
            public void onSuccess() {
                view.hideLoading();
                view.onSignUpSuccess();
            }

            @Override
            public void onFailure(String message) {
                view.hideLoading();
                view.onSignUpError(message);
            }
        });
    }
}