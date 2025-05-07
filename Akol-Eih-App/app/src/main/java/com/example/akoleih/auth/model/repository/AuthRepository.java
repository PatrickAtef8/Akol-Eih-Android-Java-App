package com.example.akoleih.auth.model.repository;

import android.app.Activity;
import android.content.Intent;

import com.example.akoleih.auth.model.callbacks.AuthCallback;

public interface AuthRepository {
    void login(String email, String password, AuthCallback callback);
    void signUp(String email, String password, AuthCallback callback);
    void signInWithGoogle(Activity activity, AuthCallback callback);
    void handleGoogleSignInResult(Intent data, AuthCallback callback);
    void setGuestMode(AuthCallback callback);
    void logout();
}