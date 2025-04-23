package com.example.akoleih.auth.model;

import android.app.Activity;
import android.content.Intent;

public interface AuthRepository {
    void login(String email, String password, AuthCallback callback);
    void signUp(String email, String password, AuthCallback callback);
    void signInWithGoogle(Activity activity, AuthCallback callback);
    void handleGoogleSignInResult(Intent data, AuthCallback callback);
}