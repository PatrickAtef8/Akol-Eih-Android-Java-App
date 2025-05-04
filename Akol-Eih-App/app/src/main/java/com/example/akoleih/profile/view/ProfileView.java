package com.example.akoleih.profile.view;

public interface ProfileView {
    void showUserName(String name);
    void showUserEmail(String email);
    void showProfilePicture(String url);
    void showMessage(String message);
    void showError(String error);
    void navigateToLogin();
}