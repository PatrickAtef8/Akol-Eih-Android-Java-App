package com.example.akoleih.auth.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

public class AuthRepositoryImpl implements AuthRepository {
    private final FirebaseAuth auth;

    public AuthRepositoryImpl() {
        this.auth = FirebaseAuth.getInstance();
    }

    @Override
    public void login(String email, String password, final AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    @Override
    public void signUp(String email, String password, final AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }
}