package com.example.akoleih.auth.presenter;

import android.app.Activity;
import android.content.Intent;

public interface SignUpPresenter {
    void signUp(String email, String password);
    void signInWithGoogle(Activity activity);
    void handleGoogleSignInResult(Intent data);
}
