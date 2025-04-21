package com.example.akoleih.auth.presenter;

import com.example.akoleih.auth.contract.SignUpContract;
import com.example.akoleih.auth.model.AuthCallback;
import com.example.akoleih.auth.model.AuthRepository;

public class SignUpPresenterImpl implements SignUpPresenter {

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
}