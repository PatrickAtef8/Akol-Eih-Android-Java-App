package com.example.akoleih.auth.presenter;

import com.example.akoleih.auth.contract.SignUpContract;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpPresenterImpl implements SignUpPresenter {

    private final SignUpContract.View view;
    private final FirebaseAuth auth;

    public SignUpPresenterImpl(SignUpContract.View view) {
        this.view = view;
        this.auth = FirebaseAuth.getInstance();
    }

    @Override
    public void signUp(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            view.onSignUpError("Email and password cannot be empty.");
            return;
        }
        view.showLoading();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    view.hideLoading();
                    view.onSignUpSuccess();
                })
                .addOnFailureListener(e -> {
                    view.hideLoading();
                    view.onSignUpError(e.getMessage());
                });
    }
}
