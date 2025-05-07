package com.example.akoleih.auth.presenter;

import android.app.Activity;
import android.content.Intent;

import com.example.akoleih.auth.model.callbacks.AuthCallback;
import com.example.akoleih.auth.model.repository.AuthRepository;
import com.example.akoleih.auth.view.SignUpView;

public class SignUpPresenterImpl implements SignUpPresenter {
    private static final int RC_GOOGLE_SIGN_IN = 123;
    private final SignUpView view;
    private final AuthRepository authRepository;

    public SignUpPresenterImpl(SignUpView signUpView, AuthRepository authRepository) {
        this.view = signUpView;
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