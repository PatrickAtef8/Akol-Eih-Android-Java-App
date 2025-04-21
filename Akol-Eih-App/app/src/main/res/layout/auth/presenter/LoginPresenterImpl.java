package com.example.akoleih.auth.presenter;

import com.example.akoleih.auth.contract.LoginContract;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPresenterImpl implements LoginPresenter {

    private final LoginContract.View view;
    private final FirebaseAuth auth;

    public LoginPresenterImpl(LoginContract.View view) {
        this.view = view;
        this.auth = FirebaseAuth.getInstance();
    }

    @Override
    public void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            view.onLoginError("Email and password must not be empty.");
            return;
        }

        view.showLoading();

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    view.hideLoading();
                    view.onLoginSuccess();
                })
                .addOnFailureListener(e -> {
                    view.hideLoading();
                    view.onLoginError(e.getMessage());
                });
    }
}
