package com.example.akoleih.auth.model.callbacks;

public interface AuthCallback {
    void onSuccess();
    void onFailure(String message);
}