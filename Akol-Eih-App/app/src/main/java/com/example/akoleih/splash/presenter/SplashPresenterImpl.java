package com.example.akoleih.splash.presenter;

import androidx.annotation.NonNull;

import com.example.akoleih.splash.view.SplashView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/////////*************donttttt forgeetttttttt to make repoooo for this  **********************////////////
public class SplashPresenterImpl implements SplashPresenter {
    private final SplashView splashView;

    public SplashPresenterImpl(SplashView view) {
        this.splashView = view;
    }

    @Override
    public void checkUserSession() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser refreshedUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (refreshedUser != null) {
                            splashView.navigateToHome();
                        } else {
                            FirebaseAuth.getInstance().signOut();
                            splashView.navigateToLogin();
                        }
                    } else {
                        FirebaseAuth.getInstance().signOut();
                        splashView.navigateToLogin();
                    }
                }
            });
        } else {
            splashView.navigateToLogin();
        }
    }
        }


