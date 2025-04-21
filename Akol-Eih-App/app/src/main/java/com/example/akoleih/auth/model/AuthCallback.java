package com.example.akoleih.auth.model;

public interface AuthCallback {
    void onSuccess();
    void onFailure(String message);
}