package com.example.akoleih.auth.model;

public interface AuthRepository {


    void login(String email, String password, AuthCallback callback);
    void signUp(String email, String password, AuthCallback callback);
}