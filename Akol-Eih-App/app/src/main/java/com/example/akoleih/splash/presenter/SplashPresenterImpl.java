package com.example.akoleih.splash.presenter;

import androidx.annotation.NonNull;

import com.example.akoleih.splash.view.SplashView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashPresenterImpl implements SplashPresenter {
    private SplashView splashView;
    private final FirebaseAuth auth;

    public SplashPresenterImpl() {
        this.auth = FirebaseAuth.getInstance();
    }

    @Override
    public void checkUserSession() {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            if (isUserValid(user)) {
                splashView.navigateToHome();
            } else {
                handleInvalidUser();
            }
        } else {
            splashView.navigateToLogin();
        }
    }

    private boolean isUserValid(FirebaseUser user) {
        return user.getEmail() != null && !user.isAnonymous();
    }

    private void handleInvalidUser() {
        auth.signOut();
        splashView.navigateToLogin();
    }

    @Override
    public void setView(SplashView view) {
        this.splashView = view;
    }

    @Override
    public void onDestroy() {
        splashView = null;
    }
}