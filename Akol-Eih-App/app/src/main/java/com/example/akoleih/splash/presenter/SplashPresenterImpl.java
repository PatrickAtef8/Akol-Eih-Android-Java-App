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
            view.navigateToHome();
        }
    }

