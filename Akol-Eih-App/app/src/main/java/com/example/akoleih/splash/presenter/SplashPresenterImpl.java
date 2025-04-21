package com.example.akoleih.splash.presenter;

import com.example.akoleih.splash.contract.SplashContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashPresenterImpl implements SplashPresenterInterface{
    private final SplashContract.View view;

    public SplashPresenterImpl(SplashContract.View view) {
        this.view = view;
    }

    @Override
    public void checkUserSession() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser refreshedUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (refreshedUser != null) {
                        view.navigateToHome();
                    } else {
                        FirebaseAuth.getInstance().signOut();
                        view.navigateToLogin();
                    }
                } else {
                    FirebaseAuth.getInstance().signOut();
                    view.navigateToLogin();
                }
            });
        } else {
            view.navigateToLogin();
        }
    }
        }


